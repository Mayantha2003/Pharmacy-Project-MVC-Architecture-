package lk.ijse.pharmacymanagmentsystem.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GRNDTO {

    private long grnId;
    private String grnNumber;
    private long supplierId;
    private String supplierName;
    private LocalDate grnDate;
    private double totalAmount;

    private String notes;
    private long receivedBy;

    private List<GRNLineDTO> lines = new ArrayList<>();

    public GRNDTO() {
    }

    public GRNDTO(String grnNumber, long supplierId, String supplierName, LocalDate grnDate, double totalAmount, String notes, long receivedBy) {
        this.grnNumber = grnNumber;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.grnDate = grnDate;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.receivedBy = receivedBy;
    }

    public GRNDTO(long grnId, String grnNumber, long supplierId, String supplierName, LocalDate grnDate, double totalAmount, String notes, long receivedBy) {
        this.grnId = grnId;
        this.grnNumber = grnNumber;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.grnDate = grnDate;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.receivedBy = receivedBy;
    }

    // Getters and Setters
    public long getGrnId() {
        return grnId;
    }

    public void setGrnId(long grnId) {
        this.grnId = grnId;
    }

    public String getGrnNumber() {
        return grnNumber;
    }

    public void setGrnNumber(String grnNumber) {
        this.grnNumber = grnNumber;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getGrnDate() {
        return grnDate;
    }

    public void setGrnDate(LocalDate grnDate) {
        this.grnDate = grnDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(long receivedBy) {
        this.receivedBy = receivedBy;
    }

    // Lines
    public List<GRNLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<GRNLineDTO> lines) {
        this.lines = lines;
    }

        public void addLine(GRNLineDTO line) {
            this.lines.add(line);
            this.totalAmount += line.getLineTotal();
        }

    @Override
    public String toString() {
        return "GRNDTO{"
                + "grnId=" + grnId
                + ", grnNumber='" + grnNumber + '\''
                + ", supplierName='" + supplierName + '\''
                + ", grnDate=" + grnDate
                + ", totalAmount=" + totalAmount
                + ", linesCount=" + lines.size()
                + '}';
    }
}
