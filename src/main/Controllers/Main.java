package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import main.Controllers.Warehouseman.Warehouseman;
import main.Controllers.Reader.Reader;
import main.Controllers.Librarian.Librarian;
import main.Controllers.Admin.Admin;
import main.model.Dao.ServiceDAO;
import main.utils.SceneManagment;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import main.model.entity.BooksEntity;
import main.model.entity.OrdersEntity;
import javafx.collections.ObservableList;
public class Main extends ServiceDAO implements Initializable {
    private static final String FXML_LOGIN_PANEL = "../FXML/Login.fxml";

    @FXML
    private Warehouseman warehousemanPanelController;
    @FXML
    private Reader readerContentController;
    @FXML
    private Librarian librarianPanelController;
    @FXML
    private Admin adminPanelController;
    @FXML
    private TabPane appTabPane;
    @FXML
    private Tab adminTab;
    @FXML
    private Tab librarianTab;
    @FXML
    private Tab warehousemanTab;
    @FXML
    private Tab readerTab;


    public Main() {
        updateNotGettingBooks();
        if(System.getProperty("role").equalsIgnoreCase("admin")){
            getAdminData();
        }else if(System.getProperty("role").equalsIgnoreCase("magazynier")){
            getWarehousemanData();
        }else if(System.getProperty("role").equalsIgnoreCase("bibliotekarz")){
            getLibrarianData();
        }else{
            getReaderData();
        }

    }

    /**
     * odswierzanie tablic z uwzglednieniem roli
     */
    @FXML
    public void handleRefreshData() {
        if(System.getProperty("role").equalsIgnoreCase("admin")){
            getAdminData();
            adminPanelController.refreshAdminTables();
        }
        else if(System.getProperty("role").equalsIgnoreCase("magazynier")){
            getWarehousemanData();
            warehousemanPanelController.refreshWarehousemanTables();
        }else if(System.getProperty("role").equalsIgnoreCase("bibliotekarz")){
            getLibrarianData();
            librarianPanelController.refreshLibrarianTables();
        }else{
            getReaderData();
            readerContentController.refreshReaderTables();
        }
    }

    /**
     * Metoda do wylogowania
     * @param event MouseEvent
     * @throws IOException bład SceneMagment
     */
    @FXML
    public void handleLogout(MouseEvent event) throws IOException {
        System.out.println("LOGOUT");
        System.out.println(System.getProperty("userId"));
        System.out.println(System.getProperty("role"));

        SceneManagment.changeStage(FXML_LOGIN_PANEL,event,this);
        System.clearProperty("userId");
        System.clearProperty("role");
        System.clearProperty("name");

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayAppropriateView();

//        appTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
//            if(newTab.getId().equalsIgnoreCase("WAREHOUSEMANTAB")){
//                System.out.println(newTab.getId());
//                warehousemanPanelController.refreshWarehousemanTables();
//            }
//
//
//        });

    }

    /**
     * Metoda zmieniajaca status nie odebranych ksiażek na zwrocona
     */
    public void updateNotGettingBooks(){
        ObservableList<OrdersEntity> orders = ordersDAO.updateAllNotGettingOrders();
        try{
            orders.forEach(order->{
                order.setStatus("zwrocona");
                ordersDAO.update(order);
            });
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Metoda wchodzaca do zakladki pasujacej do roli.
     * Metoda wylacza pozostale
     */
    public void displayAppropriateView(){
        if(System.getProperty("role").equalsIgnoreCase("admin")){
            readerTab.setDisable(true);
            librarianTab.setDisable(true);
            warehousemanTab.setDisable(true);
            appTabPane.getSelectionModel().select(adminTab);
        }else if(System.getProperty("role").equalsIgnoreCase("magazynier")){
            adminTab.setDisable(true);
            librarianTab.setDisable(true);
            readerTab.setDisable(true);
            appTabPane.getSelectionModel().select(warehousemanTab);
        }
        else if(System.getProperty("role").equalsIgnoreCase("bibliotekarz")){
            adminTab.setDisable(true);
            warehousemanTab.setDisable(true);
            readerTab.setDisable(true);
            appTabPane.getSelectionModel().select(librarianTab);
        }else{
            adminTab.setDisable(true);
            warehousemanTab.setDisable(true);
            librarianTab.setDisable(true);
            appTabPane.getSelectionModel().select(readerTab);
        }
    }

    /**
     * Metoda pobierajace dane dla magazaniera
     */
    private void getWarehousemanData(){
        booksDAO.findAll();
        sugestionsDAO.findAll();
        ordersDAO.findAllWarehousemanTodayOrders();
        bookTitleDAO.getSugestionsAboutBooks();
    }

    /**
     * Metoda pobierajace dane dla czytelnika
     */
    private void getReaderData(){
        booksDAO.findAllAvailable();
        ordersDAO.findAllUserOrders(Integer.parseInt(System.getProperty("userId")));
    }

    /**
     * Metoda pobierajace dane dla admina
     */
    private void getAdminData(){
        usersDAO.findAllEmployees();
        sugestionsDAO.findAll();
    }

    /**
     * Metoda pobierajace dane dla bibliotekarza
     */
    private void getLibrarianData(){
        booksDAO.findAllAvailable();
        usersDAO.findAll();
        ordersDAO.findAllLibrarianTodayOrders();
    }
}
