package main.Controllers.Librarian;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.Dao.BooksDAO;
import main.model.Dao.ServiceDAO;
import main.model.entity.AuthEntity;
import main.model.entity.OrdersEntity;
import main.model.entity.BooksEntity;
import java.util.Date;
import java.util.List;
import main.utils.AutoCompleteComboBoxListener;
import main.utils.DateUtils;
import main.utils.Dialogs;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler penelu ksiazki bibliotekarza
 */
public class BooksTab extends ServiceDAO implements Initializable {


    @FXML
    private TextField search;
    @FXML
    private TableView tableView;
    @FXML
    private ComboBox emailBox;
    @FXML
    private TableColumn<BooksEntity, String> isbnColumn;
    @FXML
    private TableColumn<BooksEntity, String> titleColumn;
    @FXML
    private TableColumn<BooksEntity, String> authorColumn;
    @FXML
    private TableColumn<BooksEntity, String> statusColumn;

    @FXML
    public void handleSearch(){

    }

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        initTableSortedData(booksDAO.getAllAvailableBooks());
        updateComboBox();
    }

    /**
     * Wypozyczanie ksiazki
     */
    @FXML
    public void handleRentBook(){

        if(tableView.getSelectionModel().getSelectedItem()==null)  Dialogs.errorAlert("Wybierz książkę!");
        if(emailBox.getSelectionModel().isEmpty()) Dialogs.errorAlert("Podaj email użytkownika!");
        else{
            List<AuthEntity> selectedUser = authDAO.findByEmail(emailBox.getSelectionModel().getSelectedItem().toString());
            if(!selectedUser.isEmpty()){
                BooksEntity selectedBook = (BooksEntity) tableView.getSelectionModel().getSelectedItem();
                selectedBook.setStatus("wypozyczona");
                OrdersEntity newOrder = new OrdersEntity("wypozyczona",  DateUtils.addDaysToDate(new Date(), 0), DateUtils.addDaysToDate(new Date(), 31),selectedBook,selectedUser.get(0).getUserId());
                try{
                    ordersDAO.save(newOrder);
                    booksDAO.update(selectedBook);
                    initTableSortedData(booksDAO.findAllAvailable());
                    ordersDAO.findAllLibrarianTodayOrders();
                    emailBox.getSelectionModel().clearSelection();
                    Dialogs.successAlert("Ksiazka wypozyczona!");
                }catch(Exception e){
                    Dialogs.errorAlert(e.getMessage());
                }
                tableView.refresh();
            }else{
                Dialogs.errorAlert("Nie ma takiego użytkownika!");
            }

        }


    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateComboBox();
        initTableView();
        initTableSortedData(booksDAO.getAllAvailableBooks());
        new AutoCompleteComboBoxListener<>(emailBox);
    }

    /**
     * Initializacja tabeli
     */
    public void initTableView(){
        this.isbnColumn = (TableColumn) tableView.getColumns().get(0);
        this.isbnColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getIsbn()));

        this.titleColumn = (TableColumn) tableView.getColumns().get(1);
        this.titleColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBookstitleByTitleId().getTitle()));

        this.authorColumn = (TableColumn) tableView.getColumns().get(2);
        this.authorColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBookstitleByTitleId().getAuthors().toString()));

        this.statusColumn = (TableColumn) tableView.getColumns().get(3);
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<BooksEntity, String>("status"));


    }

    /**
     * Initializacja sortowanych danych w tabeli
     * @param data dane wprowadzane do tabeli
     */
    private void initTableSortedData(ObservableList<BooksEntity> data){

        FilteredList<BooksEntity> filteredData = new FilteredList<>(data, p -> true);
        initTableSearch(filteredData);


        SortedList<BooksEntity> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Filtrowanie danych w  tabeli
     * @param filteredData dane do filtrowania
     */
    private void initTableSearch(FilteredList<BooksEntity> filteredData){
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (book.getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });

        });
    }

    /**
     * Wprowadzanie danych do combo boxa
     */
    private void updateComboBox(){

        ObservableList<String> emails = FXCollections.observableArrayList();
        usersDAO.getUsers().forEach(user->{
            if(user.getAuthId().getActive()==1){
                emails.add(user.getAuthId().getEmail());
            }
        });
        emailBox.setItems(emails);
    }

}





