package GUI;

import OS.*;
import OS.Process;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class TaskManager {
    private Kernel kernel;

    private VBox vView;
    private Scene scene;

    public TaskManager(Kernel kernel) {
        this.kernel = kernel;
    }

    public void TaskManagerStart(){
        final int[] view = {1};
        Stage stage = new Stage();

        HBox root = new HBox(30);
        root.setPadding(new Insets(15));
        vView = new VBox(5);

        VBox selection = new VBox(25);

        Button btnCPUView = new Button("PCS");
        btnCPUView.setOnAction(_-> {
            initPCSView();
            view[0] = 2;
        });
        btnCPUView.setPrefSize(50,50);
        btnCPUView.setMinSize(50,50);

        Button btnRAMView = new Button("MEM");
        btnRAMView.setOnAction(_-> {
            initRAMView();
            initHDDView();
            view[0] = 1;
        });
        btnRAMView.setPrefSize(50,50);
        btnRAMView.setMinSize(50,50);

        selection.getChildren().addAll(btnCPUView, btnRAMView);



        root.getChildren().addAll(selection,vView);

        scene = new Scene(root,700,870);
        stage.setTitle("Task Manager");
        stage.setScene(scene);
        stage.show();

        initRAMView();
        initHDDView();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100),
                        _ -> {
                    switch(view[0]){
                        case 1:
                            updateRAMView();
                            updateHDDView();
                            break;
                        case 2:
                            updatePCSView();
                            updateCPUView();
                            break;

                    }
                        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }


    // RAM

    private ListView<Frame> frameViews;
    private Label lblUsedMemory;

    private void initRAMView(){
        vView.getChildren().clear();


        VBox vRAM = new VBox(10);

        Label lblRAM = new Label("RAM");
        lblRAM.setTextFill(Color.GRAY);

        frameViews = new ListView<>();
        frameViews.getItems().addAll(kernel.getMm().getRam().getFrames());
        frameViews.setMinSize(500,300);
        frameViews.setPrefSize(500,300);

        int freeFrames = kernel.getMm().getRam().getFrameCount()-kernel.getMm().getRam().remainingSpace();
        lblUsedMemory = new Label("RAM usage: " + freeFrames* Page.pageSize + "/" + kernel.getMm().getRam().getFrameCount()*Page.pageSize);


        vRAM.getChildren().addAll(lblRAM,frameViews,lblUsedMemory);
        vRAM.setStyle("-fx-border-color: gray");
        vRAM.setPadding(new Insets(30));

        vView.getChildren().add(vRAM);
        updateRAMView();
    }

    private void updateRAMView(){
        frameViews.getItems().clear();
        frameViews.getItems().addAll(kernel.getMm().getRam().getFrames());
        int freeFrames = kernel.getMm().getRam().getFrameCount()-kernel.getMm().getRam().remainingSpace();
        lblUsedMemory = new Label("RAM usage: " + freeFrames* Page.pageSize + "/" + kernel.getMm().getRam().getFrameCount()*Page.pageSize);
    }

    // HDD
    private ListView<String> memoryViews;
    private Label lblUsedHDDMemory;

    private void initHDDView(){

        VBox vHDD = new VBox(10);

        Label lblHDD = new Label("HDD");
        lblHDD.setTextFill(Color.GRAY);
        memoryViews = new ListView<>();
        lblUsedHDDMemory = new Label();
        String[] addresses = kernel.getHm().getHDD().getAllMemory();
        int i = 0;
        for(String content:addresses){
            if(content==null)
                memoryViews.getItems().add(kernel.getHm().getHDD().encodeAddress(i++) + ":\tEMPTY");
            else
                memoryViews.getItems().add(kernel.getHm().getHDD().encodeAddress(i++) + ":\t" + content.replaceAll("\n",""));
        }

        memoryViews.setMinSize(500,300);
        memoryViews.setPrefSize(500,300);

        lblUsedHDDMemory = new Label("HDD usage: " + (kernel.getHm().getHDD().getSize()-kernel.getHm().getHDD().remainingSpace())*Page.pageSize + "/" + kernel.getHm().getHDD().getSize()*Page.pageSize);

        vHDD.getChildren().addAll(lblHDD,memoryViews,lblUsedHDDMemory);
        vHDD.setStyle("-fx-border-color: gray");
        vHDD.setPadding(new Insets(30));

        vView.getChildren().addAll(vHDD);
        updateHDDView();
    }

    private void updateHDDView(){
        memoryViews.getItems().clear();
        String[] addresses = kernel.getHm().getHDD().getAllMemory();
        int i = 0;
        for(String content:addresses){
            if(content==null)
                memoryViews.getItems().add(kernel.getHm().getHDD().encodeAddress(i++) + ":\tEMPTY");
            else
                memoryViews.getItems().add(kernel.getHm().getHDD().encodeAddress(i++) + ":\t" + content.replaceAll("\n",""));
        }
        lblUsedHDDMemory.setText("HDD usage: " + (kernel.getHm().getHDD().getSize()-kernel.getHm().getHDD().remainingSpace())*Page.pageSize + "/" + kernel.getHm().getHDD().getSize()*Page.pageSize);
    }

    // PCS
    private ListView<Process> pcsViews;
    private Label lblProcessNum;

    private void initPCSView(){
        vView.getChildren().clear();
        VBox vPCS = new VBox(10);

        Label lblPCS = new Label("Processes");
        lblPCS.setTextFill(Color.GRAY);

        pcsViews = new ListView<>();
        pcsViews.getItems().addAll(kernel.getPm().getAllProcesses());
        lblProcessNum = new Label("Number of processes: " + kernel.getPm().getProcessNumber());

        Button btnBlock = new Button("Block/Unblock");
        btnBlock.setOnAction(_->{
            if(pcsViews.getSelectionModel().getSelectedItem()==null)
                return;
            Process p = pcsViews.getSelectionModel().getSelectedItem();
            if(p.getState()== ProcessState.BLOCKED)
                kernel.unblock(Integer.toString(p.getID()));
            else
                kernel.block(Integer.toString(p.getID()));
        });

        CheckBox cxRoundRobin = new CheckBox("Enable Round Robin");
        cxRoundRobin.setOnAction(_-> kernel.setRR(cxRoundRobin.isSelected()));

        pcsViews.setMinSize(500,300);
        pcsViews.setPrefSize(500,300);

        vPCS.getChildren().addAll(lblPCS,pcsViews,lblProcessNum,btnBlock,cxRoundRobin);
        vPCS.setStyle("-fx-border-color: gray");
        vPCS.setPadding(new Insets(30));

        vView.getChildren().add(vPCS);
        initCPUView();
    }

    private void updatePCSView(){
        Process p = pcsViews.getSelectionModel().getSelectedItem();
        pcsViews.getItems().clear();

        pcsViews.getItems().addAll(kernel.getPm().getAllProcesses());

        if(pcsViews.getItems().contains(p))
            pcsViews.getSelectionModel().select(p);
        lblProcessNum = new Label("Number of processes: " + kernel.getPm().getProcessNumber());
    }


    Label lblCurrentProcess;
    private void initCPUView(){
        VBox vCPU = new VBox(10);

        Label lblCPU = new Label("CPU");
        lblCPU.setTextFill(Color.GRAY);

        lblCurrentProcess = new Label("Current process: " + kernel.getPm().getCurrentProcess());

        Label lblSettings = new Label("CPU Configs");
        HBox hBox = new HBox(5);

        TextField txtCPUinstructions = new TextField();
        txtCPUinstructions.textProperty().addListener((_, _, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCPUinstructions.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label lblSlash = new Label("instructions");
        /*lblSlash.setText("instructions /");
        TextField txtCPUSeconds = new TextField();
        txtCPUSeconds.textProperty().addListener((_, _, newValue) -> {
            if (!newValue.equals(".")) {
                return;
            }
            if (!newValue.matches("\\d*|\\.")) {
                txtCPUSeconds.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        Label lblSeconds = new Label("seconds");*/

        Button btnApply = new Button("Apply");
        btnApply.setOnAction(_ -> {
            int instructions = 1;
            if(!txtCPUinstructions.getText().isEmpty()){
                instructions = Integer.parseInt(txtCPUinstructions.getText());
            }
            int miliseconds=1000;
            //miliseconds = Math.max((int)(Float.parseFloat(txtCPUSeconds.getText())*1000),10);
            kernel.setCPUspeed(instructions,miliseconds);
        });

        //hBox.getChildren().addAll(txtCPUinstructions,lblSlash,txtCPUSeconds,lblSeconds,btnApply);
        hBox.getChildren().addAll(txtCPUinstructions,btnApply);

        vCPU.getChildren().addAll(lblCPU,lblCurrentProcess,lblSettings,hBox);


        vCPU.setStyle("-fx-border-color: gray");
        vCPU.setPadding(new Insets(30));

        vView.getChildren().add(vCPU);
    }

    private void updateCPUView(){
        lblCurrentProcess.setText("Current process: " + kernel.getPm().getCurrentProcess());
    }
}
