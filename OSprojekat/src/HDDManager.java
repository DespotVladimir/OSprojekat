public class HDDManager {
    private HDD hdd;

    //private static final int GB_SIZE = 8589934592;
    //private static final int MB_SIZE = 8388608;
    //private static final int KB_SIZE = 8192;

    public HDDManager() {
        hdd = new HDD(256);
    }

    public void writeToDisk(String address,String data) throws RuntimeException{
        hdd.writeToMemory(address,data);
    }
    public String readFromDisk(String address){
        return hdd.getData(address);
    }

    public boolean isDataInDisk(String address){
        return hdd.getData(address) != null;
    }

    public String findFirstEmpty() {
        return hdd.findFirstEmpty();
    }


    public void printMemory()
    {
        hdd.printUsedMemory();
    }
}
