package edu.vt.cs5560.amiout.domain.ui;

import java.util.LinkedList;
import java.util.List;

public class TimeLapseResponse
{
    List<AggregateQueryResponse> timeLapseSteps;

    public TimeLapseResponse()
    {
        this.timeLapseSteps = new LinkedList<>();
    }

    public List<AggregateQueryResponse> getTimeLapseSteps() {
        return timeLapseSteps;
    }

    public void setTimeLapseSteps(List<AggregateQueryResponse> timeLapseSteps) {
        this.timeLapseSteps = timeLapseSteps;
    }
}
