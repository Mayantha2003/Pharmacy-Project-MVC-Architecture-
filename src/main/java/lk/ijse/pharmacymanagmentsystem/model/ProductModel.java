package lk.ijse.pharmacymanagmentsystem.model;

import lk.ijse.pharmacymanagmentsystem.dto.ProductDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductModel {

    public boolean saveProduct(ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO Products (code, name, pack_size, cat_id, generic_name, strength, dosage_form, company) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return CrudUtil.<Boolean>execute(sql,
                dto.getCode(),
                dto.getName(),
                dto.getPackSize(),
                dto.getCatId(),
                dto.getGenericName(),
                dto.getStrength(),
                dto.getDosageForm(),
                dto.getCompany());
    }

    public boolean updateProduct(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET code = ?, name = ?, pack_size = ?, cat_id = ?, "
                + "generic_name = ?, strength = ?, dosage_form = ?, company = ? WHERE product_id = ?";

        return CrudUtil.<Boolean>execute(sql,
                dto.getCode(),
                dto.getName(),
                dto.getPackSize(),
                dto.getCatId(),
                dto.getGenericName(),
                dto.getStrength(),
                dto.getDosageForm(),
                dto.getCompany(),
                dto.getProductId());
    }

    public boolean deleteProduct(long productId) throws SQLException {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        return CrudUtil.<Boolean>execute(sql, productId);
    }

    public ProductDTO searchProduct(long productId) throws SQLException {
        String sql = "SELECT * FROM Products WHERE product_id = ?";
        ResultSet rs = CrudUtil.execute(sql, productId);

        if (rs.next()) {
            return new ProductDTO(
                    rs.getLong("product_id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("pack_size"),
                    rs.getLong("cat_id"),
                    rs.getString("generic_name"),
                    rs.getString("strength"),
                    rs.getString("dosage_form"),
                    rs.getString("company")
            );
        }
        return null;
    }

    public List<ProductDTO> getAllProducts() throws SQLException {
        String sql = "SELECT p.*, c.name AS cat_name "
                + "FROM Products p "
                + "JOIN Categories c ON p.cat_id = c.cat_id "
                + "ORDER BY p.product_id ASC";

        ResultSet rs = CrudUtil.execute(sql);

        List<ProductDTO> list = new ArrayList<>();
        while (rs.next()) {
            ProductDTO dto = new ProductDTO(
                    rs.getLong("product_id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("pack_size"),
                    rs.getLong("cat_id"),
                    rs.getString("generic_name"),
                    rs.getString("strength"),
                    rs.getString("dosage_form"),
                    rs.getString("company")
            );
            list.add(dto);
        }
        return list;
    }


    public ProductDTO searchExactProduct(String searchText) throws SQLException {

        String sql = "SELECT p.*, c.name AS cat_name "
                + "FROM Products p "
                + "JOIN Categories c ON p.cat_id = c.cat_id "
                + "WHERE p.product_id = ? OR p.code = ?";

        long id = 0;
        try {
            id = Long.parseLong(searchText);
        } catch (NumberFormatException e) {
            id = -1;
        }

        ResultSet rs = CrudUtil.execute(sql, id, searchText);

        if (rs.next()) {
            return new ProductDTO(
                    rs.getLong("product_id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("pack_size"),
                    rs.getLong("cat_id"),
                    rs.getString("generic_name"),
                    rs.getString("strength"),
                    rs.getString("dosage_form"),
                    rs.getString("company")
            );
        }
        return null;
    }

    // GRN form Product dropdown loading
    public ObservableList<String> getAllProductsForCombo() throws SQLException {
        String sql = "SELECT product_id, code, name FROM Products ORDER BY name ASC";

        ResultSet rs = CrudUtil.execute(sql);

        ObservableList<String> list = FXCollections.observableArrayList();
        while (rs.next()) {
            String display = rs.getString("name") + " (" + rs.getString("code") + ")";
            list.add(display);
        }
        return list;
    }
    //GRN
    public long getProductIdFromDisplay(String displayText) throws SQLException {
        if (displayText == null || displayText.isEmpty()) {
            return -1;
        }

        String code = displayText.substring(displayText.lastIndexOf("(") + 1, displayText.lastIndexOf(")"));

        String sql = "SELECT product_id FROM Products WHERE code = ?";
        ResultSet rs = CrudUtil.execute(sql, code);

        if (rs.next()) {
            return rs.getLong("product_id");
        }
        return -1;
    }

}
