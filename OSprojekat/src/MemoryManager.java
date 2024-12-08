public class MemoryManager {
    private RAM ram;

    public MemoryManager() {
        ram = new RAM(64);
    }

    public void allocateMemory(Process p){
        String[] allAddresses = p.getAllHDDAddresses();
        for(String address : allAddresses){
            Frame freeFrame = ram.getEmptyFrame();
            p.setFrameID(freeFrame.getFrameId(),address);
        }
    }

    public void freeMemory(Process p){
        String[] allAddresses = p.getAllHDDAddresses();
        for(String address : allAddresses){
            int frameID = p.getFrameID(address);
            ram.freeFrame(frameID);
        }
    }
    public void foundFreeSpace(int size){}
    public void getProcessMemoryAddress(Process p){}
}
