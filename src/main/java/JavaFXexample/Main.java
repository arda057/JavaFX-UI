package JavaFXexample;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    @Override
    public void start(Stage stage) throws Exception
    {
        DatabaseManager.initialize();

        FXMLLoader loader =
                new FXMLLoader(
                    getClass().getResource("/JavaFXexample/fxml/login.fxml")
                );

        Scene scene =
                new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("/JavaFXexample/css/style.css").toExternalForm());

        stage.setScene(scene);

        stage.setTitle("Login");

        stage.setResizable(false);

        stage.show();

        try {
            Connection conn = DatabaseManager.connect();
            System.out.println("Connected");
            conn.close();
        } catch (Exception e) {
            log.error("Could not verify database connection", e);
        }
        
    }

    public static void main(String[] args)
    {
        launch();
    }
}