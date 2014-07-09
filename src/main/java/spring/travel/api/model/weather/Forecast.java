package spring.travel.api.model.weather;

public class Forecast {

    private long dt;

    private Temperatures temp;

    private Weather weather;

    private double speed;

    private double deg;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Temperatures getTemp() {
        return temp;
    }

    public void setTemp(Temperatures temp) {
        this.temp = temp;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
