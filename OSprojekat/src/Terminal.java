import java.util.Scanner;

public class Terminal {
    private Kernel kernel;
    private Thread kernelThread;

    Terminal(Kernel kernel) {
        this.kernel = kernel;
        kernelThread = new Thread(kernel);
    }

    public void start(){
        try {
            kernel.boot();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kernelThread.start();
        terminal();
    }

    public void terminal()
    {
        // CD [dir_name]     prelazi u ciljeni direktorijum
        // DIR               lista trenutnih direktorijuma
        // PS                lista procesa i osnovne informacije o njima
        // mkdir [dir_name]  napravi direktorijum
        // run [filename]    pokrece_proces
        // mem               Zauzece memorije
        // exit              Gasi OS
        // rm                Uklanja datoteku
        // rd [file_name]   Ispisi fajl


        String user_command = "";
        String argument = "";
        Scanner scanner = new Scanner(System.in);

        try {
            synchronized (kernelThread){kernelThread.wait(5);};
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while(!user_command.equals("exit")){
            System.out.print(kernel.getCurrentDirectory()+">");

            String[] command = scanner.nextLine().split(" ");
            user_command = command[0].toLowerCase();


            if(command.length>1){
                argument = command[1];
                if(user_command.equalsIgnoreCase("cd"))
                    kernel.cd(argument);
                else if(user_command.equalsIgnoreCase("mkdir"))
                {
                    kernel.mkdir(argument);
                }
                else if(user_command.equalsIgnoreCase("run"))
                {
                    kernel.runs(argument);
                }
                else if(user_command.equalsIgnoreCase("rm")) {
                    kernel.rm(argument);
                }
                else if(user_command.equalsIgnoreCase("rd")){
                    kernel.rd(argument);
                }
                else{
                    System.out.println("Unknown command.");
                }
            }
            else{
                if(user_command.isEmpty())
                    continue;

                if(user_command.equalsIgnoreCase("dir")){
                    kernel.dir();
                }
                else if(user_command.equalsIgnoreCase("dirs")){
                    kernel.dirs();
                }
                else if(user_command.equalsIgnoreCase("ps")){
                    kernel.ps();
                }
                else if(user_command.equalsIgnoreCase("mem")){
                    kernel.mem();
                }
                else if(user_command.equalsIgnoreCase("exit")){
                    System.out.println("Booting down! ");
                    break;
                }
                else if(user_command.equalsIgnoreCase("help")){
                    System.out.println("Commands:");
                    // CD [dir_name]     prelazi u ciljeni direktorijum
                    // DIR               lista trenutnih direktorijuma
                    // PS                lista procesa i osnovne informacije o njima
                    // mkdir [dir_name]  napravi direktorijum
                    // run [filename]    pokrece_proces
                    // mem               Zauzece memorije
                    // exit              Gasi OS
                    // rm                Uklanja datoteku
                    // rd [file_name]    Ispisi fajl
                    System.out.println("cd [dir_name]     Prelazi u ciljeni direktorijum");
                    System.out.println("dir               lista trenutnih direktorijuma");
                    System.out.println("dirs              lista svih direktorijuma");
                    System.out.println("ps                lista procesa i osnovne informacije o njima");
                    System.out.println("mkdir [dir_name]  napravi direktorijum");
                    System.out.println("run [filename]    pokrece proces");
                    System.out.println("mem               Zauzece memorije");
                    System.out.println("rm [filename]     Uklanja datoteku");
                    System.out.println("rd [filename]     Ispisi datoteku");
                    System.out.println("exit              Gasi OS");

                }
                else{
                    System.out.println("Unknown command.");
                }
            }
            System.out.println();

        }

        kernelThread.interrupt();
        System.exit(0);
    }
}
