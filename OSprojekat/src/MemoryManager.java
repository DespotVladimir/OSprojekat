public class MemoryManager {
    private final RAM ram;

    public MemoryManager() {
        ram = new RAM(64);
    }

    public Page allocateMemory(Process p){
        Page[] processPages = p.getPages();

        if(processPages.length == 0)
            return null;



        return ram.loadPageIntoFrame(processPages[0]);

    }

    public void freeMemory(Process p){
        Page[] processPages = p.getPages();

        for(Page page : processPages){
            if(page.isInMemory())
            {
                Frame f = ram.getFrameForPage(p.getID(),page.getPageNumber());
                ram.freeFrame(f.getFrameId());
                page.setInMemory(false);
            }
        }
    }

    public boolean foundFreeSpace(int size)     // Broj stranica
    {
        return ram.remainingSpace() >= size;
    }


    public double getUsedMemory()
    {
        return ((double)(ram.getFrameCount()-ram.remainingSpace()) / ram.getFrameCount());
    }

    public RAM getRam(){
        return ram;
    }
}
