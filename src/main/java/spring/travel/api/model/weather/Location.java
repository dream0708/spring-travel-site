package spring.travel.api.model.weather;

public class Location {

    private int cityId;

    private double longitude;

    private double latitude;

    public Location() {
    }

    public Location(int cityId, double longitude, double latitude) {
        this.cityId = cityId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
