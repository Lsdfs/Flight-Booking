package SE2.flightBooking.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VNPayInitPaymentRequest {

    @JsonProperty("vnp_Version")
    public static final String VERSION = "2.1.0";

    @JsonProperty("vnp_Command")
    public static final String COMMAND = "querydr";

    @JsonProperty("vnp_RequestId")
    private String requestId;

    @JsonProperty("vnp_TmnCode")
    private String tmnCode;

    @JsonProperty("vnp_TxnRef")
    private String txnRef;

    @JsonProperty("vnp_CreateDate")
    private String createdDate;

    @JsonProperty("vnp_IpAddr")
    private String ipAddress;

    @JsonProperty("vnp_OrderInfo")
    private String orderInfo;

    @JsonProperty("vnp_SecureHash")
    private String secureHash;

    // No-args constructor
    public VNPayInitPaymentRequest() {
    }

    // All-args constructor
    public VNPayInitPaymentRequest(String requestId, String tmnCode, String txnRef,
                                   String createdDate, String ipAddress,
                                   String orderInfo, String secureHash) {
        this.requestId = requestId;
        this.tmnCode = tmnCode;
        this.txnRef = txnRef;
        this.createdDate = createdDate;
        this.ipAddress = ipAddress;
        this.orderInfo = orderInfo;
        this.secureHash = secureHash;
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public String getSecureHash() {
        return secureHash;
    }

    // Setters
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public void setSecureHash(String secureHash) {
        this.secureHash = secureHash;
    }

    // Builder pattern
    public static VNPayInitPaymentRequestBuilder builder() {
        return new VNPayInitPaymentRequestBuilder();
    }

    public static class VNPayInitPaymentRequestBuilder {
        private String requestId;
        private String tmnCode;
        private String txnRef;
        private String createdDate;
        private String ipAddress;
        private String orderInfo;
        private String secureHash;

        public VNPayInitPaymentRequestBuilder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public VNPayInitPaymentRequestBuilder tmnCode(String tmnCode) {
            this.tmnCode = tmnCode;
            return this;
        }

        public VNPayInitPaymentRequestBuilder txnRef(String txnRef) {
            this.txnRef = txnRef;
            return this;
        }

        public VNPayInitPaymentRequestBuilder createdDate(String createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public VNPayInitPaymentRequestBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public VNPayInitPaymentRequestBuilder orderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public VNPayInitPaymentRequestBuilder secureHash(String secureHash) {
            this.secureHash = secureHash;
            return this;
        }

        public VNPayInitPaymentRequest build() {
            return new VNPayInitPaymentRequest(requestId, tmnCode, txnRef, createdDate, ipAddress, orderInfo, secureHash);
        }
    }
}
