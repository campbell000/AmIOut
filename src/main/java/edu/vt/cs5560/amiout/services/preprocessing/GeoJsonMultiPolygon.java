package edu.vt.cs5560.amiout.services.preprocessing;

/**
 * Created by Alex_Lappy_486 on 11/30/18.
 */
public class GeoJsonMultiPolygon
{
    private String type;
    double[][][][] coordinates;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[][][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][][] coordinates) {
        this.coordinates = coordinates;
    }
}
