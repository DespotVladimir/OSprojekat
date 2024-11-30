public class Process {
    private int id;
    private ProcessState state;
    private String code;

    public void start(){}
    public void terminate(){}
    public void block(){}
    public void unblock(){}

    @Override
    public String toString() {return super.toString();}
}
