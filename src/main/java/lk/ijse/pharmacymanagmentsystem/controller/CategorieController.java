package lk.ijse.pharmacymanagmentsystem.controller;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.pharmacymanagmentsystem.dto.CategorieDTO;
import lk.ijse.pharmacymanagmentsystem.model.CategorieModel;

public class CategorieController implements Initializable {

    @FXML
    private TableColumn cat_Column_ID;

    @FXML
    private TableColumn cat_Column_Name;

    @FXML
    private TableColumn cat_Column_Type;

    @FXML
    private TextField cat_ID;

    @FXML
    private TableColumn<CategorieDTO, Void> ActionCol;

    @FXML
    private TextField categorie_Name;

    @FXML
    private TableView categorie_Table_View;

    @FXML
    private ComboBox<String> com_Select;

    private final String CAT_NAME_REGEX = "^[A-Za-z\\s&]{3,50}$";

    private final CategorieModel categorieModel = new CategorieModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        com_Select.setItems(FXCollections.observableArrayList("Medicine", "Item"));

        cat_Column_ID.setCellValueFactory(new PropertyValueFactory<>("categorie_Id"));
        cat_Column_Name.setCellValueFactory(new PropertyValueFactory<>("categorie_name"));
        cat_Column_Type.setCellValueFactory(new PropertyValueFactory<>("categorie_type"));

        categorie_Table_View.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setDataToFields((CategorieDTO) newValue);
            }
        });

        ActionColumn();
        loadAllCategories();
    }

    private void setDataToFields(CategorieDTO dto) {
        cat_ID.setText(String.valueOf(dto.getCategorie_Id()));
        categorie_Name.setText(dto.getCategorie_name());
        com_Select.setValue(dto.getCategorie_type());
    }

    private void ActionColumn() {
        ActionCol.setCellFactory(column -> {
            return new TableCell<>() {
                private final Button deleteButton = new Button("🗑 Remove");

                {
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                            + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

                    deleteButton.setOnAction(event -> {
                        CategorieDTO selectedCat = getTableView().getItems().get(getIndex());
                        handleActionDelete(selectedCat);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            };
        });
    }

    private void handleActionDelete(CategorieDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Category: " + dto.getCategorie_name());
        confirm.setContentText("Are you sure you want to delete this category?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (categorieModel.isCategoryUsedInProducts(dto.getCategorie_Id())) {
                    new Alert(Alert.AlertType.ERROR, "Cannot delete! This category is used in products.").show();
                    return;
                }

                boolean result = categorieModel.deleteCategorie(dto.getCategorie_Id());
                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Category Deleted!").show();
                    loadAllCategories();
                    clearFields();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSaveCategorie() {

        try {
            String name = categorie_Name.getText().trim();
            String type = com_Select.getValue();

            if (name.isEmpty() || !name.matches(CAT_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Category Name! (Min 3 letters, letters & spaces only)").show();

            } else if (type == null || type.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please select a Type!").show();

            } else {

                CategorieDTO categorieDTO = new CategorieDTO(name, type);

                boolean result = categorieModel.saveCategorie(categorieDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Category Added Successfully!").show();
                    loadAllCategories();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add category!").show();
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            new Alert(Alert.AlertType.ERROR, "Category with this name and type already exists!").show();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchCategorie(KeyEvent event) {
        try {

            if (event.getCode() == KeyCode.ENTER) {
                String id = cat_ID.getText().trim();

                if (id.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Please enter Category ID!").show();

                } else {
                    CategorieDTO categorieDTO = categorieModel.searchCategorie(Long.parseLong(id));

                    if (categorieDTO != null) {

                        cat_ID.setText(String.valueOf(categorieDTO.getCategorie_Id()));
                        categorie_Name.setText(categorieDTO.getCategorie_name());
                        com_Select.setValue(categorieDTO.getCategorie_type());

                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Category Not Found!").show();
                    }
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid ID! Please enter a number.").show();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleUpdateCategorie() {
        try {
            String id = cat_ID.getText().trim();
            String name = categorie_Name.getText().trim();
            String type = com_Select.getValue();

            if (id.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please select or search a category to update!").show();

            } else if (name.isEmpty() || !name.matches(CAT_NAME_REGEX)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Category Name!").show();

            } else if (type == null || type.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please select a Type!").show();

            } else {

                CategorieDTO categorieDTO = new CategorieDTO(Long.parseLong(id), name, type);

                boolean result = categorieModel.updateCategorie(categorieDTO);

                if (result) {
                    new Alert(Alert.AlertType.INFORMATION, "Category Updated Successfully!").show();
                    loadAllCategories();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                }
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

//    @FXML
//    private void handleDeleteCategorie() {
//        try {
//            String idText = cat_ID.getText().trim();
//
//            if (idText.isEmpty()) {
//                new Alert(Alert.AlertType.WARNING, "Input Required" + "Please enter Category ID to delete!").show();
//                return;
//            }
//
//            long catId;
//            try {
//                catId = Long.parseLong(idText);
//
//            } catch (NumberFormatException e) {
//                new Alert(Alert.AlertType.WARNING, "Invalid Input" + "Category ID must be a valid number!").show();
//                return;
//            }
//
//            if (categorieModel.isCategoryUsedInProducts(catId)) {
//                new Alert(Alert.AlertType.ERROR, "Cannot Delete Category"
//                        + "This category cannot be deleted because there are products linked to it.\n\n"
//                        + "Please delete or reassign those products to another category first.").show();
//                return;
//            }
//
//            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
//            confirm.setTitle("Confirm Delete");
//            confirm.setHeaderText("Delete Category ID: " + catId);
//            confirm.setContentText("Are you sure you want to delete this category permanently?\nThis action cannot be undone.");
//
//            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
//                boolean result = categorieModel.deleteCategorie(catId);
//
//                if (result) {
//                    new Alert(Alert.AlertType.INFORMATION, "Success" + "Category deleted successfully!").show();
//                    loadAllCategories();  // Refresh table
//                    clearFields();        // Clear fields
//                } else {
//                    new Alert(Alert.AlertType.ERROR, "Failed" + "Failed to delete category due to an unknown error.").show();
//                }
//            }
//
//        } catch (Exception e) {
//            new Alert(Alert.AlertType.ERROR, "Error" + "Something went wrong: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void loadAllCategories() {

        try {
            List<CategorieDTO> categorieList = categorieModel.getAllCategories();

            ObservableList<CategorieDTO> obList = FXCollections.observableArrayList();

            for (CategorieDTO dto : categorieList) {
                obList.add(dto);
            }
            categorie_Table_View.setItems(obList);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load Categories!").show();
            e.printStackTrace();
        }
    }

    private void clearFields() {
        cat_ID.clear();
        categorie_Name.clear();
        com_Select.setValue(null);
    }

}
