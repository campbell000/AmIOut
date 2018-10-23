package edu.vt.cs5560.amiout.services.preprocessing;

import org.springframework.web.client.RestTemplate;

public class GeoEncoder {
    private static final String URL_PREFIX = "https://nominatim.openstreetmap.org/search?q=";
    private static final String URL_SUFFIX = "&format=json&polygon_geojson=1";

    public GeoEncodeResult getGeoEncodeForLocation(String locationString)
    {
        RestTemplate client = new RestTemplate();
        StringBuilder builder = new StringBuilder();

        builder.append(URL_PREFIX);
        builder.append(locationString);
        builder.append(URL_SUFFIX);
        return client.getForObject(builder.toString(), GeoEncodeResult[].class)[0];
    }

    public static void main(String[] args)
    {
        GeoEncodeResult results = new GeoEncoder().getGeoEncodeForLocation("Falls Church, Virginia");
        System.out.println();
    }
}
