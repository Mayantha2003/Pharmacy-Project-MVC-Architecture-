package lk.ijse.pharmacymanagmentsystem.dto;

import java.time.LocalDate;

public class InvoiceDetailDTO {

    private long invoiceDetailId;
    private long invoiceId;
    private long batchId;
    private String batchNumber;
    private LocalDate expiryDate;
    private String productName;
    private int quantity;
    private double sellingPrice;
    private double lineTotal;

    public InvoiceDetailDTO() {
    }

    public InvoiceDetailDTO(long invoiceId, long batchId, String batchNumber, LocalDate expiryDate, String productName, int quantity, double sellingPrice, double lineTotal) {
        this.invoiceId = invoiceId;
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.productName = productName;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.lineTotal = lineTotal;
    }

    public InvoiceDetailDTO(long invoiceDetailId, long invoiceId, long batchId, String batchNumber, LocalDate expiryDate, String productName, int quantity, double sellingPrice, double lineTotal) {
        this.invoiceDetailId = invoiceDetailId;
        this.invoiceId = invoiceId;
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.productName = productName;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.lineTotal = lineTotal;
    }

    public long getInvoiceDetailId() {
        return invoiceDetailId;
    }

    public void setInvoiceDetailId(long invoiceDetailId) {
        this.invoiceDetailId = invoiceDetailId;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.sellingPrice;
    }

    @Override
    public String toString() {
        return "InvoiceDetailDTO{" + "invoiceDetailId=" + invoiceDetailId
                + ", invoiceId=" + invoiceId + ", batchId=" + batchId
                + ", batchNumber=" + batchNumber + ", expiryDate="
                + expiryDate + ", productName=" + productName + ", quantity=" + quantity
                + ", sellingPrice=" + sellingPrice + ", lineTotal=" + lineTotal + '}';
    }

}
