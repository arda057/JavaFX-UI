package JavaFXexample;

import javax.smartcardio.Card;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application 
{
    @Override
    public void start(Stage stage)
    {
        Label label = new Label("Login");
        label.getStyleClass().add("title");

        Label userlabel = new Label("Username");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        

        Label passlabel = new Label("Password");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        

        HBox usernameBox = new HBox(10, userlabel, usernameField);
        HBox passwordBox = new HBox(14, passlabel, passwordField);

        usernameBox.setAlignment(Pos.CENTER);
        passwordBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");

        Label resultLabel = new Label();

        loginButton.setOnAction(e ->
            {
                String username = usernameField.getText();
                String password = passwordField.getText();
                if((username.equals("admin")) && (password.equals("1234")) ){
                    resultLabel.setText("Login successful");
                    resultLabel.setStyle("-fx-text-fill: green;");
                }
                else{
                    resultLabel.setText("Wrong username or password");
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            }
        );
   
        //1. CheckBox Kutusu
        CheckBox checkBox = new CheckBox("Remember me");
        checkBox.setOnAction(e ->
            {
                if(checkBox.isSelected()){
                    System.out.println("Selected");
                }
                else
                {
                    System.out.println("Not selected");
                }
            }
        );

        //2. ToggleGroupe sadece bir düğmenin seçilmesini sağlar
        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        ToggleGroup group = new ToggleGroup();
        male.setToggleGroup(group);
        female.setToggleGroup(group);

        //3. ComboBox
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setValue("Java");
        //comboBox.setPromptText("?");
        comboBox.getItems().add("Java");
        comboBox.getItems().add("Python");
        comboBox.getItems().add("C++");
        /* comboBox.getItems().addAll(
            "Java",
            "Python",
            "C++"
        ); */

        //4. Text Area
        TextArea textArea = new TextArea();
        textArea.setPromptText("Write something...");

        //5. ListView
        /* ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
            "Apple",
            "Banana",
            "Orange"
        );
        listView.setPrefHeight(50); */

        Button clearButton = new Button("Clear");
        clearButton.setId("clearButton");

        clearButton.setOnAction(e ->
            {
                usernameField.setText("");
                passwordField.setText("");
                checkBox.setSelected(false);
                male.setSelected(false);
                female.setSelected(false);
                comboBox.setValue("Java");
                textArea.setText("");
                //listView.getSelectionModel().clearSelection();
            }
        );

        CheckBox darkthemCheckBox = new CheckBox("Dark theme");

        VBox card = new VBox(20, label, usernameBox, passwordBox,loginButton,resultLabel,checkBox, male, female,comboBox,textArea,
            //listView, 
        clearButton,darkthemCheckBox);

        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(350);
        card.setMaxHeight(680);
        card.getStyleClass().add("card");

        StackPane root = new StackPane(card);
        Scene scene = new Scene(root, 700, 700);
        

        darkthemCheckBox.setOnAction(e ->
        {
            if(darkthemCheckBox.isSelected())
            {
                card.setStyle(
                    "-fx-background-color: #000000;"
                );

                label.setStyle("-fx-text-fill: white;");
                userlabel.setStyle("-fx-text-fill: white;");
                passlabel.setStyle("-fx-text-fill: white;");
            }
            else
            {
                card.setStyle(
                    "-fx-background-color: white;"
                );

                label.setStyle("-fx-text-fill: black;");
                userlabel.setStyle("-fx-text-fill: black;");
                passlabel.setStyle("-fx-text-fill: black;");
            }
        });

        stage.setTitle("Login Screen");
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
    
}
