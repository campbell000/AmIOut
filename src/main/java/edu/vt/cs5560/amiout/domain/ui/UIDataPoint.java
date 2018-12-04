package edu.vt.cs5560.amiout.domain.ui;

public class UIDataPoint
{
    Object dataPoint;
    UIDataType type;

    public UIDataPoint(UIDataType type, Object dataPoint)
    {
        this.type = type;
        this.dataPoint = dataPoint;
    }

    public Object getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(Object dataPoint) {
        this.dataPoint = dataPoint;
    }

    public UIDataType getType() {
        return type;
    }

    public void setType(UIDataType type) {
        this.type = type;
    }
}
