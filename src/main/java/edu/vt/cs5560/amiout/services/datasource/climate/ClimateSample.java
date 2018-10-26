package edu.vt.cs5560.amiout.services.datasource.climate;

import java.util.Date;

public class ClimateSample
{
    public static final double UNKNOWN_VALUE = -99999;
    public static final String DATE_FORMAT = "yyyyMMdd";

    private Date date;
    private double lat;
    private double lon;
    private double maxTempCelsius;
    private double minTempCelsius;
    private double avgTempCelsium; // not the same as (min + max)/2
    private double precipitationInMM;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getMaxTempCelsius() {
        return maxTempCelsius;
    }

    public void setMaxTempCelsius(double maxTempCelsius) {
        this.maxTempCelsius = maxTempCelsius;
    }

    public double getMinTempCelsius() {
        return minTempCelsius;
    }

    public void setMinTempCelsius(double minTempCelsius) {
        this.minTempCelsius = minTempCelsius;
    }

    public double getAvgTempCelsium() {
        return avgTempCelsium;
    }

    public void setAvgTempCelsium(double avgTempCelsium) {
        this.avgTempCelsium = avgTempCelsium;
    }

    public double getPrecipitationInMM() {
        return precipitationInMM;
    }

    public void setPrecipitationInMM(double precipitationInMM) {
        this.precipitationInMM = precipitationInMM;
    }
}
