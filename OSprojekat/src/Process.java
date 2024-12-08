import java.util.HashMap;


public class Process {
    private int id;
    private ProcessState state;
    private String code;

    private HashMap<String,Integer> page_table = new HashMap<>();   // FrameID u ramu , Address na hdd

    public void start(){}
    public void terminate(){}
    public void block(){}
    public void unblock(){}

    public String getHDDAddress(int frameID){
        for(String address : page_table.keySet())
            if(frameID==page_table.get(address))
                return address;
        return null;
    }

    public Integer getFrameID(String address){
        return page_table.get(address);
    }

    public String[] getAllHDDAddresses(){
        return page_table.keySet().toArray(new String[page_table.keySet().size()]);
    }

    public void setFrameID(int frameID, String address){
        page_table.put(address,frameID);
    }

    @Override
    public String toString() {return super.toString();}
}
