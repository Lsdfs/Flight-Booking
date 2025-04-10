package SE2.flightBooking.controller;

import SE2.flightBooking.dto.response.IpnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import SE2.flightBooking.service.paymentService.IpnHandler;


import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final IpnHandler ipnHandler;

    public PaymentController(IpnHandler ipnHandler) {
        this.ipnHandler = ipnHandler;
    }

    @GetMapping("/vnpay_ipn")
    IpnResponse processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay Ipn] Params: {}", params);
        return ipnHandler.process(params);
    }
}
