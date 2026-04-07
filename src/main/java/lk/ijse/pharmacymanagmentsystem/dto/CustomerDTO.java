    package lk.ijse.pharmacymanagmentsystem.dto;

    public class CustomerDTO {

        private Long customerId;
        private String customerName;
        private String customerContactNumber;
        private String customerAddress;
        private String customerEmail;

        public CustomerDTO() {
        }

        public CustomerDTO(String customerName, String customerContactNumber, String customerAddress, String customerEmail) {
            this.customerName = customerName;
            this.customerContactNumber = customerContactNumber;
            this.customerAddress = customerAddress;
            this.customerEmail = customerEmail;
        }

        public CustomerDTO(Long customerId, String customerName, String customerContactNumber, String customerAddress, String customerEmail) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerContactNumber = customerContactNumber;
            this.customerAddress = customerAddress;
            this.customerEmail = customerEmail;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerContactNumber() {
            return customerContactNumber;
        }

        public void setCustomerContactNumber(String customerContactNumber) {
            this.customerContactNumber = customerContactNumber;
        }

        public String getCustomerAddress() {
            return customerAddress;
        }

        public void setCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        @Override
        public String toString() {
            return "CustomerDTO{" + "customerId=" + customerId + ", customerName=" + customerName + ", customerContactNumber=" + customerContactNumber + ", customerAddress=" + customerAddress + ", customerEmail=" + customerEmail + '}';
        }



    }
