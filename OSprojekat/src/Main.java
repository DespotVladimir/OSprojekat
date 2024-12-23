import GUI.*;

public class Main {
    public static void main(String[] args)  {
        Kernel kernel = new Kernel();

        //mainGUI gui = new mainGUI();
        //gui.start();

        Terminal terminal = new Terminal(kernel);
        terminal.start();


    }
}