package main.Controllers.Warehouseman;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import main.model.Dao.OrdersDAO;
import main.model.Dao.ServiceDAO;
import main.model.entity.BooksEntity;
import main.model.entity.OrdersEntity;
import main.model.entity.SugestionsEntity;
import main.utils.DateUtils;
import main.utils.Dialogs;
import main.utils.ISBN;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Kontroler panelu zamowienia magazaniera
 */
public class OrdersTab extends ServiceDAO implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<OrdersEntity, String> idColumn;
    @FXML
    private TableColumn<OrdersEntity, String> titleColumn;
    @FXML
    private TableColumn<OrdersEntity, String> authorColumn;
    @FXML
    private TableColumn<OrdersEntity, String> statusColumn;



    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        tableView.setItems(ordersDAO.getWarehousemanTodayOrders());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initTableView();
        tableView.setItems(ordersDAO.getWarehousemanTodayOrders());

    }

    /**
     * Initializacja tabeli
     */
    private void initTableView() {
        this.idColumn = (TableColumn) tableView.getColumns().get(0);
        this.idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getIsbn().toString()));
        this.titleColumn = (TableColumn) tableView.getColumns().get(1);
        this.titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getTitle().toString()));
        this.authorColumn = (TableColumn) tableView.getColumns().get(2);
        this.authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getAuthors().toString()));
        this.statusColumn = (TableColumn) tableView.getColumns().get(3);
        this.statusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus().toString()));
        TableColumn buyColumn = new TableColumn<>("Dostarcz/Odbierz");
        tableView.getColumns().add(buyColumn);

        buyColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Object, Boolean>,
                        ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Object, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });
        buyColumn.setCellFactory(
                new Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>() {
                    @Override
                    public TableCell<Object, Boolean> call(TableColumn<Object, Boolean> p) {
                        return new main.Controllers.Warehouseman.OrdersTab.ButtonCell();
                    }});

    }

    /**
     * Przcisk w kolumnie tabeli
     */
    private class ButtonCell extends TableCell<Object, Boolean> {

        final Button buyButton = new Button();

        public void changeTitle(String title){
            buyButton.setText(title);
        }

        ButtonCell(){


            buyButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {

                    OrdersEntity currentOrder = (OrdersEntity) main.Controllers.Warehouseman.OrdersTab.ButtonCell.this.getTableView().getItems().get(main.Controllers.Warehouseman.OrdersTab.ButtonCell.this.getIndex());

                    if(currentOrder.getStatus().equalsIgnoreCase("zarezerwowana")){
                        try{
                            currentOrder.setStatus("do odebrania");
                            ordersDAO.update(currentOrder);
                            Dialogs.successAlert("Ksiązka dostarczona!");
                            ordersDAO.findAllWarehousemanTodayOrders();
                            refreshTable();
                        }catch(Exception e){
                            Dialogs.errorAlert(e.getMessage());
                        }
                    }else{
                        try{
                            currentOrder.setStatus("zakonczone");
                            currentOrder.setDataTo( DateUtils.addDaysToDate(new Date(), 0));
                            ordersDAO.update(currentOrder);
                            currentOrder.getBooksByBookId().setStatus("w magazynie");
                            booksDAO.update(currentOrder.getBooksByBookId());
                            Dialogs.successAlert("Ksiązka odebrana!");
                            ordersDAO.findAllWarehousemanTodayOrders();
                            booksDAO.findAll();
                            refreshTable();
                        }catch(Exception e){
                            Dialogs.errorAlert(e.getMessage());
                        }
                    }


                }
            });

        }
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);

            if(!empty ){
                OrdersEntity currentOrder =(OrdersEntity) getTableView().getItems().get(getIndex());
                if(currentOrder.getStatus().equalsIgnoreCase("zarezerwowana")){
                    buyButton.setText("Dostarcz");
                    setGraphic(buyButton);
                }else{
                    buyButton.setText("Odbierz");
                    setGraphic(buyButton);
                }

            }
            else{
                setGraphic(null);
            }
        }
    }
}
