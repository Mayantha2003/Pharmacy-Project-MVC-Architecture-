package lk.ijse.pharmacymanagmentsystem.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDTO {

    private long invoiceId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private long customerId;
    private String customerName;
    private double totalAmount;
    private double paidAmount;
    private double balance;
    private String paymentMethod;
    private long cashierId;

    private List<InvoiceDetailDTO> details = new ArrayList<>();

    public InvoiceDTO() {
    }

    public InvoiceDTO(String invoiceNumber, LocalDate invoiceDate, long customerId, String customerName, double totalAmount, double paidAmount, double balance, String paymentMethod, long cashierId) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerId = customerId;
        this.customerName = customerName;

        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.paymentMethod = paymentMethod;

        this.cashierId = cashierId;
    }

    public InvoiceDTO(long invoiceId, String invoiceNumber, LocalDate invoiceDate, long customerId, String customerName, double totalAmount, double paidAmount, double balance, String paymentMethod, long cashierId) {
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerId = customerId;
        this.customerName = customerName;

        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.paymentMethod = paymentMethod;

        this.cashierId = cashierId;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;

    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public long getCashierId() {
        return cashierId;
    }

    public void setCashierId(long cashierId) {
        this.cashierId = cashierId;
    }

    public List<InvoiceDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetailDTO> details) {
        this.details = details;
    }

    public void addDetail(InvoiceDetailDTO detail) {
        this.details.add(detail);
        this.totalAmount += detail.getLineTotal();
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" + "invoiceId=" + invoiceId + ", invoiceNumber="
                + invoiceNumber + ", invoiceDate=" + invoiceDate + ", customerId="
                + customerId + ", customerName=" + customerName + ", totalAmount="
                + totalAmount + ", paidAmount=" + paidAmount + ", balance=" 
                + balance + ", paymentMethod=" + paymentMethod + ", cashierId=" 
                + cashierId + ", details=" + details + '}';
    }

   

}
