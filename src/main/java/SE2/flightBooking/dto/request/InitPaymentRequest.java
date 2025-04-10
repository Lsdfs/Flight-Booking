package SE2.flightBooking.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InitPaymentRequest {

    private String ipAddress;
    private Long userId;
    private String txnRef;
    private long amount;

    public InitPaymentRequest() {
    }

    public InitPaymentRequest( String ipAddress, Long userId, String txnRef, long amount) {
        this.ipAddress = ipAddress;
        this.userId = userId;
        this.txnRef = txnRef;
        this.amount = amount;
    }

    // Getters

    public String getIpAddress() {
        return ipAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public long getAmount() {
        return amount;
    }

    // Setters

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    // Optional: builder-style factory method if you want a similar feel to Lombok's @Builder
    public static InitPaymentRequestBuilder builder() {
        return new InitPaymentRequestBuilder();
    }

    public static class InitPaymentRequestBuilder {
        private String requestId;
        private String ipAddress;
        private Long userId;
        private String txnRef;
        private long amount;

        public InitPaymentRequestBuilder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public InitPaymentRequestBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public InitPaymentRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public InitPaymentRequestBuilder txnRef(String txnRef) {
            this.txnRef = txnRef;
            return this;
        }

        public InitPaymentRequestBuilder amount(long amount) {
            this.amount = amount;
            return this;
        }

        public InitPaymentRequest build() {
            return new InitPaymentRequest(ipAddress, userId, txnRef, amount);
        }
    }
}
