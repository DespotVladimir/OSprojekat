package OS;

public class File {
    private String name;
    private String content;
    private String address;

    public File(String name, String content, String address) {
        this.name = name;
        this.content = content;
        this.address = address;
    }

    public File(String name, String address) {
        this(name,"",address);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
