package SE2.flightBooking.controller;

import SE2.flightBooking.dto.request.InitPaymentRequest;
import SE2.flightBooking.dto.response.InitPaymentResponse;
import SE2.flightBooking.service.paymentService.FlightService;
import SE2.flightBooking.service.paymentService.PaymentService;
import SE2.flightBooking.service.paymentService.VNPayService;
import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FlightService bookingPriceService;

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);



    // Trang chủ tại /flight
    @GetMapping("/")
    public String home(Model model) {
        List<String> departures = flightRepository.findAll()
                .stream()
                .map(Flight::getDeparture)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> destinations = flightRepository.findAll()
                .stream()
                .map(Flight::getDestination)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("departures", departures);
        model.addAttribute("destinations", destinations);

        return "home";
    }

    @GetMapping("/search")
    public String searchFlights(@RequestParam(required = false) String ticketType,
                                @RequestParam(required = false) String departure,
                                @RequestParam(required = false) String destination,
                                @RequestParam(required = false) String departureTime,
                                @RequestParam(required = false) String returnTime,
                                @RequestParam(required = false) Integer passengers,
                                HttpSession session,
                                Model model) {
        // Reset booked lists after returning home.
        session.removeAttribute("departureBookedIds");
        session.removeAttribute("returnBookedIds");
        session.setAttribute("destination", destination); // Lưu destination cho /book

        // Check condition
        if (ticketType == null || departure == null || destination == null || passengers == null || departureTime == null) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin tìm kiếm.");
            return "home";
        }

        if (departure.equals(destination)) {
            model.addAttribute("error", "Điểm đi và điểm đến không được trùng nhau.");
            return "home";
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate selectedDepartureDate = departureTime != null ? LocalDate.parse(departureTime, dateFormatter) : null;
        LocalDate selectedReturnDate = (returnTime != null && !returnTime.isEmpty())
                ? LocalDate.parse(returnTime, dateFormatter)
                : null;

        // Adjust passenger with type "round-trip".
        int adjustedPassengers = passengers != null ? passengers : 1;
        if ("round-trip".equals(ticketType)) {
            adjustedPassengers *= 2;
        }

        // Get list of departure of flight.
        List<Flight> departureFlights = flightRepository.findFlightsByDepartureDestinationAndType(
                departure, destination, Flight.FlightType.ONE_WAY, passengers);
        Map<String, List<Flight>> departureFlightsByTimeFrame = new HashMap<>();
        Set<Long> processedFlightIds = new HashSet<>();

        for (Flight flight : departureFlights) {
            if (!processedFlightIds.add(flight.getId())) continue;
            LocalTime depTimeOnly = flight.getDepartureTime().toLocalTime();
            LocalDateTime newDepTime = selectedDepartureDate != null ? selectedDepartureDate.atTime(depTimeOnly) : flight.getDepartureTime();
            String depTimeFrame = getTimeFrame(depTimeOnly);

            Flight adjustedFlight = new Flight();
            adjustedFlight.setId(flight.getId());
            adjustedFlight.setAirline(flight.getAirline());
            adjustedFlight.setDeparture(flight.getDeparture());
            adjustedFlight.setDestination(flight.getDestination());
            adjustedFlight.setDepartureTime(newDepTime);
            adjustedFlight.setPrice(flight.getPrice());
            adjustedFlight.setAvailableSeats(flight.getAvailableSeats());
            adjustedFlight.setFlightType(flight.getFlightType());

            departureFlightsByTimeFrame.computeIfAbsent(depTimeFrame, k -> new ArrayList<>()).add(adjustedFlight);
        }

        // Kiểm tra nếu không có chuyến đi
        if (departureFlightsByTimeFrame.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy chuyến bay nào từ " + departure + " đến " + destination + " vào ngày " + departureTime);
            return "home";
        }

        // Lấy danh sách chuyến về nếu là round-trip
        Map<String, List<Flight>> returnFlightsByTimeFrame = new HashMap<>();
        if ("round-trip".equals(ticketType)) {
            List<Flight> returnFlights = flightRepository.findFlightsByDepartureDestinationAndType(
                    destination, departure, Flight.FlightType.ONE_WAY, passengers);
            processedFlightIds.clear();

            for (Flight flight : returnFlights) {
                if (!processedFlightIds.add(flight.getId())) continue;
                LocalTime retTimeOnly = flight.getDepartureTime().toLocalTime();
                LocalDateTime newRetTime = selectedReturnDate != null ? selectedReturnDate.atTime(retTimeOnly) : flight.getDepartureTime();
                String retTimeFrame = getTimeFrame(retTimeOnly);

                Flight adjustedFlight = new Flight();
                adjustedFlight.setId(flight.getId());
                adjustedFlight.setAirline(flight.getAirline());
                adjustedFlight.setDeparture(flight.getDeparture());
                adjustedFlight.setDestination(flight.getDestination());
                adjustedFlight.setDepartureTime(newRetTime);
                adjustedFlight.setPrice(flight.getPrice());
                adjustedFlight.setAvailableSeats(flight.getAvailableSeats());
                adjustedFlight.setFlightType(flight.getFlightType());

                returnFlightsByTimeFrame.computeIfAbsent(retTimeFrame, k -> new ArrayList<>()).add(adjustedFlight);
            }

            // Check if not round-trip.
            if (returnFlightsByTimeFrame.isEmpty()) {
                model.addAttribute("error", "No round-trip flight from" + destination + " to " + departure + " in " + returnTime);
                return "home";
            }
        }

        model.addAttribute("departureFlightsByTimeFrame", departureFlightsByTimeFrame);
        model.addAttribute("returnFlightsByTimeFrame", returnFlightsByTimeFrame);
        model.addAttribute("departure", departure);
        model.addAttribute("destination", destination);
        model.addAttribute("ticketType", ticketType);
        model.addAttribute("passengers", passengers);
        model.addAttribute("adjustedPassengers", adjustedPassengers);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("returnTime", returnTime);

        return "searchFlight";
    }

    @PostMapping("/book")
    @ResponseBody
    public Map<String, Object> bookFlight(@RequestParam Long flightId,
                                          @RequestParam String departureTime,
                                          @RequestParam(required = false) String returnTime,
                                          @RequestParam Integer passengers,
                                          @RequestParam(required = false) String ticketType,
                                          @SessionAttribute(required = false) List<Long> departureBookedIds,
                                          @SessionAttribute(required = false) List<Long> returnBookedIds,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Create list if not exist.
        if (departureBookedIds == null) departureBookedIds = new ArrayList<>();
        if (returnBookedIds == null && "round-trip".equals(ticketType)) returnBookedIds = new ArrayList<>();

        // Calculate the number of ticket that selected.
        int requiredBookings = "round-trip".equals(ticketType) ? passengers * 2 : passengers;
        int currentBookings = departureBookedIds.size() + (returnBookedIds != null ? returnBookedIds.size() : 0);

        if (currentBookings >= requiredBookings) {
            response.put("success", false);
            response.put("error", "You selected enough!" + requiredBookings + " ticket. Can't choose anymore!");
            return response;
        }

        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "Flight is unvailable!");
            return response;
        }

        Flight flight = flightOpt.get();
        if (flight.getAvailableSeats() <= 0) {
            response.put("success", false);
            response.put("error", "Unavailable seats on this flight!");
            return response;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate selectedDepartureDate = LocalDate.parse(departureTime, dateFormatter);
        LocalDateTime newDepartureTime = selectedDepartureDate.atTime(flight.getDepartureTime().toLocalTime());

        flight.setDepartureTime(newDepartureTime);
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flightRepository.save(flight);

        // Classify outgoing and return tickets. (Departure ticket/ Return ticket)
        if ("round-trip".equals(ticketType) && returnTime != null && flight.getDeparture().equals(session.getAttribute("destination"))) {
            returnBookedIds.add(flightId);
            session.setAttribute("returnBookedIds", returnBookedIds);
        } else {
            departureBookedIds.add(flightId);
            session.setAttribute("departureBookedIds", departureBookedIds);
        }

        response.put("success", true);
        response.put("airline", flight.getAirline());
        response.put("departureTime", newDepartureTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        response.put("arrivalTime", newDepartureTime.plusHours(2).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        response.put("price", flight.getPrice());
        response.put("bookedCount", departureBookedIds.size() + (returnBookedIds != null ? returnBookedIds.size() : 0));
        response.put("totalPrice", (departureBookedIds.size() + (returnBookedIds != null ? returnBookedIds.size() : 0)) * flight.getPrice());
        response.put("flightId", flightId);

        return response;
    }

    @PostMapping("/cancel")
    @ResponseBody
    public Map<String, Object> cancelFlight(@RequestParam Long flightId,
                                            @SessionAttribute(required = false) List<Long> departureBookedIds,
                                            @SessionAttribute(required = false) List<Long> returnBookedIds,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (departureBookedIds == null) departureBookedIds = new ArrayList<>();
        if (returnBookedIds == null) returnBookedIds = new ArrayList<>();

        if (!departureBookedIds.contains(flightId) && !returnBookedIds.contains(flightId)) {
            response.put("success", false);
            response.put("error", "This flight hss not been booked or not exist in the list.");
            return response;
        }

        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "The flight is not exist!");
            return response;
        }

        Flight flight = flightOpt.get();
        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        flightRepository.save(flight);

        if (departureBookedIds.contains(flightId)) {
            departureBookedIds.remove(flightId);
            session.setAttribute("departureBookedIds", departureBookedIds);
        } else if (returnBookedIds.contains(flightId)) {
            returnBookedIds.remove(flightId);
            session.setAttribute("returnBookedIds", returnBookedIds);
        }

        response.put("success", true);
        response.put("bookedCount", departureBookedIds.size() + returnBookedIds.size());
        response.put("totalPrice", (departureBookedIds.size() + returnBookedIds.size()) * flight.getPrice());
        response.put("message", "Canceled flight successfully!");
        response.put("flightId", flightId);

        return response;
    }

    @PostMapping("/next")
    @ResponseBody
    public InitPaymentResponse proceedToPayment(@RequestParam(required = false) Long departureBookedId,
                                                @RequestParam(required = false) Long returnBookedId,
                                                @RequestParam(name = "passengers", required = false) Integer passengers,
                                                @RequestParam String ticketType) {

        boolean isRoundTrip = "round-trip".equals(ticketType);
        int requiredBookings = isRoundTrip ? 2 : 1;
        int currentBookings = 0;

        if (departureBookedId != null) currentBookings++;
        if (returnBookedId != null) currentBookings++;

        if (currentBookings < requiredBookings) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You should select " + requiredBookings + " ticket(s). Right now it's just " +
                            currentBookings + " ticket(s) selected.");
        }

        Booking booking = new Booking();
        booking.setPassengerCount(passengers);


        Flight departureFlight = departureBookedId == null ? null :
                flightRepository.findById(departureBookedId).orElse(null);

        Flight returnFlight = returnBookedId == null ? null :
                flightRepository.findById(returnBookedId).orElse(null);

        bookingPriceService.calculateTotalPrice(booking, departureFlight, returnFlight);

        InitPaymentRequest initPaymentRequest = InitPaymentRequest.builder()
                .userId(booking.getId())
                .amount((long) booking.getTotalPrice())
                .txnRef(String.valueOf(booking.getBookingCode()))
                .ipAddress("127.0.0.1")
                .build();

        InitPaymentResponse initPaymentResponse = paymentService.init(initPaymentRequest);

        log.info("[request_id={}] user_name={} created booking_id={} successfully",
                "reservationid", booking.getPassengerName(), booking.getId());

        return initPaymentResponse;
    }



    private String getTimeFrame(LocalTime time) {
        int hour = time.getHour();
        if (hour < 8) return "Early Morning (00:00 - 07:59)";
        if (hour < 12) return "Morning (08:00 - 11:59)";
        if (hour < 18) return "Afternoon (12:00 - 17:59)";
        return "Evening (18:00 - 23:59)";
    }
}