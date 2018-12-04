package edu.vt.cs5560.amiout.domain.ui;

public class AggregateQuery
{
    private String startDate;
    private String endDate;
    private String partitioning;
    private boolean showOutages;
    private boolean showTweets;
    private boolean showWeather;
    public boolean timeLapse;
    public int timeLapseIntervalLengthSeconds;
    public int numSteps;

    public AggregateQuery(){}

    public AggregateQuery(AggregateQuery query, String startDate, String endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.partitioning = query.partitioning;
        this.showOutages = query.showOutages;
        this.showTweets = query.showTweets;
        this.showWeather = query.showWeather;
        this.timeLapse = query.timeLapse;
    }

    public boolean isTimeLapse() {
        return timeLapse;
    }

    public void setTimeLapse(boolean timeLapse) {
        timeLapse = timeLapse;
    }

    public int getTimeLapseIntervalLengthSeconds() {
        return timeLapseIntervalLengthSeconds;
    }

    public void setTimeLapseIntervalLengthSeconds(int timeLapseIntervalLengthSeconds) {
        this.timeLapseIntervalLengthSeconds = timeLapseIntervalLengthSeconds;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }

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
