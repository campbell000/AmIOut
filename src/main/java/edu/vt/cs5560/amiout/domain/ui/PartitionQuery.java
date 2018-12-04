package edu.vt.cs5560.amiout.domain.ui;

public class PartitionQuery {
    private String partitionID;
    private String partitioning;
    private String startDate;
    private String endDate;

    public String getPartitionID() {
        return partitionID;
    }

    public void setPartitionID(String partitionID) {
        this.partitionID = partitionID;
    }

    public String getPartitioning() {
        return partitioning;
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

    public void setPartitioning(String partitioning) {
        this.partitioning = partitioning;
    }
}
