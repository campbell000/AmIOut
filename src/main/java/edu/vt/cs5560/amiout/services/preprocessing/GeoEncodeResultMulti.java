package edu.vt.cs5560.amiout.services.preprocessing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Alex_Lappy_486 on 11/30/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoEncodeResultMulti
{
    @JsonProperty("geojson")
    public GeoJsonMultiPolygon geoJsonPolygon;
}
