import java.util.HashMap;

public class HDD {
    private final int size;                 // Broj stranica
    private HashMap<String,String> memory;

    public HDD(int size) {
        this.size = size;
        memory = new HashMap<>();

        for (int i = 1; i <= size; i += 1) {
            String str = "0x" + String.format("%010d", i);
            memory.put(str, null);                          // Inicializuje sve stranice i stavlja ih na 0
        }
    }

    public int remainingSpace(){
        int freePages = 0;
        for (String key : memory.keySet()){
            if(memory.get(key)!=null){
                freePages++;
            }
        }
        return size - freePages;
    }

    public boolean hasFreeSpace() {
        return remainingSpace() > 0;
    }

    public void writeToMemory(String address, String data) throws RuntimeException {
        if (memory.containsKey(address)) {
            memory.put(address, data);
        }
        else
            throw new RuntimeException("Couldn't write to memory! {" + address + "}");

    }

    public String findFirstEmpty(){
        for (String key : memory.keySet()) {
            if(memory.get(key)==null)
                return key;
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public String getData(String address) {
        return memory.get(address);
    }

    public void printAllMemory() {

        System.out.println("Address  \t\tData");
        System.out.println("--------------------");
        for(String address : memory.keySet()) {
            System.out.println(address + ":\t" + memory.get(address));
        }
    }

    public void printUsedMemory() {
        System.out.println("Address  \t\tData");
        System.out.println("--------------------");
        for(String address : memory.keySet()) {
            if(memory.get(address)!=null)
                System.out.println(address + ":\t" + memory.get(address));
        }
    }

}
