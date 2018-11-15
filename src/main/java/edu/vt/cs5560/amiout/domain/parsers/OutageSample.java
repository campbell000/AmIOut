package edu.vt.cs5560.amiout.domain.parsers;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class OutageSample
{
    private int id;
    private String source;

    private Date dateTimeOcurred;
    private List nercRegion;
    private String areaAffected;
    private String disturbanceType;
    private Integer energyLossInMegaWatts;
    private Integer numCustomersAffected;
    private Date restorationDateTime;
    private GeoPoint centerPoint;

    private Time restorationTime;

    public String getAreaAffected() {
        return areaAffected;
    }

    public void setAreaAffected(String areaAffected) {
        this.areaAffected = areaAffected;
    }

    public String getDisturbanceType() {
        return disturbanceType;
    }

    public void setDisturbanceType(String disturbanceType) {
        this.disturbanceType = disturbanceType;
    }

    public Integer getEnergyLossInMegaWatts() {
        return energyLossInMegaWatts;
    }

    public void setEnergyLossInMegaWatts(Integer energyLossInMegaWatts) {
        this.energyLossInMegaWatts = energyLossInMegaWatts;
    }

    public Integer getNumCustomersAffected() {
        return numCustomersAffected;
    }

    public void setNumCustomersAffected(Integer numCustomersAffected) {
        this.numCustomersAffected = numCustomersAffected;
    }

    public boolean sampleIsUsable()
    {
        return this.getDateTimeOcurred() != null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTimeOcurred() {
        return dateTimeOcurred;
    }

    public void setDateTimeOcurred(Date dateTimeOcurred) {
        this.dateTimeOcurred = dateTimeOcurred;
    }

    public Date getRestorationDateTime() {
        return restorationDateTime;
    }

    public void setRestorationDateTime(Date restorationDateTime) {
        this.restorationDateTime = restorationDateTime;
    }

    public List getNercRegion() {
        return nercRegion;
    }

    public void setNercRegion(List nercRegion) {
        this.nercRegion = nercRegion;
    }

    public GeoPoint getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(GeoPoint centerPoint) {
        this.centerPoint = centerPoint;
    }
}
