
public class Main {
    public static void main(String[] args)  {
        Kernel kernel = new Kernel();
        Terminal terminal = new Terminal(kernel);
        terminal.start();


    }
}