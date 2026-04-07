package lk.ijse.pharmacymanagmentsystem.dto;

public class ProductDTO {

    private long productId;
    private String code;
    private String name;
    private String packSize;
    private long catId;
    private String genericName;
    private String strength;
    private String dosageForm;
    private String company;

    public ProductDTO() {
    }

    public ProductDTO(String code, String name, String packSize, long catId, String genericName, String strength, String dosageForm, String company) {
        this.code = code;
        this.name = name;
        this.packSize = packSize;
        this.catId = catId;
        this.genericName = genericName;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.company = company;
    }

    public ProductDTO(long productId, String code, String name, String packSize, long catId, String genericName, String strength, String dosageForm, String company) {
        this.productId = productId;
        this.code = code;
        this.name = name;
        this.packSize = packSize;
        this.catId = catId;
        this.genericName = genericName;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.company = company;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "ProductDTO{" + "productId=" + productId + ", code=" + code + ", name=" + name + ", packSize=" + packSize + ", catId=" + catId + ", genericName=" + genericName + ", strength=" + strength + ", dosageForm=" + dosageForm + ", company=" + company + '}';
    }

}
