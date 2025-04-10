package SE2.flightBooking.service.paymentService;

import SE2.flightBooking.common.exception.BusinessException;
import SE2.flightBooking.common.constant.VNPayParams;
import SE2.flightBooking.common.constant.VnpIpnResponseConst;
import SE2.flightBooking.dto.response.IpnResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class VNPayIpnHandler implements IpnHandler {

    private static final Logger logger = Logger.getLogger(VNPayIpnHandler.class.getName());

    private final VNPayService vnPayService;
    private final FlightService flightService;

    public VNPayIpnHandler(VNPayService vnPayService, FlightService flightService) {
        this.vnPayService = vnPayService;
        this.flightService = flightService;
    }

    @Override
    public IpnResponse process(Map<String, String> params) {
        if (!vnPayService.verifyIpn(params)) {
            return VnpIpnResponseConst.SIGNATURE_FAILED;
        }

        IpnResponse response;
        String txnRef = params.get(VNPayParams.TXN_REF);
        try {
            long bookingId = Long.parseLong(txnRef);
            flightService.markBookingAsPaid(bookingId);  // Now using PaymentService
            response = VnpIpnResponseConst.SUCCESS;
        } catch (BusinessException e) {
            switch (e.getResponseCode()) {
                case BOOKING_NOT_FOUND -> response = VnpIpnResponseConst.ORDER_NOT_FOUND;
                default -> response = VnpIpnResponseConst.UNKNOWN_ERROR;
            }
        }
        catch (Exception e) {
            response = VnpIpnResponseConst.UNKNOWN_ERROR;
        }

        logger.info(String.format("[VNPay Ipn] txnRef: %s, response: %s", txnRef, response));
        return response;
    }
}
