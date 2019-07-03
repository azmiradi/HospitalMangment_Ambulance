package bee.read.abanob.prog.com.hospitalmangment.Model;

public class Order {
    private String quantity;
    private String date;
    private String blood_types_id;
    private String paramedics_id;
    public Order(){}

    public Order(String quantity, String date, String blood_types_id, String paramedics_id) {
        this.quantity = quantity;
        this.date = date;
        this.blood_types_id = blood_types_id;
        this.paramedics_id = paramedics_id;
    }

    public String getParamedics_id() {
        return paramedics_id;
    }

    public void setParamedics_id(String paramedics_id) {
        this.paramedics_id = paramedics_id;
    }

    public String getBlood_types_id() {
        return blood_types_id;
    }

    public void setBlood_types_id(String blood_types_id) {
        this.blood_types_id = blood_types_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
