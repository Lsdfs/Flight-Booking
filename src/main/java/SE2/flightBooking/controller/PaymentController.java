package SE2.flightBooking.controller;

import SE2.flightBooking.dto.request.InitPaymentRequest;
import SE2.flightBooking.dto.response.InitPaymentResponse;
import SE2.flightBooking.dto.response.IpnResponse;
import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Payment;
import SE2.flightBooking.service.paymentService.IpnHandler;
import SE2.flightBooking.service.paymentService.PaymentService;
import SE2.flightBooking.service.paymentService.VNPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private VNPayService vnPayService; // Inject VNPayService

    @Autowired
    private IpnHandler ipnHandler; // Inject IpnHandler for processing IPN


    @GetMapping("/vnpay_ipn")
    public IpnResponse processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay Ipn] Params: {}", params);
        return ipnHandler.process(params); // Process the IPN and return the response
    }
    @GetMapping("/processing")
    public String showPaymentSuccessPage(Model model) {
        // You can pass additional data to the view if needed
        model.addAttribute("message", "Your payment has been successfully processed.");
        return "booking/processing-payment"; // This corresponds to payment-processed.html in your templates folder
    }
}
