package lk.ijse.pharmacymanagmentsystem.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

public class DashboradModel {

    private String getDateFilter(String filter) {
        switch (filter) {
            case "Today":
                return "CURDATE()";
            case "Weekly":
                return "DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
            case "Monthly":
                return "DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
            default:
                return "CURDATE()";
        }
    }

    public double getTotalRevenue(String filter) throws SQLException {
        String condition = filter.equals("Today") ? "= CURDATE()" : ">= " + getDateFilter(filter);
        String sql = "SELECT SUM(total_amount) FROM Invoice WHERE invoice_date " + condition;
        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getDouble(1) : 0.0;
    }

    public double getTotalProfit(String filter) throws SQLException {
        String condition = filter.equals("Today") ? "= CURDATE()" : ">= " + getDateFilter(filter);

        String sql = "SELECT SUM((id.selling_price - b.cost_price) * id.quantity) "
                + "FROM Invoice_Details id "
                + "JOIN Batches b ON id.batch_id = b.batch_id "
                + "JOIN Invoice i ON id.invoice_id = i.invoice_id "
                + "WHERE i.invoice_date " + condition;

        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getDouble(1) : 0.0;
    }

    public int getOrderCount(String filter) throws SQLException {
        String condition = filter.equals("Today") ? "= CURDATE()" : ">= " + getDateFilter(filter);
        String sql = "SELECT COUNT(*) FROM Invoice WHERE invoice_date " + condition;
        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int getCustomerCount() throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT COUNT(*) FROM Customers");
        return rs.next() ? rs.getInt(1) : 0;
    }

    public Map<String, Double> getChartData(String filter) throws SQLException {
        String condition = ">= " + getDateFilter(filter);
        String sql = "SELECT invoice_date, SUM(total_amount) FROM Invoice "
                + "WHERE invoice_date " + condition + " GROUP BY invoice_date ORDER BY invoice_date ASC";

        ResultSet rs = CrudUtil.execute(sql);
        Map<String, Double> data = new LinkedHashMap<>();
        while (rs.next()) {
            data.put(rs.getString(1), rs.getDouble(2));
        }
        return data;
    }

    public int getExpiredCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Batches WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND qty_remaining > 0";
        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int getLowStockCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ("
                + "SELECT p.product_id "
                + "FROM Products p "
                + "JOIN Batches b ON p.product_id = b.product_id "
                + "JOIN Suppliers s ON b.supplier_id = s.supplier_id "
                + 
                "GROUP BY p.product_id, s.supplier_name "
                + "HAVING SUM(b.qty_remaining) <= 10"
                + ") AS total_low_stock";

        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public ResultSet getExpiredMedicines() throws SQLException {
        String sql = "SELECT p.name, b.batch_number, b.expiry_date, b.qty_remaining "
                + "FROM Batches b "
                + "JOIN Products p ON b.product_id = p.product_id "
                + "WHERE b.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) "
                + "AND b.qty_remaining > 0 "
                + "ORDER BY b.expiry_date ASC";
        return CrudUtil.execute(sql);
    }

    public ResultSet getLowStockMedicines() throws SQLException {
        String sql = "SELECT p.name, SUM(b.qty_remaining) AS total_qty "
                + "FROM Batches b "
                + "JOIN Products p ON b.product_id = p.product_id "
                + "GROUP BY p.product_id, p.name "
                + "HAVING total_qty <= 10  "
                + "ORDER BY total_qty ASC";

        return CrudUtil.execute(sql);
    }
}
