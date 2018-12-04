package edu.vt.cs5560.amiout.domain.ui;

import java.util.Collection;
import java.util.List;

public class PartitionQueryResponse
{
    private Collection<UIDataPoint> dataPoints;

    public Collection<UIDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(Collection<UIDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
