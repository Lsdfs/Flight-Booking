package SE2.flightBooking.service.paymentService;

import SE2.flightBooking.common.constant.Currency;
import SE2.flightBooking.common.constant.Locale;
import SE2.flightBooking.common.constant.VNPayParams;
import SE2.flightBooking.dto.request.InitPaymentRequest;
import SE2.flightBooking.dto.response.InitPaymentResponse;
import SE2.flightBooking.infrastructure.constant.Symbol;
import SE2.flightBooking.infrastructure.service.CryptoService;
import SE2.flightBooking.infrastructure.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Service
public class VNPayService implements PaymentService {

    private static final Logger logger = Logger.getLogger(VNPayService.class.getName());

    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";
    public static final String ORDER_TYPE = "190000";
    public static final long DEFAULT_MULTIPLIER = 100L;

    @Value("${payment.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${payment.vnpay.init-payment-url}")
    private String initPaymentPrefixUrl;

    @Value("${payment.vnpay.return-url}")
    private String returnUrlFormat;

    @Value("${payment.vnpay.timeout}")
    private Integer paymentTimeout;

    private final CryptoService cryptoService;


    public VNPayService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public InitPaymentResponse init(InitPaymentRequest request) {
        long amount = request.getAmount() * DEFAULT_MULTIPLIER;
        String txnRef = request.getTxnRef();
        String returnUrl = buildReturnUrl(txnRef);

        Calendar vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        String createdDate = DateUtil.formatVnTime(vnCalendar);
        vnCalendar.add(Calendar.MINUTE, paymentTimeout);
        String expiredDate = DateUtil.formatVnTime(vnCalendar);

        String orderInfo = buildPaymentDetail(request);

        Map<String, String> params = new HashMap<>();
        params.put(VNPayParams.VERSION, VERSION);
        params.put(VNPayParams.COMMAND, COMMAND);
        params.put(VNPayParams.TMN_CODE, tmnCode);
        params.put(VNPayParams.AMOUNT, String.valueOf(amount));
        params.put(VNPayParams.CURRENCY, Currency.VND.getValue());
        params.put(VNPayParams.TXN_REF, txnRef);
        params.put(VNPayParams.RETURN_URL, returnUrl);
        params.put(VNPayParams.CREATED_DATE, createdDate);
        params.put(VNPayParams.EXPIRE_DATE, expiredDate);
        params.put(VNPayParams.IP_ADDRESS, "127.0.0.1");
        params.put(VNPayParams.LOCALE, Locale.VIETNAM.getCode());
        params.put(VNPayParams.ORDER_INFO, orderInfo);
        params.put(VNPayParams.ORDER_TYPE, ORDER_TYPE);

        String initPaymentUrl = buildInitPaymentUrl(params);
        logger.info("Request: Init payment url: " + initPaymentUrl);

        InitPaymentResponse response = new InitPaymentResponse();
        response.setVnpUrl(initPaymentUrl);
        return response;
    }

    public boolean verifyIpn(Map<String, String> params) {
        String reqSecureHash = params.get(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH_TYPE);

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashPayload = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashPayload.append(fieldName).append(Symbol.EQUAL)
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashPayload.append(Symbol.AND);
                }
            }
        }

        String secureHash = cryptoService.sign(hashPayload.toString());
        return secureHash.equals(reqSecureHash);
    }

    private String buildPaymentDetail(InitPaymentRequest request) {
        return String.format("Thanh toan don dat phong %s", request.getTxnRef());
    }

    private String buildReturnUrl(String txnRef) {
        return String.format(returnUrlFormat, txnRef);
    }

    private String buildInitPaymentUrl(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashPayload = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashPayload.append(fieldName).append(Symbol.EQUAL)
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append(Symbol.EQUAL)
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashPayload.append(Symbol.AND);
                    query.append(Symbol.AND);
                }
            }
        }

        String secureHash = cryptoService.sign(hashPayload.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        return initPaymentPrefixUrl + "?" + query;
    }
}
