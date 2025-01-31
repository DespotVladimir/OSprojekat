package OS;

import java.util.ArrayList;

public class Process {
    private int id;
    private ProcessState state;
    private String code;
    private String name;

    private ArrayList<Page> pages;
    private String cpuAccumulator;
    private int pageNumber;
    private int pageBlock;

    private int totalTime; // broj instrukcija

    public Process(int id, String code) {
        this(id,code,"");
    }

    public Process(int id, String code,String name) {
        this.id = id;
        this.state = ProcessState.NEW;
        this.code = code;
        this.name = name;

        pageNumber = 0;
        pageBlock = 0;

        cpuAccumulator = "000000000000";

        pages = new ArrayList<>();
        generatePages();

        totalTime = code.split("\n").length;
        assessTime();
    }

    private void assessTime(){
        String[] codeParts = code.split("\n");
        for (int i = 0; i < codeParts.length; i++) {
            String[] parts = codeParts[i].split(" ");
            String command = parts[0];
            if(command.equals("JMP"))
            {
                int number = Integer.parseInt(parts[1].replace(";",""));
                if(number <= i){
                    totalTime = Integer.MAX_VALUE;
                    break;
                }
            }
        }
    }

    private void generatePages()
    {
        Byte[] compiledCode = Assembly.compile(code);
        for (int i = 0; i < Math.ceil((double) compiledCode.length / Page.pageSize); i++) {
            Byte[] temp = new Byte[Page.pageSize];
            for (int j = 0; j < Page.pageSize; j++) {
                if(compiledCode.length  > i*Page.pageSize+j)
                    temp[j] = compiledCode[i*Page.pageSize+j];
                else
                    temp[j] = 0;
            }
            Page page = new Page(i,id,temp);
            pages.add(page);
        }
    }

    public void start(){
        state = ProcessState.RUNNING;
    }
    public void terminate(){
        state = ProcessState.TERMINATED;
    }
    public void block(){
        state = ProcessState.BLOCKED;
    }
    public void unblock(){
        state = ProcessState.READY;
    }

    public void waiting(){
        state = ProcessState.READY;
    }

    public void incrementBlock()
    {
        if(pageBlock+1==Page.pageSize/Assembly.CodeBlockSize)
        {
            pageNumber++;
            pageBlock = 0;
        }
        else
            pageBlock++;

    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageBlock;
    }

    public int getRemainingTime(){
        return totalTime - pageBlock;
    }

    public ProcessState getState(){
        return state;
    }

    public void setState(ProcessState state){
        this.state = state;
    }

    public int getID(){
        return id;
    }

    public void setPageBlock(int number) {
        pageBlock = number;
    }

    public int getPageBlock(){
        return pageBlock;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setBlock(int address) {
        this.pageBlock = address%((Page.pageSize)/Assembly.CodeBlockSize);
        this.pageNumber = address/((Page.pageSize)/Assembly.CodeBlockSize);
    }

    public Page[] getPages()
    {
        return pages.toArray(new Page[0]);
    }


    public String getCpuAccumulator() {
        return cpuAccumulator;
    }

    public void setCpuAccumulator(String cpuAccumulator) {
        this.cpuAccumulator = cpuAccumulator;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id: " + id +
                ", name: " + name +
                ", state: " + state +
                ", pageNumber: " + pageNumber +
                ", pageBlock: " + pageBlock +
                ", remainingTime: " + getRemainingTime() +
                '}';
    }
}
