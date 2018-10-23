package edu.vt.cs5560.amiout.services.preprocessing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoEncodeResult {
    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("osm_id")
    private String osmId;

    @JsonProperty("boundingbox")
    private double[] boundingBox;
    private double lat;
    private double lon;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("geojson")
    private GeoJsonPolygon geoJsonPolygon;

    public String getPlaceId() {
        return placeId;
    }


    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getOsmId() {
        return osmId;
    }

    public void setOsmId(String osmId) {
        this.osmId = osmId;
    }

    public double[] getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String[] bbArr)
    {
        double[] bb = new double[bbArr.length];
        for (int i = 0; i < bbArr.length; i++)
        {
            bb[i] = Double.parseDouble(bbArr[i]);
        }
        this.boundingBox = bb;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GeoJsonPolygon getGeoJsonPolygon() {
        return geoJsonPolygon;
    }

    public void setGeoJsonPolygon(GeoJsonPolygon geoJsonPolygon) {
        this.geoJsonPolygon = geoJsonPolygon;
    }
}
