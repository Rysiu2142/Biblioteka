package main.Controllers.Warehouseman;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import main.model.Dao.BooksDAO;
import main.model.Dao.ServiceDAO;
import main.model.entity.BooksEntity;
import main.model.entity.AuthorEntity;
import main.model.entity.BookstitleEntity;
import main.model.entity.BooksauthorEntity;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import main.utils.Dialogs;
import main.utils.ISBN;
import main.utils.Validation;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Kontroler panelu ksiazki bibliotekarza
 */
public class BooksTab extends ServiceDAO implements Initializable {
    private static final String FXML_MAIN_PANEL = "../FXML/Main.fxml";
    private static final String TEMPORARY_LOGIN = "user";
    private static final String TEMPORARY_PASSWORD = "1234";

    @FXML
    private TextField search;

    @FXML
    private AnchorPane booksPanel;
    @FXML
    private ComboBox titleComboBox;

    @FXML
    private TableView tableView;


    @FXML
    private TableColumn<BooksEntity, String> ISBNColumn;

    @FXML
    private TableColumn<BooksEntity, String> titleColumn;

    @FXML
    private TableColumn<BooksEntity, String> authorColumn;

    @FXML
    private TableColumn<BooksEntity, String> statusColumn;

    @FXML
    private TableView authorTableView;
    @FXML
    private TableColumn<AuthorEntity, String> authorFirstName;

    @FXML
    private TableColumn<AuthorEntity, String> authorLastName;
    @FXML
    private TextField authorFirstNameField;
    @FXML
    private TextField authorLastNameField;
    @FXML
    private TextField titleField;

    ObservableList<AuthorEntity> authors = FXCollections.observableArrayList(authorDAO.findAll());

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        initTableSortedData(booksDAO.getBooks());
    }
    @FXML
    public void handleSearch(){
        System.out.println(search.getText());
    }

    /**
     * Dodawanie autora
     */
    @FXML
    public void handleAddAuthor(){
        if(Validation.validText(authorFirstNameField.getText()) && Validation.validText(authorLastNameField.getText())){
            AuthorEntity newAuthor = new AuthorEntity(authorFirstNameField.getText(),authorLastNameField.getText());
         try{
             authorDAO.save(newAuthor);
             authors.add(newAuthor);
             authorTableView.setItems(authors);
             authorFirstNameField.setText("");
             authorLastNameField.setText("");

         }catch(Exception e){
             Dialogs.errorAlert(e.getMessage());
         }

        }else{
            Dialogs.errorAlert("Podaj poprawne dane autora!");
        }

    }

    /**
     * Dodawanie Ksiazki
     */
    @FXML
    public void handleAddNewBook(){
        if(Validation.validTitle(titleField.getText())){
            if(!authorTableView.getSelectionModel().getSelectedItems().isEmpty()){
                try{
                    BookstitleEntity newTitle = new BookstitleEntity(titleField.getText());
                    bookTitleDAO.save(newTitle);
                    authorTableView.getSelectionModel().getSelectedItems().forEach(item->{
                        AuthorEntity selected = (AuthorEntity)item;
                        BooksauthorEntity newBookAuthor = new BooksauthorEntity(selected,newTitle);
                        booksauthorDAO.save(newBookAuthor);
                    });
                    updateComboBox();
                    authorTableView.getSelectionModel().clearSelection();
                    titleField.setText("");
                    Dialogs.successAlert("Dodano nowy tytuł!");
                }catch(Exception e){
                    Dialogs.errorAlert(e.getMessage());
                }

            }else{
                Dialogs.errorAlert("Wybierz minimum jednego autora!");
            }
            System.out.println(authorTableView.getSelectionModel().getSelectedItems());
        }else{
            Dialogs.errorAlert("Podaj poprawny tytuł!");
        }
    }

    /**
     * Usuwanie ksiazki
     */
    @FXML
    public void handleRemoveBook(){
        if(!tableView.getSelectionModel().isEmpty()){
            BooksEntity selected = (BooksEntity) tableView.getSelectionModel().getSelectedItem();
            if(selected.getStatus().equalsIgnoreCase("W MAGAZYNIE")||selected.getStatus().equalsIgnoreCase("DO WYLOZENIA")){
                try{
                    ordersDAO.removeBooksOrders(Integer.parseInt(selected.getId().toString()));
                    booksDAO.delete(selected);
                    Dialogs.successAlert("Usunięto książkę!");
                }catch(Exception e){
                    Dialogs.errorAlert(e.getMessage());
                }finally {
                    initTableSortedData(booksDAO.findAll());
                    ordersDAO.findAll();
                    bookTitleDAO.getSugestionsAboutBooks();
                    tableView.refresh();
                }


            }else{
                Dialogs.errorAlert("Nie można usunąć książki która jest wypożyczona lub zarezerwowana!");
            }
        }else{
            Dialogs.errorAlert("Wybierz książkę!");
        }
    }

    /**
     * Zmiana statusu ksiazki
     */
    @FXML
    public void handleUpdateStatus(){
        if(!tableView.getSelectionModel().isEmpty()) {
            BooksEntity selected = (BooksEntity) tableView.getSelectionModel().getSelectedItem();
            if(selected.getStatus().equalsIgnoreCase("do wylozenia")){
                selected.setStatus("w magazynie");
                booksDAO.update(selected);
                tableView.refresh();
                Dialogs.successAlert("Status zmieniony!");
            }else{
                Dialogs.errorAlert("Wybrana książka jest juz na półce!");
            }
        }else{
            Dialogs.errorAlert("Wybierz książkę!");
        }
    }

    /**
     * Dodanie ksiazki przez tytul
     */
    @FXML
    public void handleAddByTitle(){
        if(!titleComboBox.getSelectionModel().isEmpty()){
            BookstitleEntity selected= (BookstitleEntity) titleComboBox.getSelectionModel().getSelectedItem();
            try{
                BooksEntity newBook = new BooksEntity(ISBN.generate(),"do wylozenia",selected);
                booksDAO.save(newBook);
                Dialogs.successAlert("Dodano egzemplarz!");
                titleComboBox.getSelectionModel().clearSelection();
            }catch(Exception e){
                Dialogs.errorAlert(e.getMessage());
            }
            initTableSortedData(booksDAO.findAll());

            System.out.println(selected);
        }else{
            Dialogs.errorAlert("Wybierz tytuł z listy!");
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateComboBox();
        initTableView();
        initTableSortedData(booksDAO.getBooks());
        authorTableView.setItems(authors);
        authorTableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    /**
     * Initializacja tabeli
     */
    public void initTableView(){
            this.ISBNColumn = (TableColumn) tableView.getColumns().get(0);
            this.ISBNColumn.setCellValueFactory(data->
                    new ReadOnlyStringWrapper(data.getValue().getIsbn()));

            this.titleColumn = (TableColumn) tableView.getColumns().get(1);
            this.titleColumn.setCellValueFactory(data->
                    new ReadOnlyStringWrapper(data.getValue().getBookstitleByTitleId().getTitle()));

            this.authorColumn = (TableColumn) tableView.getColumns().get(2);
            this.authorColumn.setCellValueFactory(
                    data-> new ReadOnlyStringWrapper(
                            data.getValue().getBookstitleByTitleId().getAuthors().toString()));

            this.statusColumn = (TableColumn) tableView.getColumns().get(3);
            this.statusColumn.setCellValueFactory(new PropertyValueFactory<BooksEntity, String>("status"));


            //AUTHOR TABLE
            this.authorFirstName = (TableColumn) authorTableView.getColumns().get(0);
            this.authorFirstName.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("firstName"));

            this.authorLastName = (TableColumn) authorTableView.getColumns().get(1);
            this.authorLastName.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("lastName"));

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
        private void initTableSearch(FilteredList<BooksEntity> filteredData) {
            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(book -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (book.getBookstitleByTitleId().getTitle().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (book.getIsbn().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
        }

        private void updateComboBox(){
            titleComboBox.setItems(bookTitleDAO.findAll());
        }
}
