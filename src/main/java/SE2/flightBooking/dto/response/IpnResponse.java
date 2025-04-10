package SE2.flightBooking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IpnResponse {

    @JsonProperty("RspCode")
    private String responseCode;

    @JsonProperty("Message")
    private String message;

    // No-args constructor
    public IpnResponse() {
    }

    // All-args constructor
    public IpnResponse(String responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    // Getters
    public String getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
