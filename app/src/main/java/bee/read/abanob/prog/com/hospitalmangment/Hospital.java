package bee.read.abanob.prog.com.hospitalmangment;

public class Hospital {
    private String lat;
    private String lng;
    private String name;
    private String id;
    private String createdAt;
    public Hospital(){}

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Hospital(String lat, String lng, String name, String id, String createdAt) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
