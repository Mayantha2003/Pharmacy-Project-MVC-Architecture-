package lk.ijse.pharmacymanagmentsystem.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.pharmacymanagmentsystem.dto.SupplierDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

public class SupplierModel {

    public boolean saveSupplier(SupplierDTO supplierDTO) throws SQLException {

        boolean result = CrudUtil.execute(
                "INSERT INTO Suppliers (supplier_name,address,contact_number,email) VALUES (?,?,?,?)",
                supplierDTO.getSupplierName(),
                supplierDTO.getSupplierAddress(),
                supplierDTO.getSupplierContactNumber(),
                supplierDTO.getSupplierEmail());

        return result;

    }

    public SupplierDTO searchSupplier(Long supplierId) throws SQLException {

        ResultSet rs = CrudUtil.execute("SELECT * FROM Suppliers WHERE supplier_id =?", supplierId);

        SupplierDTO supplierDTO = null;

        if (rs.next()) {
            Long supId = rs.getLong("supplier_id");
            String supName = rs.getString("supplier_name");
            String supAddress = rs.getString("address");
            String supContactNumber = rs.getString("contact_number");
            String supEmail = rs.getString("email");

            supplierDTO = new SupplierDTO(supId, supName, supAddress, supContactNumber, supEmail);
        }

        return supplierDTO;

    }

    public boolean updateSupplier(SupplierDTO supplierDTO) throws SQLException {

        boolean result = CrudUtil.execute(
                "UPDATE Suppliers SET supplier_name=?, address=?, contact_number=?, email=? WHERE supplier_id=?",
                supplierDTO.getSupplierName(),
                supplierDTO.getSupplierAddress(),
                supplierDTO.getSupplierContactNumber(),
                supplierDTO.getSupplierEmail(),
                supplierDTO.getSupplierId());

        return result;
    }

    public boolean deleteSupplier(Long supplierId) throws SQLException {

        boolean result = CrudUtil.execute("DELETE FROM Suppliers WHERE supplier_id=?", supplierId);
        return result;

    }

    public List<SupplierDTO> getAllSuppliers() throws SQLException {

        ResultSet rs = CrudUtil.execute("SELECT * FROM Suppliers");

        List<SupplierDTO> supplierList = new ArrayList<>();

        while (rs.next()) {

            Long supId = rs.getLong("supplier_id");
            String supName = rs.getString("supplier_name");
            String supAddress = rs.getString("address");
            String supContactNumber = rs.getString("contact_number");
            String supEmail = rs.getString("email");

            SupplierDTO supplierDTO = new SupplierDTO(supId, supName, supAddress, supContactNumber, supEmail);
            supplierList.add(supplierDTO);
        }

        return supplierList;
    }

    // GRN form supplier dropdown loading
    public ObservableList<String> getAllSupplierNames() throws SQLException {
        String sql = "SELECT supplier_name FROM Suppliers ORDER BY supplier_name ASC";

        ResultSet rs = CrudUtil.execute(sql);

        ObservableList<String> list = FXCollections.observableArrayList();
        while (rs.next()) {
            list.add(rs.getString("supplier_name"));
        }
        return list;
    }

    // GRN form Supplier get Id by Name 
    public long getSupplierIdByName(String supplierName) throws SQLException {
        String sql = "SELECT supplier_id FROM Suppliers WHERE supplier_name = ?";

        ResultSet rs = CrudUtil.execute(sql, supplierName);

        if (rs.next()) {
            return rs.getLong("supplier_id");
        }
        return -1;
    }

}
