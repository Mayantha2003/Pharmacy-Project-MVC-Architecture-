package lk.ijse.pharmacymanagmentsystem.util;

import lk.ijse.pharmacymanagmentsystem.dto.UserDTO;

public class SessionManager {

    public static UserDTO currentUser;

    public static void setUser(UserDTO user) {
        currentUser = user;
    }

    public static UserDTO getUser() {
        return currentUser;
    }

}
