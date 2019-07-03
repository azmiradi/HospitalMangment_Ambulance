package bee.read.abanob.prog.com.hospitalmangment.Model;

public class Location {
    private double lang;
    private double lat;
    public Location(){}
    public Location(double lang, double lat) {
        this.lang = lang;
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
