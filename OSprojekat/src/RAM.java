import java.util.ArrayList;

public class RAM {

    private final int frameCount;           // Broj frejmova
    private final ArrayList<Frame> frames;  // Lista frejmova

    public RAM(int frameCount) {
        this.frameCount = frameCount;
        this.frames = new ArrayList<>(frameCount);

        for (int i = 0; i < frameCount; i++) {
            frames.add(new Frame(i));
        }
    }

    public int getFrameCount(){
        return frameCount;
    }

    public boolean isPageInRAM(int pageNumber) {
        for (Frame frame : frames) {

            if (frame.getPage() != null && frame.getPage().getPageNumber() == pageNumber)
                return true;
        }
        return false;
    }

    public Frame getFrameForPage(int processID,int pageNumber) {

        for (Frame frame : frames) {

            if (frame.getPage() != null)
            {
                if(frame.getPage().getPageNumber() == pageNumber && frame.getPage().getProcessId() == processID)
                {

                    return frame;
                }

            }

        }
        return null;
    }

    public void freeFrame(int frameID) {
        for (Frame frame : frames) {
            if(frameID == frame.getFrameId()){
                frame.getPage().setInMemory(false);
                frame.setPage(null);
            }
        }
    }

    public Frame getEmptyFrame() {
        /*
        Nalazi frame koji nije trenutno koristen
         */

        for (int i = 0; i < frames.size(); i++) {
            if (frames.get(i).getPage() == null)
                return frames.get(i);
        }
        return null;
    }

    public Frame getLRUFrame() {
        Frame lruFrame = null;
        long oldestTime = Long.MAX_VALUE;

        for (Frame frame : frames) {
            if (frame.getPage() != null) {
                long lastUsedTime = frame.getPage().getLastUsedTime();
                if (lastUsedTime < oldestTime) {
                    oldestTime = lastUsedTime;
                    lruFrame = frame;
                }
            }
        }
        return lruFrame; // Vraćamo frame sa najmanje korišćenom stranicom ili null ako svi frejmovi nisu zauzeti
    }

    public void loadPageIntoFrame(Page page) {
        Frame frame = getEmptyFrame();

        if (frame == null) {

            frame = getLRUFrame();
            frame.getPage().setInMemory(false);
        }

        frame.setPage(page);
        page.setInMemory(true);

    }

    public int remainingSpace(){
        int freeSpace = 0;
        for (Frame frame : frames) {
            if (frame.isEmpty())
                freeSpace++;
        }
        return freeSpace;
    }

    public void printFrames() {
        System.out.println("RAM frames:");
        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                System.out.println("Frame " + frame.getFrameId() + ": EMPTY");
            } else {
                System.out.println("Frame " + frame.getFrameId() + ": " + frame.getPage());
            }
        }
    }


}
