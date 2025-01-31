package OS;

import java.util.Map;

public class CPU {
    private Register IP;    // Instruction pointer
    private Register SP;    // Stack pointer

    private Register R1;
    private Register R2;
    private Register R3;
    private Register R4;

    private Register AC;    // Accumulator 12 bit

    //  Flags
    private Register OF;    //  Overflow flag
    private Register ZF;    //  Zero flag


    private boolean busy;
    public static int clockCycle = 1;   // po 1s
    public int clock;

    private final int quantumTime;

    private int instruction;

    private Map<String,Register> registerMap;

    public CPU(){
        busy = false;
        quantumTime = 10;

        IP = new Register("IP","0","0");
        SP = new Register("SP","1","230");
        AC = new Register("AC","2","0");
        R1 = new Register("R1","3","0");
        R2 = new Register("R2","4","0");
        R3 = new Register("R3","5","0");
        R4 = new Register("R4","6","0");
        OF = new Register("OF","7","0");
        ZF = new Register("ZF","8","0");
        registerMap= Map.ofEntries(
                Map.entry("0",IP),
                Map.entry("IP",IP),

                Map.entry("1",SP),
                Map.entry("SP",SP),

                Map.entry("2",AC),
                Map.entry("AC",AC),

                Map.entry("3",R1),
                Map.entry("R1",R1),

                Map.entry("4",R2),
                Map.entry("R2",R2),

                Map.entry("5",R3),
                Map.entry("R3",R3),

                Map.entry("6",R4),
                Map.entry("R4",R4),

                Map.entry("7",OF),
                Map.entry("OF",OF),

                Map.entry("8",ZF),
                Map.entry("ZF",ZF)
        );
    }


    public void ADD(String number){
        int num = Assembly.binaryToDecimal(number);
        int ac_num=Assembly.binaryToDecimal(AC.getValue());
        AC.setValue(Assembly.decimalTo12BitBinary(String.valueOf(num+ac_num)));
        increment();
    }

    public void SUB(String number){
        int num = Assembly.binaryToDecimal(number);
        int ac_num=Assembly.binaryToDecimal(AC.getValue());
        AC.setValue(Assembly.decimalTo12BitBinary(String.valueOf(num-ac_num)));
        increment();
    }

    public void LDA(Page page,String number){
        int blockNum = Assembly.binaryToDecimal(number)%(int)((Page.pageSize/Assembly.CodeBlockSize));
        Byte[] memoryBlock = page.getMemoryBlock(blockNum);
        int value = Assembly.getNumberFromBlock(Assembly.blockToString(memoryBlock));
        AC.setValue(Assembly.decimalTo12BitBinary(Integer.toString(value)));
        increment();
    }

    public void STA(Page page,String index){
        int blockNum = (Assembly.binaryToDecimal(index) * Assembly.CodeBlockSize) % Page.pageSize;
        page.setBlockAt(blockNum,String.format("%012d",Integer.parseInt(AC.getValue())));
        increment();
    }

    public void JMP(Process p,int number) {
        p.setBlock(number);
        increment();
    }

    public void JZ(Process p, int number) {
        if(Assembly.binaryToDecimal(AC.getValue())!=0)
            return;
        JMP(p,number);
    }

    public void JZN(Process p, int number) {
        if(Assembly.binaryToDecimal(AC.getValue())<=0)
            return;
        JMP(p,number);
    }

    public void HLT(){
        resetAC();
        increment();
    }

    public void increment(){
        instruction++;
        clock++;
    }

    public void resetAC() {
        AC.setValue(String.format("%012d",0));
    }

    public String getAC()
    {
        return AC.getValue();
    }

    public String getValue(String name) {
        return registerMap.get(name).getValue();
    }

    public Register getRegister(String address){
        return registerMap.get(address);
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean getBusy() {
        return busy;
    }

    public void resetInstruction() {
        instruction = 0;
    }

    public void resetClock()
    {
        clock = 0;
    }

    public int getClock()
    {
        return clock;
    }

    public boolean isNextCycle()
    {
        return instruction >=quantumTime;
    }
}
