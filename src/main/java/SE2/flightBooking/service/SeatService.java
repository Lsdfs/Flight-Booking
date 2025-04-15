package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    @Autowired
    private SeatRepository seatRepository;

    // Kiểm tra ghế có sẵn không với tham số Flight rõ ràng
    public boolean isSeatAvailable(Seat seat, Flight flight) {
        if (seat == null || flight == null) {
            logger.warn("Seat or flight is null in isSeatAvailable check.");
            return false;
        }
        boolean isAvailable = seat.getFlight() != null &&
                seat.getFlight().getId().equals(flight.getId()) &&
                seat.getStatus() == Seat.SeatStatus.AVAILABLE &&
                seat.getBooking() == null;
        if (!isAvailable) {
            logger.debug("Seat {} is not available for flight {}. Status: {}, Booking: {}",
                    seat.getId(), flight.getId(), seat.getStatus(), seat.getBooking());
        }
        return isAvailable;
    }

    // Lấy danh sách ghế khả dụng cho chuyến bay
    public List<Seat> getAvailableSeatsForFlight(Flight flight) {
        if (flight == null) {
            logger.error("Flight is null in getAvailableSeatsForFlight.");
            throw new IllegalArgumentException("Flight cannot be null.");
        }
        List<Seat> seats = seatRepository.findByFlightAndStatus(flight, Seat.SeatStatus.AVAILABLE);
        logger.debug("Found {} available seats for flight {}.", seats.size(), flight.getId());
        return seats.stream()
                .filter(seat -> seat.getBooking() == null) // Đảm bảo không có booking
                .collect(Collectors.toList());
    }

    // Lấy danh sách ghế khả dụng theo loại ghế và chuyến bay
    public List<Seat> getAvailableSeatsForFlightAndClass(Flight flight, Seat.SeatClass seatClass) {
        if (flight == null || seatClass == null) {
            logger.error("Flight or seatClass is null in getAvailableSeatsForFlightAndClass.");
            throw new IllegalArgumentException("Flight or seatClass cannot be null.");
        }
        List<Seat> seats = seatRepository.findByFlightAndSeatClassAndStatus(flight, seatClass, Seat.SeatStatus.AVAILABLE);
        logger.debug("Found {} available seats of class {} for flight {}.", seats.size(), seatClass, flight.getId());
        return seats.stream()
                .filter(seat -> seat.getBooking() == null)
                .collect(Collectors.toList());
    }

    // Gán ghế cho booking
    @Transactional
    public void assignSeatToBooking(Booking booking, Seat seat, Flight flight) {
        if (booking == null || seat == null || flight == null) {
            logger.error("Booking, seat, or flight is null in assignSeatToBooking.");
            throw new IllegalArgumentException("Booking, seat, or flight cannot be null.");
        }

        // Kiểm tra xem flight có thuộc booking không
        if (!booking.getFlights().stream().anyMatch(f -> f.getId().equals(flight.getId()))) {
            logger.error("Flight {} does not belong to booking {}.", flight.getId(), booking.getId());
            throw new IllegalStateException("Flight does not belong to this booking.");
        }

        // Kiểm tra tính khả dụng của ghế
        if (!isSeatAvailable(seat, flight)) {
            logger.warn("Seat {} is not available for flight {} in booking {}.",
                    seat.getId(), flight.getId(), booking.getId());
            throw new IllegalStateException("Seat is not available.");
        }

        try {
            seat.setStatus(Seat.SeatStatus.SELECTING); // Sử dụng BOOKED thay vì OCCUPIED cho đồng bộ
            seat.setBooking(booking);
            seat.setFlight(flight); // Đảm bảo ghế liên kết với chuyến bay đúng
            seatRepository.save(seat);
            booking.addSeat(seat); // Sử dụng phương thức tiện ích từ Booking
            logger.info("Seat {} assigned to booking {} for flight {} successfully.",
                    seat.getId(), booking.getId(), flight.getId());
        } catch (Exception e) {
            logger.error("Failed to assign seat {} to booking {}: {}", seat.getId(), booking.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to assign seat: " + e.getMessage());
        }
    }

    // Giải phóng ghế
    @Transactional
    public void releaseSeat(Booking booking, Seat seat) {
        if (booking == null || seat == null) {
            logger.error("Booking or seat is null in releaseSeat.");
            throw new IllegalArgumentException("Booking or seat cannot be null.");
        }

        // Kiểm tra xem ghế có thực sự thuộc booking không
        if (seat.getStatus() != Seat.SeatStatus.SELECTING ||
                seat.getBooking() == null ||
                !seat.getBooking().getId().equals(booking.getId())) {
            logger.warn("Seat {} is not booked by booking {}. Status: {}, Booking: {}",
                    seat.getId(), booking.getId(), seat.getStatus(), seat.getBooking());
            throw new IllegalStateException("Seat is not booked by this booking.");
        }

        try {
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
            seat.setBooking(null);
            booking.removeSeat(seat); // Sử dụng phương thức tiện ích từ Booking
            seatRepository.save(seat);
            logger.info("Seat {} released from booking {} successfully.", seat.getId(), booking.getId());
        } catch (Exception e) {
            logger.error("Failed to release seat {} from booking {}: {}", seat.getId(), booking.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to release seat: " + e.getMessage());
        }
    }

    // Tìm ghế theo ID
    public Optional<Seat> findById(Long seatId) {
        if (seatId == null) {
            logger.warn("Seat ID is null in findById.");
            return Optional.empty();
        }
        Optional<Seat> seatOpt = seatRepository.findById(seatId);
        if (seatOpt.isEmpty()) {
            logger.debug("Seat with ID {} not found.", seatId);
        }
        return seatOpt;
    }
}