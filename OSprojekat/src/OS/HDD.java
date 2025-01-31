package OS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HDD {

    /*
    OS.HDD je sastavljen od memorije koja je predstavljena arraylistom ciji clanovi su predstavljeni kao blokovi koji su
    jednaki velicini stranica.
    Pristupanje stranicama je pomocu adresa u formatu 0x0 ili 0, adrese su jednake indeks*velicina_stranice

    String str = "0x" + String.format("%010d", i * OS.Page.pageSize);
     */

    private final int size;     // Broj blokova
    private ArrayList<String> memory;

    private int head;

    public HDD(int size) {
        this.size = size;
        memory = new ArrayList<>();

        for (int i = 0; i <= size; i += 1)
            memory.add(null);

    }

    public int remainingSpace(){

        int freePages = 0;

        for (String page : memory)
            if(page == null)
                freePages++;

        return freePages;
    }

    public boolean hasFreeSpace() {
        return remainingSpace() > 0;
    }

    public void writeToMemory(String address, String data){
        memory.set(translateAddress(address), data);
    }

    public String findFirstEmpty(){
        return findFirstEmpty("0x0");
    }

    public String findFirstEmpty(String startAddress){
        int start = translateAddress(startAddress);
        for (int i = start; i < memory.size(); i++)
            if(memory.get(i) == null)
                return encodeAddress(i);
        return null;
    }

    public int getSize() {
        return size;
    }

    public String[] SCAN(String ... addresses){
        HashMap<Integer,Integer> addressMap = new HashMap<>();
        String[] returns = new String[addresses.length];

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for(int i = 0; i < addresses.length; i++){
            int translatedAddresses = translateAddress(addresses[i]);
            addressMap.put(translatedAddresses,i);
            if(translatedAddresses < head)
                left.add(translatedAddresses);
            else
                right.add(translatedAddresses);
        }

        Collections.sort(left);
        Collections.sort(right);

        for (int rightAddress : right) {
            returns[addressMap.get(rightAddress)] = memory.get(rightAddress);
            head = rightAddress;
        }

        head = 0;

        for (int leftAddress : left) {
            returns[addressMap.get(leftAddress)] = memory.get(leftAddress);
            head = leftAddress;
        }

        return returns;
    }

    public String getData(String address) {
        return SCAN(address)[0];
    }

    private int translateAddress(String address) {
        if(address.contains("0x"))
            return Integer.parseInt(address.substring(address.indexOf("0x")+2)) / Page.pageSize;
        return Integer.parseInt(address)/Page.pageSize;
    }

    public String encodeAddress(int address) {
        return "0x"+String.format("%010d", address*Page.pageSize);
    }

    public boolean isValidAddress(String address) {
        return translateAddress(address)<memory.size();
    }

    public void printAllMemory() {
        System.out.println("Address  \t\tData");
        System.out.println("--------------------");
        for(int i=0;i<memory.size();i+=1) {
            System.out.println(encodeAddress(i) + ":\t" + memory.get(i));
        }
    }

    public String[] getAllMemory() {
        return memory.toArray(new String[0]);
    }

    public void printUsedMemory() {
        System.out.println("Address  \t\tData");
        System.out.println("--------------------");
        for(int i=0; i<memory.size(); i+=1) {
            if(memory.get(i)!=null)
                System.out.println(encodeAddress(i) + ":\t" + memory.get(i).replaceAll("\n",""));
        }
    }

}
