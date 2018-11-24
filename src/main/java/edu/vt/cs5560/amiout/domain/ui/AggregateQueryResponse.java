package edu.vt.cs5560.amiout.domain.ui;

import edu.vt.cs5560.amiout.services.datasource.climate.ClimateSample;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateQueryResponse
{
    private Map<String, Integer> partitionIdToCountMap;
    private List<ClimateSample> climateSamples;
    private String partitionType;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Date startDate;
    private Date endDate;

    public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    public AggregateQueryResponse()
    {
        this.partitionIdToCountMap = new HashMap<>();
    }

    public Map<String, Integer> getPartitionIdToCountMap() {
        return partitionIdToCountMap;
    }

    public void setPartitionIdToCountMap(Map<String, Integer> partitionIdToCountMap) {
        this.partitionIdToCountMap = partitionIdToCountMap;
    }

    public List<ClimateSample> getClimateSamples() {
        return climateSamples;
    }

    public void setClimateSamples(List<ClimateSample> climateSamples) {
        this.climateSamples = climateSamples;
    }
}
