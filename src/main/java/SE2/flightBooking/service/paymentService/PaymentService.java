package SE2.flightBooking.service.paymentService;

import SE2.flightBooking.dto.request.InitPaymentRequest;
import SE2.flightBooking.dto.response.InitPaymentResponse;
public interface PaymentService {

    InitPaymentResponse init(InitPaymentRequest request);
}
