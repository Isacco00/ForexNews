package org.example;

import java.time.LocalDate;

public class ForexNewsBean {
    public ForexNewsBean(String eventName, String eventCurrency, String time, LocalDate eventDate, String actual, String forecast, String previous) {
        this.eventName = eventName;
        this.eventCurrency = eventCurrency;
        this.time = time;
        this.eventDate = eventDate;
        this.actual = actual;
        this.forecast = forecast;
        this.previous = previous;
    }

    private String eventName;
    private String eventCurrency;
    private LocalDate eventDate;

    private String time;
    private String actual;
    private String forecast;
    private String previous;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventCurrency() {
        return eventCurrency;
    }

    public void setEventCurrency(String eventCurrency) {
        this.eventCurrency = eventCurrency;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
