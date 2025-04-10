package SE2.flightBooking.common.constant;

public enum Currency {

    USD("USD"),
    VND("VND");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
