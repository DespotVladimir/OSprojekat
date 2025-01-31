package GUI;

import OS.Kernel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class FileOpenGUI {
    private String fileName;
    private String filePath;
    private Kernel kernel;

    public FileOpenGUI(String fileName, String filePath,Kernel kernel) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.kernel = kernel;
    }

    public void start(){
        Stage stage = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));


        root.setAlignment(Pos.CENTER);
        TextArea txtInput = new TextArea();
        txtInput.setText(kernel.getFileContents(filePath));
        txtInput.setMinHeight(300);
        txtInput.setWrapText(true);

        Button btnSave = new Button("Save");
        btnSave.setMinWidth(150);
        btnSave.setOnAction(_ -> {
            kernel.updateFileContent(filePath, txtInput.getText());
            stage.close();
        });


        root.getChildren().addAll(txtInput, btnSave);
        Scene scene = new Scene(root,500,400);
        stage.setScene(scene);
        stage.setTitle(fileName);
        stage.show();
    }

}
