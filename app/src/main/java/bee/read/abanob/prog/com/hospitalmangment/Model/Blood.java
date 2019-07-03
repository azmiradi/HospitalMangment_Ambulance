package bee.read.abanob.prog.com.hospitalmangment.Model;

public class Blood {
   private String  id;
   private  String type;
   private String quantity;
    private String blood_types_id;
    private String exp;
    private String kind;

    public Blood(){}
    public Blood(String id, String type) {
        this.id = id;
        this.type = type;
    }



    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
