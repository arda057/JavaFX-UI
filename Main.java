package JavaFXexample;

import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader =
                new FXMLLoader(
                    getClass().getResource("login.fxml")
                );

        Scene scene =
                new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);

        stage.setTitle("Login");

        stage.show();

        try {
            Connection conn = DatabaseManager.connect();
            System.out.println("Connected");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseManager.createTable();
        Student student = new Student(1, "Dean", 2.10, "Gastronomy");
        DatabaseManager.insertStudent(student);
    }

    public static void main(String[] args)
    {
        launch();
    }
}