public class Frame {
    private final int frameId;  // ID frejma
    private Page page;          // Stranica koja se nalazi u ovom frejmu

    public Frame(int frameId) {
        this.frameId = frameId;
        this.page = null;
    }

    public int getFrameId() {
        return frameId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
