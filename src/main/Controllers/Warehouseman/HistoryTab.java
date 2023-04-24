package main.Controllers.Warehouseman;
import com.biblioteka.PDFgenerator.Generate;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import main.model.Dao.ServiceDAO;
import main.model.entity.*;
import main.utils.*;

import java.util.ArrayList;
import java.util.Date;
import main.model.entity.OrdersEntity;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler zakladki histora magazniera
 */
public class HistoryTab extends ServiceDAO implements Initializable {

    @FXML
    private TextField search;

    @FXML
    private AnchorPane booksPanel;


    @FXML
    private TableView tableView;


    @FXML
    private TableColumn<OrdersEntity, String> ISBNColumn;

    @FXML
    private TableColumn<OrdersEntity, String> titleColumn;

    @FXML
    private TableColumn<OrdersEntity, String> authorColumn;

    @FXML
    private TableColumn<OrdersEntity, String> readerColumn;
    @FXML
    private TableColumn<OrdersEntity, String> dateFrom;
    @FXML
    private TableColumn<OrdersEntity, String> dateTo;
    @FXML
    private  DatePicker dateFromPicker;
    @FXML
    private  DatePicker dateToPicker;
    @FXML
    private ComboBox titlesBox;
    @FXML
    private TextField amount;


    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        initTableSortedData(ordersDAO.getOrders());
//        updateComboBox();
    }
    @FXML
    public void handleSearch(){
        System.out.println(search.getText());
    }




    /**
     * Initializacja tabeli
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        updateComboBox();
        initTableView();
        initTableSortedData(ordersDAO.findAll());

    }
    public void initTableView(){
        this.ISBNColumn = (TableColumn) tableView.getColumns().get(0);
        this.ISBNColumn.setCellValueFactory(data->
                new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getIsbn()));

        this.titleColumn = (TableColumn) tableView.getColumns().get(1);
        this.titleColumn.setCellValueFactory(data->
                new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getTitle()));

        this.authorColumn = (TableColumn) tableView.getColumns().get(2);
        this.authorColumn.setCellValueFactory(
                data-> new ReadOnlyStringWrapper(
                        data.getValue().getBooksByBookId().getBookstitleByTitleId().getAuthors().toString()));

        this.readerColumn = (TableColumn) tableView.getColumns().get(3);
        this.readerColumn.setCellValueFactory(data->{
            String fullName =data.getValue().getUsersByOrderId().getFirstName() + " " + data.getValue().getUsersByOrderId().getLastName();
            return new ReadOnlyStringWrapper(fullName);
        }) ;

        this.dateFrom = (TableColumn) tableView.getColumns().get(4);
        this.dateFrom.setCellValueFactory( data-> new ReadOnlyStringWrapper(
                data.getValue().getDataFrom().toString()));

        this.dateTo = (TableColumn) tableView.getColumns().get(5);
        this.dateTo.setCellValueFactory( data-> new ReadOnlyStringWrapper(
                data.getValue().getDataTo().toString()));



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
     * Filtrowanie danych w tabeli do generatora
     * @param filteredData dane do filtrowania
     */
    private void initTableSearch(FilteredList<OrdersEntity> filteredData) {
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    if(dateFromPicker.getValue() == null  && dateToPicker.getValue() != null){
                        if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateFromPicker.getValue() != null  && dateToPicker.getValue() == null){
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateFromPicker.getValue() != null  && dateToPicker.getValue() != null){
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue())) && book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                       return true;
                    }

                }
                String lowerCaseFilter = newValue.toLowerCase();
                if(dateFromPicker.getValue() == null  && dateToPicker.getValue() != null){
                    if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }else{
                        return false;
                    }

                }else if(dateFromPicker.getValue() != null  && dateToPicker.getValue() == null){
                    if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue()))){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }else{
                        return false;
                    }
                }else if(dateFromPicker.getValue() != null  && dateToPicker.getValue() != null){
                    if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue())) && book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }else{
                        return false;
                    }
                }else{
                    if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter) ) {
                        return true;
                    } else if (book.getBooksByBookId().getIsbn().contains(lowerCaseFilter)) {
                        return true;
                    }
                }

                return false;
            });
        });
        dateFromPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null) {
                    if(dateToPicker.getValue() == null && search.getText().isEmpty()){
                        return true;
                    }else if(dateToPicker.getValue() != null && search.getText().isEmpty()){
                        if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateToPicker.getValue() == null && !search.getText().isEmpty()){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                                return true;
                            } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                                return true;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
                if (book.getDataFrom().after(java.sql.Date.valueOf(newValue))) {
                    if(dateToPicker.getValue() == null && search.getText().isEmpty()){
                        return true;
                    }else if(dateToPicker.getValue() != null && search.getText().isEmpty()){
                        if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateToPicker.getValue() == null && !search.getText().isEmpty()){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        if(book.getDataTo().before(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                                return true;
                            } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                                return true;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
                return false;
            });
        });
        dateToPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null) {
                    if(dateFromPicker.getValue() == null && search.getText().isEmpty()){
                        return true;
                    }else if(dateFromPicker.getValue() != null && search.getText().isEmpty()){
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateToPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateFromPicker.getValue() == null && !search.getText().isEmpty()){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue()))){
                            if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                                return true;
                            } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                                return true;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
                if (book.getDataTo().before(java.sql.Date.valueOf(newValue))) {
                    if(dateFromPicker.getValue() == null && search.getText().isEmpty()){
                        return true;
                    }else if(dateFromPicker.getValue() != null && search.getText().isEmpty()){
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue()))){
                            return true;
                        }else{
                            return false;
                        }
                    }else if(dateFromPicker.getValue() == null && !search.getText().isEmpty()){
                        if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                            return true;
                        } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        if(book.getDataFrom().after(java.sql.Date.valueOf(dateFromPicker.getValue()))){
                            if (book.getBooksByBookId().getBookstitleByTitleId().getTitle().toLowerCase().contains(search.getText().toLowerCase()) ) {
                                return true;
                            } else if (book.getBooksByBookId().getIsbn().contains(search.getText().toLowerCase())) {
                                return true;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
                return false;
            });
        });
    }
//    @FXML
//    void handleSend(){
//        if(!titlesBox.getSelectionModel().isEmpty()){
//            if(Validation.validNumber(amount.getText())){
//                try{
//                    BookstitleEntity selected =(BookstitleEntity) titlesBox.getSelectionModel().getSelectedItem();
//
//                    SugestionsEntity newSugestion = new SugestionsEntity(Integer.parseInt(amount.getText()),"oczekiwanie",selected);
//                    sugestionsDAO.save(newSugestion);
//
//                    Dialogs.successAlert("Wysłano prośbę o zamówienie ksiązki!");
//                    titlesBox.getSelectionModel().clearSelection();
//                    amount.setText("");
//                }catch(Exception e){
//                    Dialogs.errorAlert(e.getMessage());
//                }
//            }else{
//                Dialogs.errorAlert("Podaj poprawną ilość");
//            }
//        }else{
//            Dialogs.errorAlert("Wybierz tytuł!");
//        }
//
//    }

    /**
     * generowanie PDF na podstawie filtrowanych danych
     */
    @FXML
    void generateHistoryPDF(){
        System.out.println(dateFromPicker.getValue());
        if(dateFromPicker.getValue() != null && dateToPicker.getValue() != null){
            if(dateFromPicker.getValue().isAfter(dateToPicker.getValue())){
                Dialogs.errorAlert("Data poczatku nie może być wieksza niż data końca okresu!");
            }else{
                java.util.List<Object[]> arr = new ArrayList<>();
                tableView.getItems().forEach((order)->{
                    OrdersEntity tmpOrder = (OrdersEntity) order;
                    String authorsName=tmpOrder.getBooksByBookId().getBookstitleByTitleId().getAuthors().toString();
                    String userFullName=tmpOrder.getUsersByOrderId().getFirstName() +" "+tmpOrder.getUsersByOrderId().getLastName();
                    OrderUtilsClass converted = new OrderUtilsClass(tmpOrder.getBooksByBookId().getIsbn(),tmpOrder.getDataFrom(),tmpOrder.getDataTo(),tmpOrder.getBooksByBookId().getBookstitleByTitleId().getTitle(),authorsName,userFullName);

                    Object[] obj={converted.getIsbn(),converted.getDateFrom(),converted.getDateTo(),converted.getBookTitle(),converted.getBookAuthors(),converted.getFullName()};
                    arr.add( obj);
                });
                Generate.generateHistoryPDF(arr,dateFromPicker.getValue().toString(),dateToPicker.getValue().toString());
                Dialogs.successAlert("Wygenerowano PDF!");
            }

        }else{
            Dialogs.errorAlert("Wybierz okres w jakim chcesz wygenerować pdf!");
        }

    }





//    private void updateComboBox(){
//        titlesBox.setItems(bookTitleDAO.findAll());
//
//    }
}
