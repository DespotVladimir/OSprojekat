import java.util.ArrayList;
import java.util.Comparator;

public class ProcessManager implements Scheduler{
    private ArrayList<Process> processList;

    private int head;

    private ArrayList<Process> processTerminationList;

    public ProcessManager() {
        processList = new ArrayList<>();
        processTerminationList = new ArrayList<>();
        head = 0;
    }

    @Override
    public Process scheduleNextProcess() {
        if(processList.isEmpty())
            return null;
        if(processList.get(head).getState()==ProcessState.TERMINATED)
        {
            processTerminationList.add(processList.get(head));
            removeProcess(processList.get(head));
            head--;
        }

        processList.get(head).waiting();
        head = (head + 1) % processList.size();
        if(head == 0)
            sortProcessList();
        do {

            if(processList.get(head).getState()==ProcessState.TERMINATED)
            {
                processTerminationList.add(processList.get(head));
                removeProcess(processList.get(head));
            }

            if(processList.isEmpty())
                return null;


        }while(processList.get(head).getState() == ProcessState.TERMINATED
                ||processList.get(head).getState() == ProcessState.BLOCKED
        );

        processList.get(head).start();
        return processList.get(head);
    }

    private void sortProcessList() {
        processList.sort(Comparator.comparingInt(Process::getRemainingTime));
    }

    public Process getCurrentProcess() {
        return processList.get(head);
    }

    public void addProcess(Process p) {
        if(processList.isEmpty())
            processList.add(p);
        else
            processList.add(head+1,p);
    }
    public void removeProcess(Process p) {
        processList.remove(p);
    }

    public boolean isEmpty() {
        return processList.isEmpty();
    }

    public int getFreeID()
    {
        int i=0;

        Process[] processArray = processList.toArray(new Process[0]);
        for (Process p : processArray)
        {
            if(p.getID()!=i)
                return i;
            i++;

        }

        return i;
    }

    public boolean hasProcessToTerminate() {
        return !processTerminationList.isEmpty();
    }

    public Process[] getProcessesToTerminate(){
        return processTerminationList.toArray(new Process[0]);
    }

    public void printAll(){
        System.out.println("All processes: ");
        Process[] processArray = processList.toArray(new Process[0]);
        for(Process p : processArray)
            System.out.println(p);
    }
}
