public class HDDManager {

    private HDD hdd;

    private String fileFirstEmpty;
    private String firstVirtualMemory;

    private final int HDDSize =512;
    private final String VirtualMemoryStart = "0x"+(380*Page.pageSize);

    public HDDManager() {
        hdd = new HDD(HDDSize);
        fileFirstEmpty = "0x0";
        firstVirtualMemory = VirtualMemoryStart;
    }

    public void writeToDisk(String address,String data) throws RuntimeException{
        hdd.writeToMemory(address,data);
    }

    public void writeToDisk(String data) {
        hdd.writeToMemory(findFirstEmpty(),data);
    }

    public String readFromDisk(String address){
        return hdd.getData(address);
    }

    public void writeFileContents(String fileContents)
    {
        int numOfPagesTaken =(int)(Math.ceil((fileContents.length()+1.0)/Page.pageSize));
        for (int i = 0; i < numOfPagesTaken; i++) {
            String freeAddress = findFirstEmpty();
            writeToDisk(freeAddress,fileContents.substring(i*Page.pageSize,Math.min((i+1)*Page.pageSize,fileContents.length())));
        }
    }

    public void writePage(Page page, String address){
        String pageContents = page.dataToString();
        String toDisk = page.getPageNumber()+"/"+pageContents;
        writeToDisk(address,toDisk);
    }

    public Page getPage(String address, int processID)
    {
        String addressContents = hdd.getData(address);
        String pageID = addressContents.substring(0,addressContents.indexOf("/"));
        String bytes = addressContents.substring(addressContents.indexOf("/")+1);
        Page page = new Page(Integer.parseInt(pageID),processID);
        page.setDataFromString(bytes);
        return page;
    }

    public void removeContentFrom(String ... addresses){
        for(String address : addresses){
            writeToDisk(address,null);
        }
    }

    public boolean isDataInDisk(String address){
        return hdd.getData(address) != null;
    }

    public String findFirstEmpty() {
        if(hdd.getData(fileFirstEmpty) != null)
            fileFirstEmpty = hdd.findFirstEmpty(fileFirstEmpty);
        return fileFirstEmpty;
    }

    public String findFirstEmptyVirtualMemory() {
        if (hdd.getData(firstVirtualMemory) != null)
            firstVirtualMemory = hdd.findFirstEmpty(firstVirtualMemory);
        return firstVirtualMemory;
    }

    public String[] findSCAN(String ... addresses){
        return hdd.SCAN(addresses);
    }

    public HDD getHDD() {
        return hdd;
    }

    public void printMemory()
    {
        hdd.printUsedMemory();
    }
}
