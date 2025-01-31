package GUI;

import OS.Directory;
import OS.Kernel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainScreenGUI {

    private Kernel kernel;

    MainScreenGUI(Kernel kernel) {
        this.kernel = kernel;
    }

    public void MainScreen(Stage primaryStage){
        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        HBox hDir = new HBox(10);
        hDir.setAlignment(Pos.CENTER);

        VBox vList = new VBox(5);

        Label lblCurrentDirectory = new Label("Current Directory: "+kernel.getCurrentDirectory());
        lblCurrentDirectory.setTextFill(Color.GRAY);
        ListView<Directory> dirs = new ListView<>();
        dirs.setOnMouseClicked(event->directoryMove(event,dirs,lblCurrentDirectory));
        listCurrentDirectoryFiles(dirs);

        vList.getChildren().addAll(lblCurrentDirectory,dirs);
        hDir.getChildren().addAll(vList);


        VBox vKomande = new VBox(10);

        Button btnOpen = new Button("Open file");
        btnOpen.setMinWidth(100);
        btnOpen.setOnAction(_->{
            Directory selected = dirs.getSelectionModel().getSelectedItem();
           if(selected == null)
               return;
           if(!selected.getName().contains("."))
               return;
            FileOpenGUI fileOpenGUI = new FileOpenGUI(selected.getName(), selected.getCurrentDirectory(),kernel);
            fileOpenGUI.start();

        });

        Button btnRun = new Button("Run file");
        btnRun.setMinWidth(100);
        btnRun.setOnAction(_->{
            Directory selected = dirs.getSelectionModel().getSelectedItem();
            if(selected == null)
                return;
            if(!selected.getName().contains(".file"))
                return;
            kernel.runs(selected.getCurrentDirectory());
        });

        TextField txtFileName = new TextField();
        txtFileName.setMinWidth(100);
        txtFileName.setPromptText("Enter file/directory name");
        txtFileName.setVisible(false);

        Button btnMakeFile = new Button("Make file");
        btnMakeFile.setMinWidth(100);
        btnMakeFile.setOnAction(_->{
            if(btnMakeFile.getText().equals("Make file"))
            {
                btnMakeFile.setText("Confirm");
                txtFileName.setVisible(true);
                return;
            }

            btnMakeFile.setText("Make file");
            txtFileName.setVisible(false);
            String userInput = txtFileName.getText();
            txtFileName.setText("");
            if(userInput.contains(".")){
                kernel.mkfile(userInput);
            }
            else {
                kernel.mkdir(userInput);
            }
            listCurrentDirectoryFiles(dirs);

        });

        Button btnRemoveFile = new Button("Remove file");
        btnRemoveFile.setMinWidth(100);
        btnRemoveFile.setOnAction(_->{
            Directory selected = dirs.getSelectionModel().getSelectedItem();
            if(selected == null)
                return;
            if(selected.getName().contains(".")){
                kernel.rm(selected.getName());
                listCurrentDirectoryFiles(dirs);
            }
        });


        vKomande.getChildren().addAll(btnOpen,btnRun,txtFileName,btnMakeFile,btnRemoveFile);
        hDir.getChildren().addAll(vKomande);


        root.getChildren().add(hDir);

        Button btnTaskManager = new Button("Task Manager");
        btnTaskManager.setMinWidth(100);
        btnTaskManager.setOnAction(_->{
            TaskManager taskManager = new TaskManager(kernel);
            taskManager.TaskManagerStart();
        });
        root.getChildren().add(btnTaskManager);

        Scene scene = new Scene(root,400,400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Main Screen");
        primaryStage.show();

    }

    private void listCurrentDirectoryFiles(ListView<Directory> dirs){
        dirs.getItems().clear();

        Directory current = kernel.getCurrentDirectory(new Directory());
        if(kernel.getCurrentDirectory(new Directory()).getPreviousDirectory()!=null)
        {
            Directory back = new Directory();
            back.setName("↵");
            dirs.getItems().add(back);
        }

        dirs.getItems().addAll(current.getAllDirectories());

        for(String fileNameAddress:current.getAllFilesNamesAddress()){
            String fileName = fileNameAddress.split("&")[0];
            Directory newDirectory = new Directory();
            newDirectory.setCurrentDirectory(current.getCurrentDirectory()+"/"+fileName);
            newDirectory.setName(fileName);
            dirs.getItems().add(newDirectory);
        }
    }

    private void directoryMove(MouseEvent event,ListView<Directory> dirs,Label lbl){
        Directory selectedDirectory = dirs.getSelectionModel().getSelectedItem();

        if(event.getClickCount()<2)
            return;
        if(selectedDirectory==null)
            return;
        if(selectedDirectory.getName().contains(".")){
            return;
        }

        try{

        if(selectedDirectory.getName().contains("↵"))
            kernel.setCurrentDirectory(kernel.getFsm().navigateToDirectory(kernel.getCurrentDirectory(new Directory()).getPreviousDirectory()));
        else
            kernel.setCurrentDirectory(kernel.getFsm().navigateToDirectory(selectedDirectory.getCurrentDirectory()));

        }catch (Exception e){
            System.out.println("\nError try again");
            System.out.println();
        }

        listCurrentDirectoryFiles(dirs);
        lbl.setText("Current Directory: "+kernel.getCurrentDirectory());
    }
}
