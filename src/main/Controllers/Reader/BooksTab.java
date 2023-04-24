package main.Controllers.Reader;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import main.model.Dao.ServiceDAO;
import main.model.entity.BooksEntity;
import main.model.entity.OrdersEntity;
import main.model.entity.SugestionsEntity;
import main.model.entity.UsersEntity;
import main.utils.DateUtils;
import main.utils.Dialogs;
import javafx.event.EventHandler;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kontroler zakladki Ksiazki dla bibliotekarza
 */
public class BooksTab extends ServiceDAO implements Initializable {
    @FXML
    private TextField search;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<BooksEntity, String> titleColumn;
    @FXML
    private TableColumn<BooksEntity, String> authorColumn;
    @FXML
    private TableColumn<BooksEntity, String> statusColumn;

    @FXML
    public void handleSearch(){
        System.out.println(search.getText());
    }
    public void refreshTable(){
        initTableSortedData(booksDAO.getAllAvailableBooks());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("Books: ");

        initTableView();
        initTableSortedData(booksDAO.getAllAvailableBooks());
    }

    /**
     * Initializacja tabeli
     */
    public void initTableView(){


        this.titleColumn = (TableColumn) tableView.getColumns().get(0);
        this.titleColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBookstitleByTitleId().getTitle()));

        this.authorColumn = (TableColumn) tableView.getColumns().get(1);
        this.authorColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBookstitleByTitleId().getAuthors().toString()));

        this.statusColumn = (TableColumn) tableView.getColumns().get(2);
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<BooksEntity, String>("status"));

        TableColumn actionColumn = new TableColumn<>("Wypożycz");
        tableView.getColumns().add(actionColumn);

        actionColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Object, Boolean>,
                                        ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Object, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });
        actionColumn.setCellFactory(
                new Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>() {
                    @Override
                    public TableCell<Object, Boolean> call(TableColumn<Object, Boolean> p) {
                        return new ButtonCell();
                    }

                });


    }

    /**
     * Przcisk w kolumnie tabeli
     */
    private class ButtonCell extends TableCell<Object, Boolean> {
        final Button cellButton = new Button("Zarezerwuj");

        ButtonCell(){
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    BooksEntity currentBook = (BooksEntity) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    UsersEntity logged = usersDAO.findById(Integer.parseInt(System.getProperty("userId")));
                    OrdersEntity newOrder;
                    try {
                        if(currentBook.getStatus().equalsIgnoreCase("W MAGAZYNIE")){
                            java.sql.Date start = DateUtils.addDaysToDate(new Date(), 0);
                            java.sql.Date end = DateUtils.addDaysToDate(new Date(), 3);
                            newOrder = new OrdersEntity("zarezerwowana", start, end, currentBook, usersDAO.findById(Integer.parseInt(System.getProperty("userId"))));
                            System.out.println(newOrder);

                            tableView.refresh();
                            currentBook.setStatus("zarezerwowana");
                            booksDAO.update(currentBook);
                            ordersDAO.save(newOrder);
                            ordersDAO.findAllUserOrders(Integer.parseInt(logged.getId().toString()));
                            initTableSortedData(booksDAO.findAllAvailable());

                            Dialogs.successAlert("Zarezerwowałeś ksiązke. Masz 3 dni na odbiór!");
                        }
                    } catch (Exception e) {
                        Dialogs.errorAlert(e.getMessage());
                    }


                }
            });
        }
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);

            if(!empty){
                BooksEntity currentBook =(BooksEntity) getTableView().getItems().get(getIndex());
                if(currentBook.getStatus().equalsIgnoreCase("w magazynie")){
                    setGraphic(cellButton);
                }else{
                    setGraphic(null);
                }

            }
            else{
                setGraphic(null);
            }
        }
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
     * Sprawdza czy ksiazka jest wypożyczona lub zarezerwowana
     * @param bookId wybrana ksiazka w zamowieniach
     * @return boolean czy jest wypożyczona lub zarezerwowana
     */
    private boolean checkIfUserAlreadyRentTheBook(Long bookId){
        AtomicBoolean isExist = new AtomicBoolean(false);
        ordersDAO.getUserOrders().forEach(ordersEntity -> {
            if(ordersEntity.getId() ==bookId && (ordersEntity.getStatus().equalsIgnoreCase("WYPOZYCZONA") ||ordersEntity.getStatus().equalsIgnoreCase("ZAREZERWOWANA")) ){
                isExist.set(true);
            }
        });
        return isExist.get();
    }

}
