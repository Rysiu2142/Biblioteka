package main.Controllers.Admin;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler glowny panelu admina
 */
public class Admin implements Initializable {
    /**
     * Kontroler zakladki sugestii
     */
    @FXML
    private OrdersTab ordersPanelController;
    /**
     * Kontoler zakladki pracownikow
     */
    @FXML
    private EmployeeTab employeePanelController;
    @FXML
    private TabPane tabPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            refreshAdminTables();
        });
    }

    /**
     * Metoda odswiezajaca dane w tabelach
     */
    public void refreshAdminTables(){
        ordersPanelController.refreshTable();
        employeePanelController.refreshTable();
    }

}
