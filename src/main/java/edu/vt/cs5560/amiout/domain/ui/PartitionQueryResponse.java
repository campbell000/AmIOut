package edu.vt.cs5560.amiout.domain.ui;

import java.util.List;

public class PartitionQueryResponse
{
    private List<UIDataPoint> dataPoints;

    public List<UIDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<UIDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
