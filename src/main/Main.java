package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.model.Dao.HibernateUtil;

public class Main extends Application {
    static HibernateUtil hibUtil = new HibernateUtil();
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("./FXML/Login.fxml"));
        primaryStage.setTitle("Biblioteka");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                hibUtil.CloseConnection();
                Platform.exit();
                System.exit(0);
            }
        });
    }



    public static void main(String[] args) {
        hibUtil.setSessionFactory("main/cfg/hibernate.cfg.xml");
        launch(args);
    }
}
