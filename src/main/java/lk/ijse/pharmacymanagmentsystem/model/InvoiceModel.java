package lk.ijse.pharmacymanagmentsystem.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lk.ijse.pharmacymanagmentsystem.dbc.DBConnection;
import lk.ijse.pharmacymanagmentsystem.dto.InvoiceDTO;
import lk.ijse.pharmacymanagmentsystem.dto.InvoiceDetailDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

public class InvoiceModel {

    public long saveInvoiceWithTransaction(InvoiceDTO invoiceDTO) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Invoice header save invoice_id 
            long invoiceId = saveInvoiceHeader(invoiceDTO);
            if (invoiceId == -1) {
                conn.rollback();
                return -1;
            }

            // 2. detail Invoice_Details insert + Batch qty_remaining 
            for (InvoiceDetailDTO detail : invoiceDTO.getDetails()) {
                boolean detailSaved = saveInvoiceDetail(invoiceId, detail);
                if (!detailSaved) {
                    conn.rollback();
                    return -1;
                }

                // Batch stock 
                boolean stockUpdated = updateBatchStock(detail.getBatchId(), detail.getQuantity());
                if (!stockUpdated) {
                    conn.rollback();
                    return -1;
                }
            }

            // 3. Payment insert 
            boolean paymentSaved = saveInvoicePayment(invoiceId, invoiceDTO);
            if (!paymentSaved) {
                conn.rollback();
                return -1;
            }

            conn.commit();
            return invoiceId;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    private long saveInvoiceHeader(InvoiceDTO dto) throws SQLException {
        String sql = "INSERT INTO Invoice (invoice_number, invoice_date, customer_id, total_amount, paid_amount, cashier_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        CrudUtil.execute(sql,
                dto.getInvoiceNumber(),
                dto.getInvoiceDate(),
                dto.getCustomerId() == 0 ? null : dto.getCustomerId(),
                dto.getTotalAmount(),
                dto.getPaidAmount(),
                dto.getCashierId());

        String idSql = "SELECT LAST_INSERT_ID()";
        ResultSet rs = CrudUtil.execute(idSql);
        if (rs.next()) {
            return rs.getLong(1);
        }
        return -1;
    }

    private boolean saveInvoiceDetail(long invoiceId, InvoiceDetailDTO detail) throws SQLException {
        String sql = "INSERT INTO Invoice_Details (invoice_id, batch_id, quantity, selling_price) "
                + "VALUES (?, ?, ?, ?)";

        return CrudUtil.<Boolean>execute(sql,
                invoiceId,
                detail.getBatchId(),
                detail.getQuantity(),
                detail.getSellingPrice());
    }

    private boolean saveInvoicePayment(long invoiceId, InvoiceDTO dto) throws SQLException {
        String sql = "INSERT INTO Invoice_Payment (invoice_id, payment_method, amount) "
                + "VALUES (?, ?, ?)";

        return CrudUtil.<Boolean>execute(sql,
                invoiceId,
                dto.getPaymentMethod(),
                dto.getPaidAmount());
    }

    private boolean updateBatchStock(long batchId, int soldQty) throws SQLException {
        String sql = "UPDATE Batches SET qty_remaining = qty_remaining - ? WHERE batch_id = ? AND qty_remaining >= ?";

        return CrudUtil.<Boolean>execute(sql, soldQty, batchId, soldQty);
    }

    public String generateNextInvoiceNumber() throws SQLException {
        int currentYear = java.time.LocalDate.now().getYear();
        
        String sql = "SELECT invoice_number FROM Invoice WHERE invoice_number LIKE ? ORDER BY invoice_id DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql, "INV-" + currentYear + "-%");

        if (rs.next()) {
            String lastNumber = rs.getString("invoice_number");
            String[] parts = lastNumber.split("-");
            int num = Integer.parseInt(parts[2]);
            return "INV-" + currentYear + "-" + String.format("%03d", num + 1);
        }
        return "INV-" + currentYear + "-001";
    }
}
