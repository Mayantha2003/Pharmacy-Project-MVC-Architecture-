package lk.ijse.pharmacymanagmentsystem.dto;

import java.time.LocalDate;

public class BatchDTO {

    private long batchId;
    private String batchNumber;
    private long productId;
    private String productName;
    private LocalDate expiryDate;
    private LocalDate manufactureDate;
    private int qtyReceived;
    private int qtyRemaining;
    private double costPrice;
    private double sellingPrice;
    private long supplierId;
    private LocalDate receivedDate;

    public BatchDTO() {
    }

    public BatchDTO(String batchNumber, long productId, String productName, LocalDate expiryDate,
                    LocalDate manufactureDate, int qtyReceived, int qtyRemaining, double costPrice,
                    double sellingPrice, long supplierId, LocalDate receivedDate) {
        this.batchNumber = batchNumber;
        this.productId = productId;
        this.productName = productName;
        this.expiryDate = expiryDate;
        this.manufactureDate = manufactureDate;
        this.qtyReceived = qtyReceived;
        this.qtyRemaining = qtyRemaining;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.supplierId = supplierId;
        this.receivedDate = receivedDate;
    }

    public BatchDTO(long batchId, String batchNumber, long productId, String productName,
                    LocalDate expiryDate, LocalDate manufactureDate, int qtyReceived, int
                            qtyRemaining, double costPrice, double sellingPrice, long supplierId, LocalDate receivedDate) {
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.productId = productId;
        this.productName = productName;
        this.expiryDate = expiryDate;
        this.manufactureDate = manufactureDate;
        this.qtyReceived = qtyReceived;
        this.qtyRemaining = qtyRemaining;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.supplierId = supplierId;
        this.receivedDate = receivedDate;
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

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public int getQtyReceived() {
        return qtyReceived;
    }

    public void setQtyReceived(int qtyReceived) {
        this.qtyReceived = qtyReceived;
    }

    public int getQtyRemaining() {
        return qtyRemaining;
    }

    public void setQtyRemaining(int qtyRemaining) {
        this.qtyRemaining = qtyRemaining;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Override
    public String toString() {
        return "BatchDTO{" + "batchId=" + batchId + 
                ", batchNumber=" + batchNumber + ", productId=" + productId + 
                ", productName=" + productName + ", expiryDate=" + expiryDate + 
                ", manufactureDate=" + manufactureDate + ", qtyReceived=" + qtyReceived + 
                ", qtyRemaining=" + qtyRemaining + ", costPrice=" + costPrice + ", sellingPrice=" 
                + sellingPrice + ", supplierId=" + supplierId + ", receivedDate=" + receivedDate + '}';
    }
    
    

}
