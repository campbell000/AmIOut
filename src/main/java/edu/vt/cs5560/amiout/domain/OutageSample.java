package edu.vt.cs5560.amiout.domain;

import java.sql.Time;
import java.util.Date;

public class OutageSample
{
    private Date date;
    private NERCRegion nercRegion;
    private Time time;
    private String areaAffected;
    private String disturbanceType;
    private Integer energyLossInMegaWatts;
    private Integer numCustomersAffected;
    private Date restorationDate;

    private Time restorationTime;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public NERCRegion getNercRegion() {
        return nercRegion;
    }

    public void setNercRegion(NERCRegion nercRegion) {
        this.nercRegion = nercRegion;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

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

    public Date getRestorationDate() {
        return restorationDate;
    }

    public void setRestorationDate(Date restorationDate) {
        this.restorationDate = restorationDate;
    }

    public Time getRestorationTime() {
        return restorationTime;
    }

    public void setRestorationTime(Time restorationTime) {
        this.restorationTime = restorationTime;
    }

    @Override
    public String toString() {
        return "OutageSample{" +
                "date=" + date +
                ", nercRegion=" + nercRegion +
                ", time=" + time +
                ", areaAffected='" + areaAffected + '\'' +
                ", disturbanceType='" + disturbanceType + '\'' +
                ", energyLossInMegaWatts=" + energyLossInMegaWatts +
                ", numCustomersAffected=" + numCustomersAffected +
                ", restorationDate=" + restorationDate +
                ", restorationTime=" + restorationTime +
                '}';
    }

    public boolean sampleIsUsable()
    {
        return this.getDate() != null;
    }
}
