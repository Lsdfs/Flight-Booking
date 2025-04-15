package SE2.flightBooking.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InitPaymentResponse {

    private String vnpUrl;

    // No-args constructor
    public InitPaymentResponse() {
    }

    // All-args constructor
    public InitPaymentResponse(String vnpUrl) {
        this.vnpUrl = vnpUrl;
    }

    // Getter
    public String getVnpUrl() {
        return vnpUrl;
    }

    // Setter
    public void setVnpUrl(String vnpUrl) {
        this.vnpUrl = vnpUrl;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String vnpUrl;

        public Builder vnpUrl(String vnpUrl) {
            this.vnpUrl = vnpUrl;
            return this;
        }

        public InitPaymentResponse build() {
            return new InitPaymentResponse(vnpUrl);
        }
    }
}
