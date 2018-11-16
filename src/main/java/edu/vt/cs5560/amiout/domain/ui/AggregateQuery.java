package edu.vt.cs5560.amiout.domain.ui;

public class AggregateQuery
{
    private String startDate;
    private String endDate;
    private String partitioning;
    private boolean showOutages;
    private boolean showTweets;
    private boolean showWeather;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(String partitioning) {
        this.partitioning = partitioning;
    }

    public boolean isShowOutages() {
        return showOutages;
    }

    public void setShowOutages(boolean showOutages) {
        this.showOutages = showOutages;
    }

    public boolean isShowTweets() {
        return showTweets;
    }

    public void setShowTweets(boolean showTweets) {
        this.showTweets = showTweets;
    }

    public boolean isShowWeather() {
        return showWeather;
    }

    public void setShowWeather(boolean showWeather) {
        this.showWeather = showWeather;
    }
}
