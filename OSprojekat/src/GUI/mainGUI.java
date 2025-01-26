package GUI;


import OS.Directory;
import Terminal.Terminal;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import OS.Kernel;

public class mainGUI extends Application {

    public static Kernel kernel;

    public static void main(String[] args) {
        kernel = new Kernel();

        Terminal terminal = new Terminal(kernel);

        Thread terminalThread = new Thread(terminal::start);
        terminalThread.start();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainScreen(primaryStage);
    }

    private void MainScreen(Stage primaryStage){
        VBox root = new VBox(10);

        HBox hDir = new HBox(10);

        ListView<Directory> dirs = new ListView<>();
        dirs.getItems().addAll(kernel.getCurrentDirectory(new Directory()).getAllDirectories());

        hDir.getChildren().addAll(dirs);




        root.getChildren().add(hDir);

        Scene scene = new Scene(root,600,400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    private void taskManager(){
        Stage stage = new Stage();

        stage.show();
    }
}
