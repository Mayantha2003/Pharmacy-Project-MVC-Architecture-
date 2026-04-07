package lk.ijse.pharmacymanagmentsystem.dto;


public class CategorieDTO {
    
    private Long categorie_Id;
    private String categorie_name;
    private String categorie_type;

    public CategorieDTO() {
    }

    public CategorieDTO(String categorie_name, String categorie_type) {
        this.categorie_name = categorie_name;
        this.categorie_type = categorie_type;
    }

    public CategorieDTO(Long categorie_Id, String categorie_name, String categorie_type) {
        this.categorie_Id = categorie_Id;
        this.categorie_name = categorie_name;
        this.categorie_type = categorie_type;
    }

    public Long getCategorie_Id() {
        return categorie_Id;
    }

    public void setCategorie_Id(Long categorie_Id) {
        this.categorie_Id = categorie_Id;
    }

    public String getCategorie_name() {
        return categorie_name;
    }

    public void setCategorie_name(String categorie_name) {
        this.categorie_name = categorie_name;
    }

    public String getCategorie_type() {
        return categorie_type;
    }

    public void setCategorie_type(String categorie_type) {
        this.categorie_type = categorie_type;
    }

    @Override
    public String toString() {
        return "CategorieDTO{" + "categorie_Id=" + categorie_Id + ", categorie_name=" + categorie_name + ", categorie_type=" + categorie_type + '}';
    }
    
    
    
    
    
}
