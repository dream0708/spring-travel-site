package spring.travel.api.model.weather;

import java.util.List;

public class DailyForecast {

    private City city;

    private List<Forecast> forecasts;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }
}
