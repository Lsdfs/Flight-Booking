package SE2.flightBooking.common.exception;

import SE2.flightBooking.common.constant.ResponseCode;
public class BusinessException extends RuntimeException {

    private ResponseCode responseCode;

    // No-arg constructor
    public BusinessException() {
        super();
    }

    // All-arg constructor
    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    // Getter
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    // Setter
    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
