package OS;

import java.util.ArrayList;
import java.util.Comparator;

public class ProcessManager implements Scheduler{
    private ArrayList<Process> processList;

    private int head;
    private int sameProcess;

    private ArrayList<Process> processTerminationList;
    private ArrayList<Integer> IDs;



    public ProcessManager() {
        processList = new ArrayList<>();
        processTerminationList = new ArrayList<>();
        IDs = new ArrayList<>();
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
            head = Math.max(head-1, 0);
        }

        processList.get(head).waiting();
        //


        do {

            sortProcessList();

            if(sameProcess>=100){

                // ide RoundRobin ako je pocetni proces izabran 10 puta
                // traje do kraja reda

                head = (head + 1) % processList.size();

                if(head==0){
                    sameProcess=0;
                    continue;
                }



                if(processList.get(head).getState()==ProcessState.TERMINATED)
                {
                    processTerminationList.add(processList.get(head));
                    removeProcess(processList.get(head));
                }
            }
            else{

                // Normalno uzima 1 element niza

                if(processList.get(head).getState()==ProcessState.TERMINATED)
                {
                    processTerminationList.add(processList.get(head));
                    removeProcess(processList.get(head));
                    sameProcess--;
                }
                sameProcess++;
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
            processList.add(head,p);

        IDs.add(p.getID());
    }
    public void removeProcess(Process p) {
        processList.remove(p);
        IDs.remove(p.getID());
    }

    public boolean isEmpty() {
        return processList.isEmpty();
    }

    public Process getProcess(int id) {

        Process[] allProcesses = processList.toArray(new Process[0]);

        for(Process p : allProcesses)
            if(p.getID()==id)
                return p;

        return null;
    }

    public int getFreeID()
    {
        for (int j = 0; j < Integer.MAX_VALUE; j++) {
            if(!IDs.contains(j))
                return j;
        }

        return -1;
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
