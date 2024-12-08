import java.io.Serializable;

public class Page implements Serializable {
    private int pageNumber;         // Broj stranice u virtuelnom prostoru
    private int processId;          // ID procesa kojem stranica pripada
    private boolean inMemory;       // Da li je stranica u ram memoriji
    private long lastUsedTime;      // Timestamp poslednje upotrebe
    private int[] data;             // 1 int = 32 bita

    public static final int pageSize = 8192;

    public Page(int pageNumber, int processId) {
        this.pageNumber = pageNumber;
        this.processId = processId;
        this.inMemory = false;
        this.lastUsedTime = System.currentTimeMillis();
        this.data = new int[256];   // 8192 bita
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


    @Override
    public String toString() {
        return "Page{" +
                "pageNumber=" + pageNumber +
                ", processId='" + processId + '\'' +
                ", inMemory=" + inMemory +
                ", lastUsedTime=" + lastUsedTime +
                '}';
    }
}
