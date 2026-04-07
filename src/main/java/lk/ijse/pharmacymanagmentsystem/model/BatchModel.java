package lk.ijse.pharmacymanagmentsystem.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lk.ijse.pharmacymanagmentsystem.dto.BatchDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

public class BatchModel {

    public List<String> searchAvailableBatches(String product) throws SQLException {
        String sql = "SELECT b.batch_id, b.batch_number, p.name AS product_name, b.expiry_date, b.qty_remaining, b.selling_price "
                + "FROM Batches b "
                + "JOIN Products p ON b.product_id = p.product_id "
                + "WHERE p.name LIKE ? AND b.qty_remaining > 0 "
                + "ORDER BY b.expiry_date ASC";

        ResultSet rs = CrudUtil.execute(sql, "%" + product + "%");

        List<String> batches = new ArrayList<>();
        while (rs.next()) {
            String display = rs.getString("product_name") + " | Batch: " + rs.getString("batch_number")
                    + " | Expiry: " + rs.getDate("expiry_date")
                    + " | Qty: " + rs.getInt("qty_remaining")
                    + " | Price: Rs. " + String.format("%.2f", rs.getDouble("selling_price"));

            batches.add(display);
        }
        return batches;
    }

    public BatchDTO getBatchByDisplay(String display) throws SQLException {

        String batchNumber = display.split("Batch: ")[1].split(" | ")[0].trim();

        String sql = "SELECT b.*, p.name AS product_name "
                + "FROM Batches b "
                + "JOIN Products p ON b.product_id = p.product_id "
                + "WHERE b.batch_number = ?";

        ResultSet rs = CrudUtil.execute(sql, batchNumber);

        if (rs.next()) {
            BatchDTO batch = new BatchDTO();
            batch.setBatchId(rs.getLong("batch_id"));
            batch.setProductName(rs.getString("product_name"));
            batch.setBatchNumber(rs.getString("batch_number"));
            batch.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
            batch.setQtyRemaining(rs.getInt("qty_remaining"));
            batch.setSellingPrice(rs.getDouble("selling_price"));
            return batch;
        }
        return null;
    }

    public List<String> getProductSuggestionsStartingWith(String letter) throws SQLException {
        String sql = "SELECT DISTINCT p.name "
                + "FROM Products p "
                + "JOIN Batches b ON p.product_id = b.product_id "
                + "WHERE UPPER(p.name) LIKE ? AND b.qty_remaining > 0 "
                + "ORDER BY p.name";

        ResultSet rs = CrudUtil.execute(sql, letter + "%");

        List<String> suggestions = new ArrayList<>();
        while (rs.next()) {
            suggestions.add(rs.getString("name"));
        }
        return suggestions;
    }

    public String generateNextBatchNumber(int rowCount) throws SQLException {
        String sql = "SELECT MAX(batch_id) FROM Batches";
        ResultSet rs = CrudUtil.execute(sql);

        String datePart = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BT-" + datePart + "-";

        long lastDbId = 0;
        if (rs.next()) {
            lastDbId = rs.getLong(1);
        }

        long nextId = lastDbId + rowCount + 1;

        return prefix + String.format("%04d", nextId);
    }
}
