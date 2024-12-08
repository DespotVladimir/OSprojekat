import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Page page1 = new Page(1,10);

        Page page2 = new Page(2,11);
        Page page3 = new Page(3,12);

        HDDManager hddManager = new HDDManager();


        Serializer ser = new Serializer(page1);
        System.out.println(ser.serializeToString());
        System.out.println(((Page)Serializer.deserializeFromPage(ser.serializeToString()).get()).getPageNumber());
        hddManager.writeToDisk(hddManager.findFirstEmpty(), ser.serializeToString());
        ser.setObject(page2);
        hddManager.writeToDisk(hddManager.findFirstEmpty(), ser.serializeToString());
        ser.setObject(page3);
        hddManager.writeToDisk(hddManager.findFirstEmpty(), ser.serializeToString());
        hddManager.printMemory();

    }
}