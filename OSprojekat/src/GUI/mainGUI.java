package GUI;


import Terminal.Terminal;
import javafx.application.Application;
import javafx.stage.Stage;

import OS.Kernel;

public class mainGUI extends Application {

    public static Kernel kernel;
    public static Thread terminalThread;

    public static void main(String[] args) {
        kernel = new Kernel();

        Terminal terminal = new Terminal(kernel);

        terminalThread = new Thread(terminal::start);
        terminalThread.start();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainScreenGUI mainScreen = new MainScreenGUI(kernel);
        mainScreen.MainScreen(primaryStage);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("\nBooting down! ");
        terminalThread.interrupt();
        System.exit(0);
    }
}
