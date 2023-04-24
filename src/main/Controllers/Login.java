package main.Controllers;

import com.itextpdf.text.pdf.parser.clipper.Paths;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import main.model.Dao.ServiceDAO;
import main.model.entity.UsersEntity;
import main.utils.Dialogs;
import main.utils.InitialDatabaseData;
import main.utils.SceneManagment;
import main.utils.Bcrypt;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.Charset;

public class Login extends ServiceDAO implements Initializable {
    private static final String FXML_MAIN_PANEL = "../FXML/Main.fxml";
    private static final String TEMPORARY_LOGIN = "user";
    private static final String TEMPORARY_PASSWORD = "1234";

    @FXML
    private Button loadData;
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Button signIn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login.setText("magazynier@magazynier");
        password.setText("12345678a");
        InitialDatabaseData init = new InitialDatabaseData();
        init.checkIfDataLoaded();
    }

    /**
     * Metoda obslugi logowania
     * @param event MouseEvent
     * @throws IOException bład SceneMagment
     */
    @FXML
    public void handleLogin(MouseEvent event) throws IOException{

        if(!login.getText().isEmpty() &&  !password.getText().isEmpty()){
            List<UsersEntity> loggedUser = usersDAO.findByEmailAndPassword(login.getText(), password.getText());

            if(!loggedUser.isEmpty()){
                if(Bcrypt.checkPass(password.getText(),loggedUser.get(0).getAuthId().getPassword())){
                    if(loggedUser.get(0).getAuthId().getActive()==1) {

                        System.out.println("Zalogowany");
                        System.setProperty("name", loggedUser.get(0).getFirstName().toString()+" "+loggedUser.get(0).getLastName().toString() );
                        System.setProperty("userId", loggedUser.get(0).getId().toString());
                        System.setProperty("role", loggedUser.get(0).getAuthId().getRole());
                        SceneManagment.changeStage(FXML_MAIN_PANEL, event, this);
                    }else{
                        Dialogs.errorAlert("Konto zablokowane!");
                    }
                }else{
                    Dialogs.errorAlert("Bledne haslo!");
                }

            }else{
                Dialogs.errorAlert("Nie ma takiego użytkownika!");
            }

        }else{
            Dialogs.errorAlert("Wypełnij wszystkie pola!");
        }


    }


}
