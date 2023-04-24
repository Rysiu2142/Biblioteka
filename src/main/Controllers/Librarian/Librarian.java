package main.Controllers.Librarian;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import main.Controllers.Librarian.BooksTab;
import main.Controllers.Librarian.ReaderTab;
import main.Controllers.Librarian.LoanReturnsTab;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Kontroler glowny panelu bibliotekarza
 */
public class Librarian implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private ReaderTab readerPanelController;
    @FXML
    private BooksTab booksPanelController;
    @FXML
    private LoanReturnsTab loanReturnPanelController;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            refreshLibrarianTables();
        });
    }
    /**
     * Metoda odswiezajaca dane w tabelach
     */
    public void refreshLibrarianTables(){

        readerPanelController.refreshTable();
        booksPanelController.refreshTable();
        loanReturnPanelController.refreshTable();
    }

}
