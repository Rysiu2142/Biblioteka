package main.Controllers.Librarian;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import main.model.Dao.ServiceDAO;
import main.model.Dao.UsersDAO;
import main.model.Dao.OrdersDAO;
import main.model.entity.BooksEntity;
import main.model.entity.AuthEntity;
import main.model.entity.UsersEntity;
import main.model.entity.OrdersEntity;
import main.utils.Bcrypt;
import main.utils.Dialogs;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.utils.Validation;


import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler panelu czytelnicy dla bibliotekarza
 */
public class ReaderTab extends ServiceDAO implements Initializable {


    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<UsersEntity, String> firstNameColumn;

    @FXML
    private TableColumn<UsersEntity, String> lastNameColumn;

    @FXML
    private TableColumn<UsersEntity, String> emailColumn;

    @FXML
    private TableColumn<UsersEntity, String> phoneNumberColumn;
    @FXML
    private TableColumn<UsersEntity, String> statusColumn;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField phoneNumberInput;

    @FXML
    private TextField passwordInput;

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        tableView.setItems(usersDAO.getUsers());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       initTableView();
       tableView.setItems(usersDAO.getUsers());

        //edid
        tableView.setEditable(true);
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());


    }

    /**
     * Initializacja tabeli
     */
    public void initTableView() {
        this.firstNameColumn = (TableColumn) tableView.getColumns().get(0);
        this.firstNameColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getFirstName().toString()));

        this.lastNameColumn = (TableColumn) tableView.getColumns().get(1);
        this.lastNameColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getLastName().toString()));

        this.emailColumn = (TableColumn) tableView.getColumns().get(2);
        this.emailColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getAuthId().getEmail().toString()));

        this.phoneNumberColumn = (TableColumn) tableView.getColumns().get(3);
        this.phoneNumberColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getPhoneNumber().toString()));

        this.statusColumn = (TableColumn) tableView.getColumns().get(4);
        this.statusColumn.setCellValueFactory(data-> {
            if(data.getValue().getAuthId().getActive() == 1){
                return new ReadOnlyStringWrapper("Aktywne");
            }else{
                return  new ReadOnlyStringWrapper("Nieaktywne");
            }

        });

    }

    /**
     * Metoda zmienniajca Imie czytlenika w bazie danych
     * @param personCellEditEvent event edycji w UI
     */
    @FXML
    void firstNameEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validText(personCellEditEvent.getNewValue())){
            System.out.println("dane poprawne");
            try{
                person.setFirstName(personCellEditEvent.getNewValue());
                usersDAO.update(person);

            }catch (Exception e){
                System.out.println("dane poprawne ale cos sie odjaniepawla");
                Dialogs.errorAlert(e.getMessage());
            }
        }else{
            Dialogs.errorAlert("Podaj poprawne imię!");
        }
        tableView.refresh();

    }

    /**
     * Metoda zmienniajca Nazwisko czytlenika w bazie danych
     * @param personCellEditEvent event zatwierdzenia edycji w UI
     */
    @FXML
    void lastNameEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validText(personCellEditEvent.getNewValue())){
            System.out.println("git");
            try{
                person.setLastName(personCellEditEvent.getNewValue());
                usersDAO.update(person);
            }catch (Exception e){
                System.out.println("dane poprawne ale cos sie odjaniepawla");
                Dialogs.errorAlert(e.getMessage());
            }
        }else{
            Dialogs.errorAlert("Podaj poprawne nazwisko!");
        }
        tableView.refresh();
    }

    /**
     * Metoda zmienniajca email czytlenika w bazie danych
     * @param personCellEditEvent event zatwierdzenia edycji w UI
     */
    @FXML
    void emailEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validEmail(personCellEditEvent.getNewValue())){
            System.out.println("gitara");
            try{
                person.getAuthId().setEmail(personCellEditEvent.getNewValue());
                authDAO.update(person.getAuthId());
            } catch (Exception e) {
                System.out.println("dane poprawne ale cos sie odjaniepawla");
                Dialogs.errorAlert(e.getMessage());
            }
        }else{
            Dialogs.errorAlert("Podaj poprawny email!");
        }
        tableView.refresh();
    }

    /**
     * Metoda zmienniajca nr telefonu czytlenika w bazie danych
     * @param personCellEditEvent event zatwierdzenia edycji w UI
     */
    @FXML
    void phoneEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validPhoneNumber(personCellEditEvent.getNewValue())){
            System.out.println("gitara");
            try{
                person.setPhoneNumber(personCellEditEvent.getNewValue());
                usersDAO.update(person);
            } catch (Exception e){
                System.out.println("dane poprawne ale cos sie odjaniepawla");
                Dialogs.errorAlert(e.getMessage());
            }
        }else{
            Dialogs.errorAlert("Podaj poprawny nr telefonu!");
        }
        tableView.refresh();
    }


    /**
     * Metoda dodajaca użytkownika o roli czytelnik.
     * Wykorzystuje pola textowe w UI do uzyskania danych
     */
    @FXML
    void addUser() {

        String role = new String("czytelnik");

        if (Validation.validEmail(emailInput.getText()) && Validation.validText(firstNameInput.getText()) && Validation.validPassword(passwordInput.getText()) &&  Validation.validText(lastNameInput.getText()) && Validation.validPhoneNumber(phoneNumberInput.getText())) {
            System.out.println("poprawne dane");

            try {
                AuthEntity newAuth = new AuthEntity(emailInput.getText(), Bcrypt.hash(passwordInput.getText()), role);
                authDAO.save(newAuth);
                UsersEntity newUser = new UsersEntity(firstNameInput.getText(), lastNameInput.getText(), phoneNumberInput.getText(), newAuth);
                usersDAO.save(newUser);
                clearInputs();

                Dialogs.errorAlert("użytkownik dodany");


            } catch (Exception e) {
                System.out.println("Błąd podczas dodania usera");
                Dialogs.errorAlert(e.getMessage());
            }
        } else {
            Dialogs.errorAlert("Podaj poprawne dane");
        }
        usersDAO.findAll();
        tableView.setItems(usersDAO.getUsers());

    }

    /**
     * czyszczenie inputu teskstowego
     */
    public void clearInputs(){
        firstNameInput.setText("");
        lastNameInput.setText("");
        phoneNumberInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
    }


//    @FXML
//    public void delUser() {
//
//
//        if(!tableView.getSelectionModel().isEmpty()){
//
//            UsersEntity xd = (UsersEntity)tableView.getSelectionModel().getSelectedItem();
//            ordersDAO.removeUsersOrders(Integer.parseInt(xd.getId().toString()));
//            usersDAO.delete(xd);
//            authDAO.delete(xd.getAuthId());
//
//        }else{
//            Dialogs.errorAlert("Wybierz użytkownika do usunięcia!");
//        }
//
//        usersDAO.findAll();
//        tableView.setItems(usersDAO.getUsers());
//    }

    /**
     * Metoda usuwajca zaznaczonego użytkownika
     */
    @FXML
    public void delUser() {
        System.out.println("aaa");
        if(!tableView.getSelectionModel().isEmpty()){
            UsersEntity user = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
            if(user.getAuthId().getActive() == 1){
                user.getAuthId().setActive(0);
            }else{
                user.getAuthId().setActive(1);
            }
            try{
                authDAO.update(user.getAuthId());
                System.out.println(user.getAuthId());
                usersDAO.findAll();
                tableView.getItems().clear();
                tableView.setItems(usersDAO.getUsers());
            }catch(Exception e){
                Dialogs.errorAlert(e.getMessage());
            }



        }else{
            Dialogs.errorAlert("Wybierz użytkownika");
        }


    }





}