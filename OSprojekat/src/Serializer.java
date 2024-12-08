import java.io.*;
import java.util.Base64;
import java.util.Optional;

public class Serializer <T>{
    private T object;

    Serializer(T object){
        this.object = object;
    }

    public void setObject(T object){
        this.object = object;
    }

    public String serializeToString() throws IOException {
        ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytearray);
        oos.writeObject(object);
        oos.close();
        String className = object.getClass().getName().toUpperCase();
        if(className.equalsIgnoreCase("Page")){
            return className+"/"+((Page)object).getPageNumber()+"/"+Base64.getEncoder().encodeToString(bytearray.toByteArray());
        }
        return className+"/"+Base64.getEncoder().encodeToString(bytearray.toByteArray());
    }

    public static Optional deserializeFromPage(String s) throws IOException, ClassNotFoundException {
        String[] parts = s.split("/");
        String className = parts[0];
        String base64 = parts[parts.length-1];

        byte [] data = Base64.getDecoder().decode(base64.getBytes());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));

        Optional optional = Optional.empty();
        if (className.equalsIgnoreCase("PAGE"))
        {
            Page page = (Page) ois.readObject();
            optional = Optional.of(page);
        }
        else if (className.equalsIgnoreCase("DIRECTORY"))
        {
            Directory directory = (Directory) ois.readObject();
            optional = Optional.of(directory);
        }

        ois.close();

        return optional;
    }



}
