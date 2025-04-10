package SE2.flightBooking.infrastructure.service;

import SE2.flightBooking.common.constant.ResponseCode;
import SE2.flightBooking.common.exception.BusinessException;
import SE2.flightBooking.infrastructure.constant.DefaultValue;
import SE2.flightBooking.infrastructure.util.EncodingUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Service
public class CryptoService {

    private static final Logger logger = Logger.getLogger(CryptoService.class.getName());

    private final Mac mac = Mac.getInstance(DefaultValue.HMAC_SHA512);

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;

    public CryptoService() throws NoSuchAlgorithmException {
    }

    @PostConstruct
    void init() throws InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), DefaultValue.HMAC_SHA512);
        mac.init(secretKeySpec);
    }

    public String sign(String data) {
        try {
            return EncodingUtil.toHexString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            logger.severe("Signing failed: " + e.getMessage());
            throw new BusinessException(ResponseCode.VNPAY_SIGNING_FAILED, "Failed to sign data using HMAC-SHA512.");
        }
    }
}
