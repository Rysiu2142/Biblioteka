package main.Controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import main.model.Dao.ServiceDAO;
import main.model.entity.BooksEntity;
import main.model.entity.SugestionsEntity;
import main.utils.Dialogs;
import javafx.scene.layout.HBox;
import main.utils.ISBN;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler zakladki sugestii
 */
public class OrdersTab extends ServiceDAO implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<SugestionsEntity, String> titleColumn;
    @FXML
    private TableColumn<SugestionsEntity, String> authorColumn;
    @FXML
    private TableColumn<SugestionsEntity, String> amountColumn;
    @FXML
    private TableColumn<SugestionsEntity, String> statusColumn;

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        tableView.setItems(sugestionsDAO.getSugestions());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initTableView();

        tableView.setItems(sugestionsDAO.getSugestions());
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
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<SugestionsEntity, String>("amount"));
        this.statusColumn = (TableColumn) tableView.getColumns().get(3);
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<SugestionsEntity, String>("status"));

        TableColumn buyColumn = new TableColumn<>("Zamów/Odrzuć");
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
                        return new ButtonCell();
                    }

                });

    }

    /**
     * Przcisk w kolumnie tabeli
     */
    private class ButtonCell extends TableCell<Object, Boolean> {
        final Button buyButton = new Button("Zamów");
        final Button rejectButton = new Button("Odrzuć");
        final HBox pane = new HBox(buyButton, rejectButton);
        ButtonCell(){

            buyButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    SugestionsEntity currentSugestion = (SugestionsEntity) OrdersTab.ButtonCell.this.getTableView().getItems().get(OrdersTab.ButtonCell.this.getIndex());

                    if(currentSugestion.getStatus().equalsIgnoreCase("OCZEKIWANIE")){
                      try{
                          currentSugestion.setStatus("zamówiona");
                          sugestionsDAO.update(currentSugestion);


                          Dialogs.successAlert("Ksiązka zamówiona!");
                          for (int i=0;i<currentSugestion.getAmount();i++){
                              BooksEntity newBook = new BooksEntity(ISBN.generate(),"do wylozenia",currentSugestion.getBookstitleByTitleId());
                              booksDAO.save(newBook);
                          }
                          tableView.refresh();
                          System.out.println(sugestionsDAO.getSugestions());
                      }catch(Exception e){
                          Dialogs.errorAlert(e.getMessage());
                      }
                    }


                }
            });
            rejectButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    SugestionsEntity currentSugestion = (SugestionsEntity) OrdersTab.ButtonCell.this.getTableView().getItems().get(OrdersTab.ButtonCell.this.getIndex());

                    if(currentSugestion.getStatus().equalsIgnoreCase("OCZEKIWANIE")){
                        currentSugestion.setStatus("odrzucona");
                        sugestionsDAO.update(currentSugestion);
                        Dialogs.successAlert("Sugestia odrzucona!");
                        tableView.refresh();
                    }


                }
            });
        }
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);

            if(!empty ){
                SugestionsEntity currentSugestion =(SugestionsEntity) getTableView().getItems().get(getIndex());
                if(currentSugestion.getStatus().equalsIgnoreCase("OCZEKIWANIE")){
                    setGraphic(pane);
                }else{
                    setGraphic(null);

                }

            }
            else{
                setGraphic(null);
            }
        }
    }
}
