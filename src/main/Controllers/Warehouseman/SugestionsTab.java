package main.Controllers.Warehouseman;

import javafx.beans.property.ReadOnlyStringWrapper;
import com.biblioteka.PDFgenerator.Generate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import main.model.Dao.ServiceDAO;
import main.utils.Dialogs;
import main.utils.ISBN;
import main.utils.Validation;
import main.model.entity.SugestionsEntity;
import main.model.entity.BookstitleEntity;
import java.net.URL;
import java.util.ResourceBundle;




/**
 * Kontroler sugesti magazaniera
 */
public class SugestionsTab extends ServiceDAO implements Initializable {



        @FXML
        private TableView tableView;

        @FXML
        private TableColumn<Object[], String> titleColumn;

        @FXML
        private TableColumn<Object[], String> amountColumn;

        @FXML
        private TextField amountInput;

        /**
         * Metoda skladajaca pośbę o zamowienie ksiazki
         */
        @FXML
        public void handleSendRequest(){
            if(!tableView.getSelectionModel().isEmpty()){
                if(Validation.validNumber(amountInput.getText())){
                   try{
                       Object[] selected =  (Object[])tableView.getSelectionModel().getSelectedItem();
                       BookstitleEntity searchBookTitle = bookTitleDAO.findById(Integer.parseInt(selected[0].toString()));
                       System.out.println(searchBookTitle);
                       SugestionsEntity newSugestion = new SugestionsEntity(Integer.parseInt(amountInput.getText()),"oczekiwanie",searchBookTitle);
                       sugestionsDAO.save(newSugestion);

                       Generate.generateSugestionPDF(selected,System.getProperty("name"), amountInput.getText());

                       Dialogs.successAlert("Wysłano prośbę o zamówienie ksiązki!");
                       tableView.getSelectionModel().clearSelection();
                       amountInput.setText("");
                   }catch(Exception e){
                       Dialogs.errorAlert(e.getMessage());
                   }
                }else {
                    Dialogs.errorAlert("Podaj poprawną ilość!");
                }

            }else {
                Dialogs.errorAlert("Wybierz książkę!");
            }
        }

        /**
         * Odswiezanie tabeli
         */
        public void refreshTable(){
                 tableView.setItems(bookTitleDAO.getSugestionsAboutBooks());
        }
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            initTableView();
            tableView.setItems(bookTitleDAO.getAllSugestionsAboutBooks());

        }

    /**
     * Initializacja tabeli
     */
    private void initTableView() {

        this.titleColumn = (TableColumn) tableView.getColumns().get(0);
        this.titleColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue()[1].toString()));

        this.amountColumn = (TableColumn) tableView.getColumns().get(1);
        this.amountColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(String.valueOf((Float.parseFloat(data.getValue()[2].toString())/(Float.parseFloat(data.getValue()[3].toString()))))));

    }
}
