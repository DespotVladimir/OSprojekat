package OS;

public class Page {

    public static final int pageSize = 4096;

    private int pageNumber;         // Broj stranice u virtuelnom prostoru
    private int processId;          // ID procesa kojem stranica pripada
    private boolean inMemory;       // Da li je stranica u ram memoriji
    private long lastUsedTime;      // Timestamp poslednje upotrebe
    private Byte[] data;           // 4096 bita

    public Page(int pageNumber, int processId) {
        this.pageNumber = pageNumber;
        this.processId = processId;
        this.inMemory = false;
        this.lastUsedTime = System.currentTimeMillis();
        this.data = new Byte[4096];
        for(int i = 0; i < 4096; i++)
            this.data[i] = 0;
    }

    public Page(File file,int pageID){
        this.pageNumber = 0;
        this.processId = pageID;
        this.inMemory = false;
        this.lastUsedTime = System.currentTimeMillis();
        this.data = new Byte[4096];
        byte[] fileData = file.getContent().getBytes();
        for(int i = 0; i < 4096; i++){
            if(i<fileData.length)
                this.data[i] = (byte)(fileData[i]>4?1:0);
            else
                this.data[i] = 0;
        }
    }

    public Page(int pageNumber, int processId, Byte[] data) {
        this.pageNumber = pageNumber;
        this.processId = processId;
        this.inMemory = false;
        this.lastUsedTime = System.currentTimeMillis();
        this.data = data;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getProcessId() {
        return processId;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void updateLastUsedTime() {
        this.lastUsedTime = System.nanoTime();
    }

    public Byte[] getMemoryBlock(int blockNumber) {
        int pageIndex = blockNumber * Assembly.CodeBlockSize;
        Byte[] memoryBlock = new Byte[Assembly.CodeBlockSize];

        for(int i=pageIndex,k=0;i<pageIndex+Assembly.CodeBlockSize;i++)
            memoryBlock[k++] = data[i];
        return memoryBlock;
    }


    public String dataToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pageSize; i++) {
            if(data[i]==null)
                sb.append("0");
            else
                sb.append(data[i]);
        }
        return sb.toString();
    }

    public void setBlockAt(int index,String number){
        int i=index;
        if(number.startsWith("0000"))
        {
            i+=4;
        }
        for(int k=0;i<index+Assembly.CodeBlockSize;i++,k++)
        {
            data[i]=Byte.parseByte(String.valueOf(number.charAt(k)));
        }
    }

    public void setData(Byte[] data) {
        this.data = data;
    }

    public void setDataFromString(String data) {
        Byte[] temp = new Byte[Page.pageSize];
        for (int i = 0; i < data.length();i++) {
            temp[i] = Byte.parseByte(String.valueOf(data.charAt(i)));
        }
        setData(temp);
    }



    @Override
    public String toString() {
        return  "PN" + pageNumber +
                "/PID" + processId +
                "/"+dataToString();
    }
}
