package lk.ijse.pharmacymanagmentsystem.model;

import lk.ijse.pharmacymanagmentsystem.dto.GRNDTO;
import lk.ijse.pharmacymanagmentsystem.dto.GRNLineDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import lk.ijse.pharmacymanagmentsystem.dbc.DBConnection;

public class GRNModel {

// Main method – transaction GRN header + lines + batches save 
    public boolean saveGRNWithTransaction(GRNDTO grnDTO) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);  // Transaction start

            // GRN header save generated grn_id 
            long grnId = saveGRNHeader(grnDTO);
            if (grnId == -1) {
                conn.rollback();
                return false;
            }

            // line GRN_Details + Batches table insert 
            for (GRNLineDTO line : grnDTO.getLines()) {
                boolean lineSaved = saveGRNLine(grnId, line, grnDTO.getSupplierId());
                if (!lineSaved) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();  // success → permanent save
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private long saveGRNHeader(GRNDTO dto) throws SQLException {
        String sql = "INSERT INTO GRN (grn_number, supplier_id, grn_date, total_amount,notes, received_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        CrudUtil.execute(sql,
                dto.getGrnNumber(),
                dto.getSupplierId(),
                dto.getGrnDate(),
                dto.getTotalAmount(),
                dto.getNotes(),
                dto.getReceivedBy());

        // Generated grn_id 
        String keySql = "SELECT LAST_INSERT_ID()";
        ResultSet rs = CrudUtil.execute(keySql);
        if (rs.next()) {
            return rs.getLong(1);
        }
        return -1;
    } //GRN

    private boolean saveGRNLine(long grnId, GRNLineDTO line, long supplierId) throws SQLException {
        String sql = "INSERT INTO GRN_Details (grn_id, product_id, batch_number, expiry_date, manufacture_date, quantity, cost_price, selling_price) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        boolean grnLineSaved = CrudUtil.execute(sql,
                grnId,
                line.getProductId(),
                line.getBatchNumber(),
                line.getExpiryDate(),
                line.getManufactureDate(),
                line.getQuantity(),
                line.getCostPrice(),
                line.getSellingPrice());

        if (grnLineSaved) {
            // Batches table insert
            String batchSql = "INSERT INTO Batches (batch_number, product_id, expiry_date, manufacture_date, qty_received, qty_remaining, cost_price, selling_price, supplier_id, received_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";

            CrudUtil.execute(batchSql,
                    line.getBatchNumber(),
                    line.getProductId(),
                    line.getExpiryDate(),
                    line.getManufactureDate(),
                    line.getQuantity(),
                    line.getQuantity(), // qty_remaining = qty_received
                    line.getCostPrice(),
                    line.getSellingPrice(),
                    supplierId);
        }

        return grnLineSaved;
    } //GRNine

    public String generateNextGrnNumber() throws SQLException {
        String sql = "SELECT grn_number FROM GRN ORDER BY grn_id DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql);

        if (rs.next()) {
            String lastNumber = rs.getString("grn_number");
            try {
                int num = Integer.parseInt(lastNumber.split("-")[2]);
                return "GRN-" + LocalDate.now().getYear() + "-" + String.format("%03d", num + 1);
            } catch (Exception e) {
                return "GRN-" + LocalDate.now().getYear() + "-001";
            }
        }
        return "GRN-" + LocalDate.now().getYear() + "-001";
    } //GRN

}
