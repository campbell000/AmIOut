package edu.vt.cs5560.amiout.domain.parsers;

public class GeoPoint
{
    double lat;
    double lon;

    public GeoPoint(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
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
}
