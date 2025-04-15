package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectSeats(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            Model model,
            HttpSession session) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        List<Seat> departSeats = seatService.getAvailableSeatsForFlight(departFlight);

        @SuppressWarnings("unchecked")
        List<Long> selectedDepartSeats = (List<Long>) session.getAttribute("tempDepartSeatIds_" + bookingId);
        if (selectedDepartSeats == null) {
            selectedDepartSeats = new ArrayList<>();
            for (Seat seat : booking.getSeats()) {
                if (seat.getFlight().getId().equals(departFlight.getId())) {
                    selectedDepartSeats.add(seat.getId());
                }
            }
        }

        List<Seat> returnSeats = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Long> selectedReturnSeats = (List<Long>) session.getAttribute("tempReturnSeatIds_" + bookingId);
        if (selectedReturnSeats == null) {
            selectedReturnSeats = new ArrayList<>();
            if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
                for (Seat seat : booking.getSeats()) {
                    if (seat.getFlight().getId().equals(booking.getFlights().get(1).getId())) {
                        selectedReturnSeats.add(seat.getId());
                    }
                }
            }
        }

        String passengerName = booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger";

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                Flight returnFlight = returnFlightOpt.get();
                returnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                returnSeats.forEach(seat -> seat.setFlight(returnFlight));
            } else {
                model.addAttribute("warning", "Return flight not found.");
            }
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("departSeats", departSeats);
        model.addAttribute("selectedDepartSeats", selectedDepartSeats);
        model.addAttribute("returnSeats", returnSeats);
        model.addAttribute("selectedReturnSeats", selectedReturnSeats);
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", passengerName);

        return "option/seat";
    }

    @PostMapping("/save")
    @Transactional
    public String saveSeats(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            @RequestParam(required = false) List<Long> departSeatIds,
            @RequestParam(required = false) List<Long> returnSeatIds,
            Model model,
            HttpSession session) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();
        TicketClass ticketClass = departFlight.getTicketClass();

        if (ticketClass == null) {
            model.addAttribute("error", "Ticket class not found for the departure flight.");
            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
        }

        try {
            int numberOfPassengers = booking.getPassengerCount();
            if (numberOfPassengers <= 0) {
                model.addAttribute("error", "Invalid number of passengers for this booking.");
                return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
            }

            // Xóa các ghế miễn phí cũ của chuyến đi khởi hành
            List<Seat> seatsToRemove = new ArrayList<>();
            for (Seat seat : booking.getSeats()) {
                if (seat.getFlight().getId().equals(departFlight.getId())) {
                    seat.setStatus(Seat.SeatStatus.AVAILABLE);
                    seat.setBooking(null);
                    seatsToRemove.add(seat);
                }
            }
            booking.getSeats().removeAll(seatsToRemove);

            double additionalSeatCost = 0.0;
            Seat.SeatClass freeSeatClass = ticketClass.getFreeSeatClass();

            if (departSeatIds != null && !departSeatIds.isEmpty()) {
                if (departSeatIds.size() != numberOfPassengers) {
                    model.addAttribute("error", "You must select exactly " + numberOfPassengers + " seats for the departure trip.");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                for (Long seatId : departSeatIds) {
                    Optional<Seat> seatOpt = seatService.findById(seatId);
                    if (seatOpt.isEmpty()) {
                        model.addAttribute("error", "Seat with ID " + seatId + " not found.");
                        return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                    }
                    Seat seat = seatOpt.get();
                    if (!seatService.isSeatAvailable(seat, departFlight)) {
                        model.addAttribute("error", "Seat with ID " + seatId + " is no longer available.");
                        return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                    }
                    if (seat.getSeatClass() != freeSeatClass) {
                        additionalSeatCost += seat.getPrice();
                    }
                    // Truyền thêm Flight vào assignSeatToBooking
                    seatService.assignSeatToBooking(booking, seat, departFlight);
                }
            }

            // Xử lý chuyến về nếu có
            if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
                Long returnFlightId = booking.getFlights().get(1).getId();
                Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
                if (returnFlightOpt.isEmpty()) {
                    model.addAttribute("error", "Return flight not found.");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                Flight returnFlight = returnFlightOpt.get();
                TicketClass returnTicketClass = returnFlight.getTicketClass();
                if (returnTicketClass == null) {
                    model.addAttribute("error", "Ticket class not found for the return flight.");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                // Xóa các ghế miễn phí cũ của chuyến về
                seatsToRemove.clear();
                for (Seat seat : booking.getSeats()) {
                    if (seat.getFlight().getId().equals(returnFlight.getId())) {
                        seat.setStatus(Seat.SeatStatus.AVAILABLE);
                        seat.setBooking(null);
                        seatsToRemove.add(seat);
                    }
                }
                booking.getSeats().removeAll(seatsToRemove);

                Seat.SeatClass returnFreeSeatClass = returnTicketClass.getFreeSeatClass();

                if (returnSeatIds != null && !returnSeatIds.isEmpty()) {
                    if (returnSeatIds.size() != numberOfPassengers) {
                        model.addAttribute("error", "You must select exactly " + numberOfPassengers + " seats for the return trip.");
                        return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                    }

                    for (Long seatId : returnSeatIds) {
                        Optional<Seat> seatOpt = seatService.findById(seatId);
                        if (seatOpt.isEmpty()) {
                            model.addAttribute("error", "Seat with ID " + seatId + " not found.");
                            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                        }
                        Seat seat = seatOpt.get();
                        if (!seatService.isSeatAvailable(seat, returnFlight)) {
                            model.addAttribute("error", "Seat with ID " + seatId + " is no longer available.");
                            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                        }
                        if (seat.getSeatClass() != returnFreeSeatClass) {
                            additionalSeatCost += seat.getPrice();
                        }
                        // Truyền thêm Flight vào assignSeatToBooking
                        seatService.assignSeatToBooking(booking, seat, returnFlight);
                    }
                }
            }

            // Cập nhật chi phí bổ sung
            booking.setAdditionalCost(booking.getAdditionalCost() + additionalSeatCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            session.removeAttribute("tempDepartSeatIds_" + bookingId);
            session.removeAttribute("tempReturnSeatIds_" + bookingId);

            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
        }
    }

    @PostMapping("/autosave")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> autoSaveSeats(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            List<Long> departSeatIds = payload.get("departSeatIds") != null
                    ? (List<Long>) payload.get("departSeatIds")
                    : new ArrayList<>();
            List<Long> returnSeatIds = payload.get("returnSeatIds") != null
                    ? (List<Long>) payload.get("returnSeatIds")
                    : new ArrayList<>();

            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }

            Booking booking = bookingOpt.get();
            int numberOfPassengers = booking.getPassengerCount();
            if (numberOfPassengers <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid number of passengers for this booking.");
            }

            if (departSeatIds.size() != numberOfPassengers) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("You must select exactly " + numberOfPassengers + " seats for the departure trip.");
            }
            if (returnSeatIds.size() != 0 && returnSeatIds.size() != numberOfPassengers) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("You must select exactly " + numberOfPassengers + " seats for the return trip.");
            }

            // Validate seat availability for departure seats
            if (!departSeatIds.isEmpty()) {
                Optional<Flight> flightOpt = flightRepository.findById(booking.getFlights().get(0).getId());
                if (flightOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Departure flight not found.");
                }
                Flight departFlight = flightOpt.get();
                List<Seat> availableDepartSeats = seatService.getAvailableSeatsForFlight(departFlight);
                for (Long seatId : departSeatIds) {
                    Optional<Seat> seatOpt = seatService.findById(seatId);
                    if (seatOpt.isEmpty() || !availableDepartSeats.contains(seatOpt.get())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Seat with ID " + seatId + " is not available for the departure flight.");
                    }
                }
            }

            // Validate seat availability for return seats
            if (!returnSeatIds.isEmpty() && booking.getFlights().size() > 1) {
                Optional<Flight> returnFlightOpt = flightRepository.findById(booking.getFlights().get(1).getId());
                if (returnFlightOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Return flight not found.");
                }
                Flight returnFlight = returnFlightOpt.get();
                List<Seat> availableReturnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                for (Long seatId : returnSeatIds) {
                    Optional<Seat> seatOpt = seatService.findById(seatId);
                    if (seatOpt.isEmpty() || !availableReturnSeats.contains(seatOpt.get())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Seat with ID " + seatId + " is not available for the return flight.");
                    }
                }
            }

            // Save temporarily to session
            session.setAttribute("tempDepartSeatIds_" + bookingId, departSeatIds);
            session.setAttribute("tempReturnSeatIds_" + bookingId, returnSeatIds);

            return ResponseEntity.ok("Auto-saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Auto-save failed: " + e.getMessage());
        }
    }

    private String prepareSeatSelectionModel(
            Model model,
            Booking booking,
            Flight departFlight,
            String applyToReturnTrip,
            List<Long> departSeatIds,
            List<Long> returnSeatIds) {
        List<Seat> departSeats = seatService.getAvailableSeatsForFlight(departFlight);
        List<Seat> returnSeats = new ArrayList<>();

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                Flight returnFlight = returnFlightOpt.get();
                returnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                returnSeats.forEach(seat -> seat.setFlight(returnFlight));
            }
        }

        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("departSeats", departSeats);
        model.addAttribute("selectedDepartSeats", departSeatIds != null ? departSeatIds : new ArrayList<>());
        model.addAttribute("returnSeats", returnSeats);
        model.addAttribute("selectedReturnSeats", returnSeatIds != null ? returnSeatIds : new ArrayList<>());
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger");

        return "option/seat";
    }

    @PostMapping("/save-ajax")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveSeatAjax(
            @RequestBody Map<String, Object> payload) {
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long seatId = Long.valueOf(payload.get("seatId").toString());

            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            Optional<Seat> seatOpt = seatService.findById(seatId);

            if (bookingOpt.isEmpty() || seatOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Booking booking = bookingOpt.get();
            Seat seat = seatOpt.get();

            // Giả định ghế chỉ áp dụng cho chuyến đi đầu tiên (departFlight)
            Flight flight = booking.getFlights().get(0); // Cần điều chỉnh nếu hỗ trợ nhiều chuyến

            // Xóa các ghế cũ
            for (Seat oldSeat : new ArrayList<>(booking.getSeats())) {
                seatService.releaseSeat(booking, oldSeat);
            }

            // Gán ghế mới với flight tương ứng
            seatService.assignSeatToBooking(booking, seat, flight);

            double seatCost = seat.getPrice() > 0 ? seat.getPrice() : 0;
            booking.setAdditionalCost(booking.getAdditionalCost() + seatCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("additionalCost", booking.getAdditionalCost());
            response.put("total", booking.getFlights().get(0).getPrice() + (booking.getFlights().get(0).getPrice() * 0.2) + booking.getAdditionalCost());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}