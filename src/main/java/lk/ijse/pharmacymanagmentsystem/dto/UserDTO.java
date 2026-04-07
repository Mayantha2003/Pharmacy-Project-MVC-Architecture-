package lk.ijse.pharmacymanagmentsystem.dto;

public class UserDTO {

    private Long userId;
    private String username;
    private String password;
    private String contact;
    private String role;
    private String fullName;

    public UserDTO() {
    }

    public UserDTO(String username, String password, String contact, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.fullName = fullName;
    }

    public UserDTO(Long userId, String username, String password, String contact, String role, String fullName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "UserDTO{"
                + "userId=" + userId
                + ", username='" + username + '\''
                + ", contact='" + contact + '\''
                + ", role='" + role + '\''
                + ", fullName='" + fullName + '\''
                + '}';
    }
}
