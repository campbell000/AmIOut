package edu.vt.cs5560.amiout.domain.ui;

import java.util.HashMap;
import java.util.Map;

public class AggregateQueryResponse
{
    private Map<String, Integer> partitionIdToCountMap;
    private String partitionType;

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
}
