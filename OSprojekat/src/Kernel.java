public class Kernel {
    private final CPU cpu;
    private final FileSystemManager fsm;
    private final ProcessManager pm;
    private final MemoryManager mm;
    private final HDDManager hm;

    public Kernel() {
        cpu = new CPU();
        fsm = new FileSystemManager();
        pm = new ProcessManager();
        mm = new MemoryManager();
        hm = new HDDManager();
    }

    public void start(){}
}
