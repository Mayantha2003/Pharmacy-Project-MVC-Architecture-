package lk.ijse.pharmacymanagmentsystem.model;

import lk.ijse.pharmacymanagmentsystem.dto.UserDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    public UserDTO login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        ResultSet rs = CrudUtil.execute(sql, username, password);
        

        if (rs.next()) {
            UserDTO user = new UserDTO();
            user.setUserId(rs.getLong("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setFullName(rs.getString("full_name"));
            return user;
        }
        return null;
    }

    public boolean saveUser(UserDTO userDTO) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Users (username, password, contact, role, full_name) VALUES (?, ?, ?, ?, ?)",
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getContact(),
                userDTO.getRole(),
                userDTO.getFullName()
        );
    }

    public UserDTO searchUser(Long userId) throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Users WHERE user_id = ?", userId);
        if (rs.next()) {
            return new UserDTO(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("contact"),
                    rs.getString("role"),
                    rs.getString("full_name")
            );
        }
        return null;
    }

    public boolean updateUser(UserDTO userDTO) throws SQLException {

        return CrudUtil.execute(
                "UPDATE Users SET username = ?, password = ?, contact = ?, role = ?, full_name = ? WHERE user_id = ?",
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getContact(),
                userDTO.getRole(),
                userDTO.getFullName(),
                userDTO.getUserId()
        );
    }

    public boolean deleteUser(Long userId) throws SQLException {
        return CrudUtil.execute("DELETE FROM Users WHERE user_id = ?", userId);
    }

    public List<UserDTO> getAllUsers() throws SQLException {

        ResultSet rs = CrudUtil.execute("SELECT * FROM Users ORDER BY user_id ASC");
        List<UserDTO> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new UserDTO(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("contact"),
                    rs.getString("role"),
                    rs.getString("full_name")
            ));
        }
        return list;
    }
}
