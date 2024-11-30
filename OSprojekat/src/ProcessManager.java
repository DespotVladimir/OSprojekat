public class ProcessManager implements Scheduler{
    private java.util.ArrayList<Process> processList;

    @Override
    public Process scheduleNextProcess() {
        return null;
    }

    public void addProcess(Process p) {}
    public void removeProcess(Process p) {}
    public boolean isEmpty() {return processList.isEmpty();}
}
