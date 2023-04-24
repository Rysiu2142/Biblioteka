package main.Controllers.Librarian;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import main.model.Dao.OrdersDAO;
import main.model.Dao.ServiceDAO;
import main.model.entity.BooksEntity;
import main.model.entity.OrdersEntity;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import main.utils.DateUtils;
import main.utils.Dialogs;

/**
 * Kontroler panelu wypozyczen bibliotekarza
 */
public class LoanReturnsTab extends ServiceDAO implements Initializable {
    private static final String FXML_MAIN_PANEL = "../FXML/Main.fxml";
    private static final String TEMPORARY_LOGIN = "user";
    private static final String TEMPORARY_PASSWORD = "1234";



    @FXML
    private TextField search;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<OrdersEntity, String> titleCol;
    @FXML
    private TableColumn<OrdersEntity, String> emailCol;
    @FXML
    private TableColumn<OrdersEntity, String> datawypCol;
    @FXML
    private TableColumn<OrdersEntity, String> dataodbCol;
    @FXML
    private TableColumn<OrdersEntity, String> statusCol;
    @FXML
    private Button wydaj;
    @FXML
    private Button odbierz;
    @FXML
    public void handleSearch(ActionEvent actionEvent) {
        System.out.println(search.getText());
    }

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        initTableSortedData(ordersDAO.findAllLibrarianTodayOrders());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        initTableSortedData(ordersDAO.getLibrarianTodayOrders());
    }

    /**
     * Initializacja tabeli
     */
    private void initTable() {
        this.titleCol = (TableColumn) tableView.getColumns().get(0);
        this.titleCol.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getTitle()));

        this.emailCol = (TableColumn) tableView.getColumns().get(1);
        this.emailCol.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getUsersByOrderId().getAuthId().getEmail()));

        this.datawypCol = (TableColumn) tableView.getColumns().get(2);
        this.datawypCol.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getDataFrom().toString()));

        this.dataodbCol = (TableColumn) tableView.getColumns().get(3);
        this.dataodbCol.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getDataTo().toString()));

        this.statusCol = (TableColumn) tableView.getColumns().get(4);
        this.statusCol.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getStatus()));

    }

    /**
     * Initializacja sortowanych danych w tabeli
     * @param data dane wprowadzane do tabeli
     */
    private void initTableSortedData(ObservableList<OrdersEntity> data){

        FilteredList<OrdersEntity> filteredData = new FilteredList<>(data, p -> true);
        initTableSearch(filteredData);


        SortedList<OrdersEntity> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    /**
     * Filtrowanie danych w  tabeli
     * @param filteredData dane do filtrowania
     */
    private void initTableSearch(FilteredList<OrdersEntity> filteredData){
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(LoanRet -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (LoanRet.getUsersByOrderId().getAuthId().getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (LoanRet.getUsersByOrderId().getAuthId().getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });

        });
    }

    /**
     * Metoda zminiajaca status ksiazki "wypozyczona" na status "zwrocona"
     * @param event nie użyty
     */
    @FXML
    void odbierz(ActionEvent event) {

        if( tableView.getSelectionModel().getSelectedItem()!=null){
            OrdersEntity current = (OrdersEntity) tableView.getSelectionModel().getSelectedItem();
            if(current.getStatus().equalsIgnoreCase("wypozyczona"))
            {
             try{
                 current.setStatus("zwrocona");
                 current.setDataTo(DateUtils.addDaysToDate(new Date(), 0));
                 ordersDAO.update(current);
                 initTableSortedData(ordersDAO.findAllLibrarianTodayOrders());
                 Dialogs.successAlert("Ksiazka odebrana!");
             }catch(Exception e){
                 Dialogs.errorAlert(e.getMessage());
             }
                tableView.refresh();
            }

        }else{
            Dialogs.errorAlert("Wybierz ksiązke!");
        }

    }

    /**
     * Metoda zmienająca status ksiazki "do odebrania" na status "wypozyczona"
     * @param event nie użyty
     */
    @FXML
    void wydaj(ActionEvent event) {
        if( tableView.getSelectionModel().getSelectedItem()!=null) {
            OrdersEntity current = (OrdersEntity) tableView.getSelectionModel().getSelectedItem();
            if (current.getStatus().equalsIgnoreCase("do odebrania")) {
               try{
                   current.setStatus("wypozyczona");
                   current.setDataTo( DateUtils.addDaysToDate(current.getDataFrom(), 31));
                   ordersDAO.update(current);
                   current.getBooksByBookId().setStatus("wypozyczona");
                   booksDAO.update(current.getBooksByBookId());
                   initTableSortedData(ordersDAO.findAllLibrarianTodayOrders());
                   booksDAO.findAllAvailable();
                   Dialogs.successAlert("Ksiazka wydana!");
               }catch(Exception e){
                   Dialogs.errorAlert(e.getMessage());
               }
            }
            tableView.refresh();
        }else{
            Dialogs.errorAlert("Wybierz ksiązke!");
        }
    }


}
