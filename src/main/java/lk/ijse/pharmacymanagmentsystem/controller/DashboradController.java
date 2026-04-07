package lk.ijse.pharmacymanagmentsystem.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pharmacymanagmentsystem.dbc.DBConnection;
import lk.ijse.pharmacymanagmentsystem.model.DashboradModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class DashboradController implements Initializable {

    @FXML
    private Label lblCustomers, lblExpier, lblOrders, lblProfit, lblRevenue, lblStock;
    @FXML
    private ComboBox<String> cmbFilter;
    @FXML
    private AreaChart<String, Number> salesChart;

    private final DashboradModel model = new DashboradModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblStock.setOnMouseClicked(event -> showLowStockPopUp());
        lblStock.setStyle("-fx-cursor: hand;");
        lblExpier.setOnMouseClicked(event -> showExpiredPopUp());
        lblExpier.setStyle("-fx-cursor: hand;");

        cmbFilter.setItems(FXCollections.observableArrayList("Today", "Weekly", "Monthly"));
        cmbFilter.setValue("Today");

        loadDashboardData("Today");

        cmbFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadDashboardData(newValue);
            }
        });

        Platform.runLater(this::checkExpiriesAndAlert);
    }

    private void checkExpiriesAndAlert() {
        try {
            int expiredCount = model.getExpiredCount();
            if (expiredCount > 0) {
                showSideAlert(expiredCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showSideAlert(int count) {
        System.out.print("\007");
        System.out.flush();

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Pharmacy System Notification");
        alert.setHeaderText("Stock Expiry Warning!");
        alert.setContentText("There are " + count + " items expiring soon.\nClick 'View' or this will close in 5s.");

        ButtonType btnView = new ButtonType("View List");
        alert.getButtonTypes().setAll(btnView, ButtonType.CLOSE);

        alert.setOnShowing(event -> {
            Screen screen = Screen.getPrimary();
            double screenWidth = screen.getVisualBounds().getWidth();
            double screenHeight = screen.getVisualBounds().getHeight();
            alert.setX(screenWidth - 460);
            alert.setY(screenHeight - 250);
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (alert.isShowing()) {
                alert.close();
            }
        }));
        timeline.play();

        alert.showAndWait().ifPresent(response -> {
            if (response == btnView) {
                showExpiredPopUp();
            }
        });
    }

    private void loadDashboardData(String filter) {
        try {
            lblRevenue.setText(String.format("Rs. %.2f", model.getTotalRevenue(filter)));
            lblProfit.setText(String.format("Rs. %.2f", model.getTotalProfit(filter)));
            lblOrders.setText(String.valueOf(model.getOrderCount(filter)));
            lblCustomers.setText(String.valueOf(model.getCustomerCount()));

            int expiredCount = model.getExpiredCount();
            int lowStockCount = model.getLowStockCount();

            lblExpier.setText(String.valueOf(expiredCount));
            lblStock.setText(String.valueOf(lowStockCount));

            if (expiredCount > 0) {
                lblExpier.setStyle("-fx-text-fill: #FF3333; -fx-font-weight: bold; -fx-font-size: 18;");
            }
            if (lowStockCount > 0) {
                lblStock.setStyle("-fx-text-fill: #FF8C00; -fx-font-weight: bold; -fx-font-size: 18;");
            }

            updateChart(filter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChart(String filter) throws SQLException {
        salesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue (" + filter + ")");
        Map<String, Double> data = model.getChartData(filter);
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        salesChart.getData().add(series);
    }

    private void showExpiredPopUp() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Expired/Expiring Medicines");

        TableView<Map<String, String>> tableView = new TableView<>();
        setupTableColumns(tableView, true);

        loadTableData(tableView, "expired");

        VBox layout = new VBox(10, tableView);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #E3FDFD;");
        popupStage.setScene(new Scene(layout, 600, 400));
        popupStage.show();
    }

    private void showLowStockPopUp() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Low Stock Warning");

        TableView<Map<String, String>> tableView = new TableView<>();
        setupTableColumns(tableView, false);

        loadTableData(tableView, "lowstock");

        VBox layout = new VBox(10, tableView);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #FFF9E3;");
        popupStage.setScene(new Scene(layout, 500, 400));
        popupStage.show();
    }

    private void setupTableColumns(TableView<Map<String, String>> table, boolean isExpiry) {
        TableColumn<Map<String, String>, String> colName = new TableColumn<>("Medicine");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("name")));

        TableColumn<Map<String, String>, String> colBatch = new TableColumn<>("Batch");
        colBatch.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("batch")));

        table.getColumns().addAll(colName, colBatch);

        if (isExpiry) {
            TableColumn<Map<String, String>, String> colDate = new TableColumn<>("Expiry");
            colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("date")));
            table.getColumns().add(colDate);
        }

        TableColumn<Map<String, String>, String> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("qty")));
        table.getColumns().add(colQty);
    }

    private void loadTableData(TableView<Map<String, String>> table, String type) {
        ObservableList<Map<String, String>> list = FXCollections.observableArrayList();
        try {
            ResultSet rs = type.equals("expired") ? model.getExpiredMedicines() : model.getLowStockMedicines();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();

                if (type.equals("lowstock")) {
                    row.put("name", rs.getString("name"));
                    row.put("batch", "All Batches");
                    row.put("qty", rs.getString("total_qty"));
                } else {
                    row.put("name", rs.getString("name"));
                    row.put("batch", rs.getString("batch_number"));
                    row.put("qty", rs.getString("qty_remaining"));
                    row.put("date", rs.getString("expiry_date"));
                }
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(list);
    }

    public void printLowStockReport() {
        try (InputStream inputStream = getClass().getResourceAsStream("/lk/ijse/pharmacymanagmentsystem/report/Low_Stock.jrxml")) {

            Connection conn = DBConnection.getInstance().getConnection();

            if (inputStream == null) {
                new Alert(Alert.AlertType.ERROR, "Report file එක සොයාගත නොහැක!").show();
                return;
            }

            Map<String, Object> parameters = new HashMap<>();
            JasperReport jr = JasperCompileManager.compileReport(inputStream);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, conn);

            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Pharmacy Management System - Low Stock Summary");
            viewer.setVisible(true);
            viewer.toFront();

        } catch (SQLException | JRException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "දෝෂයකි: " + e.getMessage()).show();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
