package lk.ijse.pharmacymanagmentsystem.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.pharmacymanagmentsystem.dto.CategorieDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

public class CategorieModel {

    public boolean saveCategorie(CategorieDTO categorieDTO) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Categories (name, type) VALUES (?, ?)",
                categorieDTO.getCategorie_name(),
                categorieDTO.getCategorie_type()
        );
    }

    public CategorieDTO searchCategorie(Long categorie_Id) throws SQLException {

        ResultSet rs = CrudUtil.execute(
                "SELECT * FROM Categories WHERE cat_id = ?", categorie_Id);

        if (rs.next()) {
            return new CategorieDTO(
                    rs.getLong("cat_id"),
                    rs.getString("name"),
                    rs.getString("type")
            );
        }
        return null;
    }

    public boolean updateCategorie(CategorieDTO categorieDTO) throws SQLException {

        return CrudUtil.execute(
                "UPDATE Categories SET name = ?, type = ? WHERE cat_id = ?",
                categorieDTO.getCategorie_name(),
                categorieDTO.getCategorie_type(),
                categorieDTO.getCategorie_Id()
        );
    }

    public boolean deleteCategorie(Long categorie_Id) throws SQLException {
        return CrudUtil.execute("DELETE FROM Categories WHERE cat_id = ?", categorie_Id);
    }

    public List<CategorieDTO> getAllCategories() throws SQLException {

        ResultSet rs = CrudUtil.execute("SELECT * FROM Categories");

        List<CategorieDTO> CategorieList = new ArrayList<>();

        while (rs.next()) {

            Long catId = rs.getLong("cat_id");
            String catName = rs.getString("name");
            String catType = rs.getString("type");

            CategorieDTO CategorieDTO = new CategorieDTO(catId, catName, catType);
            CategorieList.add(CategorieDTO);
        }

        return CategorieList;
    }

    public boolean isCategoryUsedInProducts(Long categorie_Id) throws SQLException {
        String sql = "SELECT 1 FROM Products WHERE cat_id = ? LIMIT 1";

        ResultSet rs = CrudUtil.execute(sql, categorie_Id);

        return rs.next();
    }


    public String getCategoryDisplayName(long catId) throws SQLException {
        String sql = "SELECT name, type FROM Categories WHERE cat_id = ?";
        ResultSet rs = CrudUtil.execute(sql, catId);

        if (rs.next()) {
            return rs.getString("name") + " (" + rs.getString("type") + ")";
        }
        return "Unknown Category";
    }

    public ObservableList<String> getAllCategoriesForCombo() throws SQLException {
        String sql = "SELECT name, type FROM Categories ORDER BY type DESC, name ASC";
        ResultSet rs = CrudUtil.execute(sql);

        ObservableList<String> list = FXCollections.observableArrayList();
        while (rs.next()) {
            String display = rs.getString("name") + " (" + rs.getString("type") + ")";
            list.add(display);
        }
        return list;
    }

    public String getCatTypeFromDisplay(String displayText) throws SQLException {

        if (displayText == null || displayText.isEmpty()) {
            return "";
        }

        String categoryName = displayText.split(" \\(")[0].trim();

        String sql = "SELECT type FROM Categories WHERE name = ?";
        ResultSet rs = CrudUtil.execute(sql, categoryName);

        if (rs.next()) {
            return rs.getString("type");
        }
        return "";
    }

    public long getCatIdFromDisplay(String displayText) throws SQLException {
        if (displayText == null || displayText.isEmpty()) {
            return -1;
        }

        String categoryName = displayText.split(" \\(")[0].trim();

        String sql = "SELECT cat_id FROM Categories WHERE name = ?";
        ResultSet rs = CrudUtil.execute(sql, categoryName);

        if (rs.next()) {
            return rs.getLong("cat_id");
        }
        return -1;
    }
}
