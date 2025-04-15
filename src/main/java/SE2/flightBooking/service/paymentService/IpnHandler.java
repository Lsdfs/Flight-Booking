package SE2.flightBooking.service.paymentService;

import SE2.flightBooking.dto.response.IpnResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface IpnHandler {
    IpnResponse process(Map<String, String> params);
}
