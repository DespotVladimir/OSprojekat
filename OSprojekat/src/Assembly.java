import java.util.ArrayList;
import java.util.Map;

public class Assembly {
    public static final int CodeBlockSize = 16;

    public static Byte[] compile(String sourceCode){
        ArrayList<Byte> bytes = new ArrayList<>();
        int najvecaAdresa=0;
        sourceCode = sourceCode.replace("\n","");
        String[] lines = sourceCode.split(";");
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(" ");

            String command=parts[0];
            String argument="0";
            if(parts.length>1)
                argument=parts[1];

            if(isJumper(command))
            {
                int arg = Integer.parseInt(argument);
                if(arg>najvecaAdresa)
                    najvecaAdresa = arg;
            }

            String compiled = opcode_table.get(command)+decimalTo12BitBinary(argument);

            for(int j=0;j<compiled.length();j++){
                bytes.add(Byte.parseByte(compiled.charAt(j)+""));
            }
        }
        if(bytes.size() < najvecaAdresa * CodeBlockSize)
            for(int i=0; i < najvecaAdresa; i++)
                bytes.add((byte)0);

        return bytes.toArray(new Byte[0]);
    }

    public static String decimalTo12BitBinary(String decimal){
        // decimal -> 12 bit binary

        int number = Integer.parseInt(decimal);

        if (number<=0)
            return "000000000000";

        StringBuilder binary = new StringBuilder();
        while(number>0){
            int remainder = number % 2;
            binary.append(remainder);
            number = number / 2;
        }
        binary.reverse();

        return String.format("%012d", Integer.parseInt(binary.toString()));
    }

    public static int binaryToDecimal(String binary){
        // 12 bit binary to decimal

        int number = Integer.parseInt(binary);

        int base = 1,dec_value=0;
        while(number>0){
            dec_value+=(number%10)*base;
            number = number/10;
            base*=2;
        }
        return dec_value;
    }

    public static final Map<String,String> opcode_table = Map.ofEntries(
            Map.entry("LDA","0001"),
            Map.entry("0001","LDA"),

            Map.entry("ADD","0010"),
            Map.entry("0010","ADD"),

            Map.entry("SUB","0011"),
            Map.entry("0011","SUB"),

            Map.entry("STA","0100"),
            Map.entry("0100","STA"),

            Map.entry("JMP","0101"),
            Map.entry("0101","JMP"),

            Map.entry("JZ","0110"),
            Map.entry("0110","JZ"),

            Map.entry("HLT","1111"),
            Map.entry("1111","HLT")
    );

    public static boolean isJumper(String command)
    {
        return command.equals("JMP")
                || command.equals("JZ")
                || command.equals("STA")
                || command.equals("LDA");
    }

    public static int getNumberFromBlock(String binaryBlock)
    {
        // 16 bit binary to decimal
        return binaryToDecimal(binaryBlock.substring(4));
    }

    public static String blockToString(Byte[] memoryBlock)
    {
        // bytes to string
        StringBuilder sb = new StringBuilder();
        for (byte b : memoryBlock) {
            sb.append(b);
        }
        return sb.toString();
    }


    /*
    * LDA	Učitaj podatak iz memorije	0001
    * ADD	Saberi podatke	0010
    * SUB	Oduzmi podatke	0011
    * STA	Sačuvaj podatak u memoriju	0100
    * JMP	Skok na adresu	0101
    * JZ	Skok ako je rezultat 0	0110
    * HLT	Zaustavi program	1111
    * 0000  Broj
    ***/
}
