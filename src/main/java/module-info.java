module lk.ijse.pharmacymanagmentsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires net.sf.jasperreports.core;


    opens lk.ijse.pharmacymanagmentsystem.controller to javafx.fxml;
    opens lk.ijse.pharmacymanagmentsystem.dto to javafx.base;
    exports lk.ijse.pharmacymanagmentsystem;
    exports lk.ijse.pharmacymanagmentsystem.controller;
    exports lk.ijse.pharmacymanagmentsystem.dto;
    
}
