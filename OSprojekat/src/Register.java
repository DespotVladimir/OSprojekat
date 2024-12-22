public class Register {
    private final String name;
    private final String address;
    private String value;

    public Register(String name, String address, String value) {
        this.name = name;
        this.address = address;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
