package main.Controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import main.model.Dao.ServiceDAO;
import main.model.entity.AuthEntity;
import main.model.entity.UsersEntity;
import main.utils.Bcrypt;
import main.utils.Validation;
import main.utils.Dialogs;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;
import java.util.List;
import javafx.collections.FXCollections;
import java.util.ArrayList;

/**
 * Kontroler zakladki pracownikow
 */
public class EmployeeTab  extends ServiceDAO implements Initializable {

    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<UsersEntity, String> firstNameColumn;
    @FXML
    private TableColumn<UsersEntity, String> lastNameColumn;
    @FXML
    private TableColumn<UsersEntity, String> emailColumn;
    @FXML
    private TableColumn<UsersEntity, String> roleColumn;
    @FXML
    private TableColumn<UsersEntity, String> statusColumn;
    @FXML
    private TableColumn<UsersEntity, String> phoneNumberColumn;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private ComboBox roleInput;

    @FXML
    private TextField phoneNumberInput;

    /**
     * Metoda odswiezajaca dane w tabeli
     */
    public void refreshTable(){
        tableView.setItems(usersDAO.getEmployees());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        adminTab.setDisable(true);
        List<String> roles = new ArrayList();
        roles.add("czytelnik");
        roles.add("magazynier");
        roles.add("bibliotekarz");
        roleInput.setItems(FXCollections.observableArrayList(roles));

        initTableView();
        tableView.setItems(usersDAO.getEmployees());
        this.tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            phoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        });
    }

    /**
     * Metoda initalizujaca tabele
     */
    private void initTableView() {
        this.firstNameColumn = (TableColumn) tableView.getColumns().get(0);
        this.firstNameColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getFirstName().toString()));
        this.lastNameColumn = (TableColumn) tableView.getColumns().get(1);
        this.lastNameColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getLastName().toString()));
        this.emailColumn = (TableColumn) tableView.getColumns().get(2);
        this.emailColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getAuthId().getEmail().toString()));
        this.phoneNumberColumn = (TableColumn) tableView.getColumns().get(3);
        this.phoneNumberColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getPhoneNumber().toString()));
        this.roleColumn = (TableColumn) tableView.getColumns().get(4);
        this.roleColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getAuthId().getRole().toString()));
        this.statusColumn = (TableColumn) tableView.getColumns().get(5);
        this.statusColumn.setCellValueFactory(data-> {
            if(data.getValue().getAuthId().getActive() == 1){
                return new ReadOnlyStringWrapper("Aktywne");
            }else{
                return  new ReadOnlyStringWrapper("Nieaktywne");
            }

        });
    }


    /**
     * Metoda umozliwajaca edycje imienia pracownika
     * @param personCellEditEvent wybrana komorka w tabeli
     */
    @FXML
    void firstNameEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validText(personCellEditEvent.getNewValue())){
            person.setFirstName(personCellEditEvent.getNewValue());
            usersDAO.update(person);
        }else{
            Dialogs.errorAlert("Podaj poprawne imię!");
        }
        tableView.refresh();

    }
    /**
     * Metoda umozliwajaca edycje nazwiska pracownika
     * @param personCellEditEvent wybrana komorka w tabeli
     */
    @FXML
    void lastNameEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validText(personCellEditEvent.getNewValue())){
            person.setLastName(personCellEditEvent.getNewValue());
            usersDAO.update(person);
        }else{
            Dialogs.errorAlert("Podaj poprawne nazwisko!");
        }
        tableView.refresh();
    }
    /**
     * Metoda umozliwajaca edycje emailu pracownika
     * @param personCellEditEvent wybrana komorka w tabeli
     */
    @FXML
    void emailEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validEmail(personCellEditEvent.getNewValue())){
            person.getAuthId().setEmail(personCellEditEvent.getNewValue());
            authDAO.update(person.getAuthId());
        }else{
            Dialogs.errorAlert("Podaj poprawny email!");
        }
        tableView.refresh();
    }
    /**
     * Metoda umozliwajaca edycje telefonu pracownika
     * @param personCellEditEvent wybrana komorka w tabeli
     */
    @FXML
    void phoneEdit(TableColumn.CellEditEvent<String, String> personCellEditEvent) {
        UsersEntity person = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
        if(Validation.validPhoneNumber(personCellEditEvent.getNewValue())){
            person.setPhoneNumber(personCellEditEvent.getNewValue());
            usersDAO.update(person);
        }else{
            Dialogs.errorAlert("Podaj poprawny nr telefonu!");
        }
        tableView.refresh();
    }

    /**
     * Metoda dodajaca pracownika
     */
    @FXML
    void handleAddEmployee() {

        if (Validation.validText(firstNameInput.getText()) && Validation.validText(lastNameInput.getText()) && Validation.validPhoneNumber(phoneNumberInput.getText())
        && Validation.validEmail(emailInput.getText()) && Validation.validPassword(passwordInput.getText()) && !roleInput.getSelectionModel().isEmpty()){

            try{
                AuthEntity newAuth = new AuthEntity(emailInput.getText(),Bcrypt.hash(passwordInput.getText()),roleInput.getSelectionModel().getSelectedItem().toString());
                authDAO.save(newAuth);
                UsersEntity newUser = new UsersEntity(firstNameInput.getText(),lastNameInput.getText(),phoneNumberInput.getText(),newAuth);
                usersDAO.save(newUser);

                clearInputs();
            }catch (Exception e){

                System.out.println("Błąd podczas dodania usera");
                Dialogs.errorAlert(e.getMessage());
            }
        }else{
            Dialogs.errorAlert("Podaj poprawne dane!");
        }
        usersDAO.findAllEmployees();
        tableView.setItems(usersDAO.getEmployees());
    }

    /**
     * Metoda zmieniajaca status pracownika na zablokowany i odwrotnie
     */
    @FXML
    void handleBlockEmployee(){
        if(!tableView.getSelectionModel().isEmpty()){
            UsersEntity user = (UsersEntity) tableView.getSelectionModel().getSelectedItem();
            if(user.getAuthId().getActive() == 1){
                user.getAuthId().setActive(0);
            }else{
                user.getAuthId().setActive(1);
            }
          try{
              authDAO.update(user.getAuthId());
              tableView.setItems(usersDAO.findAllEmployees());
          }catch(Exception e){
              Dialogs.errorAlert(e.getMessage());
          }

        }else{
            Dialogs.errorAlert("Wybierz użytkownika");
        }
    }

    /**
     * Czyszczenie wartosci w formularzach
     */
    public void clearInputs(){
        firstNameInput.setText("");
        lastNameInput.setText("");
        phoneNumberInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        roleInput.getSelectionModel().clearSelection();
    }
}
