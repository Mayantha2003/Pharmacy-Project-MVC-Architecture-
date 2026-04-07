package lk.ijse.pharmacymanagmentsystem.model;

import lk.ijse.pharmacymanagmentsystem.dto.CustomerDTO;
import lk.ijse.pharmacymanagmentsystem.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerModel {

    // Save Customer
    public boolean saveCustomer(CustomerDTO customerDTO) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Customers (name, address, contact, email) VALUES (?, ?, ?, ?)",
                customerDTO.getCustomerName(),
                customerDTO.getCustomerAddress(),
                customerDTO.getCustomerContactNumber(),
                customerDTO.getCustomerEmail()
        );
    }

    // Search Customer by ID
    public CustomerDTO searchCustomer(Long customerId) throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Customers WHERE customer_id = ?", customerId);
        CustomerDTO customerDTO = null;

        if (rs.next()) {
            Long cusId = rs.getLong("customer_id");
            String cusName = rs.getString("name");
            String cusAddress = rs.getString("address");
            String cusContact = rs.getString("contact");
            String cusEmail = rs.getString("email");

            customerDTO = new CustomerDTO(cusId, cusName, cusContact, cusAddress, cusEmail);
        }
        return customerDTO;
    }

    // Update Customer
    public boolean updateCustomer(CustomerDTO customerDTO) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Customers SET name = ?, address = ?, contact = ?, email = ? WHERE customer_id = ?",
                customerDTO.getCustomerName(),
                customerDTO.getCustomerAddress(),
                customerDTO.getCustomerContactNumber(),
                customerDTO.getCustomerEmail(),
                customerDTO.getCustomerId()
        );
    }

    // Delete Customer
    public boolean deleteCustomer(Long customerId) throws SQLException {
        return CrudUtil.execute("DELETE FROM Customers WHERE customer_id = ?", customerId);
    }

    // Get All Customers
    public List<CustomerDTO> getAllCustomers() throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Customers");
        List<CustomerDTO> customerList = new ArrayList<>();

        while (rs.next()) {
            Long cusId = rs.getLong("customer_id");
            String cusName = rs.getString("name");
            String cusAddress = rs.getString("address");
            String cusContact = rs.getString("contact");
            String cusEmail = rs.getString("email");

            CustomerDTO customerDTO = new CustomerDTO(cusId, cusName, cusContact, cusAddress, cusEmail);
            customerList.add(customerDTO);
        }
        return customerList;
    }

    public long getCustomerIdByName(String name) throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT customer_id FROM Customers WHERE name = ?", name);

        if (rs.next()) {
            return rs.getLong("customer_id");
        }
        return 0;
    }
}
