package edu.vt.cs5560.amiout.controllers;

import edu.vt.cs5560.amiout.domain.parsers.GeoPoint;
import edu.vt.cs5560.amiout.domain.parsers.OutageSample;
import edu.vt.cs5560.amiout.domain.ui.*;
import edu.vt.cs5560.amiout.services.DBService;
import edu.vt.cs5560.amiout.services.datasource.climate.ClimateSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class IndexController
{
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    DBService dbService;

    @RequestMapping("/")
    public String loadIndexPage()
    {
        return "index";
    }

    @RequestMapping("/aggregateQuery")
    public @ResponseBody AggregateQueryResponse aggregateQuery(@RequestBody AggregateQuery aggregateQuery) throws ParseException, SQLException {
        return doAggregateQuery(aggregateQuery);
    }

    @RequestMapping("/timeLapse")
    public @ResponseBody TimeLapseResponse timeLapseQuery(@RequestBody AggregateQuery aggregateQuery) throws ParseException, SQLException {
        TimeLapseResponse response = new TimeLapseResponse();
        Date startDate = this.format.parse(aggregateQuery.getStartDate());
        Date endDate = this.format.parse(aggregateQuery.getEndDate());

        long stepLength = Math.abs(startDate.getTime() - endDate.getTime()) / new Long(aggregateQuery.getNumSteps());
        Date currStatDate = startDate;
        for (int i = 0; i < aggregateQuery.getNumSteps(); i++)
        {
            double currEndDateTime = currStatDate.getTime()+stepLength;
            Date currEndDate = new Date((long)currEndDateTime);

            // TODO: do query for this particular date range
            AggregateQuery stepAggregateQuery = new AggregateQuery(aggregateQuery, format.format(currStatDate),
                    format.format(currEndDate));
            response.getTimeLapseSteps().add(doAggregateQuery(stepAggregateQuery));
            currStatDate = currEndDate;
        }
        return response;
    }

    public AggregateQueryResponse doAggregateQuery(AggregateQuery query) throws SQLException {
        return dbService.doAggregateQuery(query);
    }

    @RequestMapping("/partitionQuery")
    public @ResponseBody PartitionQueryResponse aggregateQuery(@RequestBody PartitionQuery partitionQuery) throws SQLException {
        PartitionQueryResponse resp = new PartitionQueryResponse();
        resp.setDataPoints(getPointsInPartition(partitionQuery.getPartitioning(), partitionQuery.getPartitionID(),
                partitionQuery.getStartDate(), partitionQuery.getEndDate()));
        return resp;
    }

    private Collection<UIDataPoint> getPointsInPartition(String partitionType, String partitionID, String startDate, String endDate) throws SQLException {
        List<UIDataPoint> points = new LinkedList<>();
        return dbService.getOutagesInPartition(partitionType, partitionID, startDate, endDate);
    }

    double[][] dummyWeatherCoords = {{32.806671, -86.79113},
        {61.370716, -152.404419},
        {33.729759, -111.431221},
        {34.969704, -92.373123},
        {36.116203, -119.681564},
        {39.059811, -105.311104},
        {41.597782, -72.755371},
        {39.318523, -75.507141},
        {38.897438, -77.026817},
        {27.766279, -81.686783},
        {33.040619, -83.643074},
        {21.094318, -157.498337},
        {44.240459, -114.478828},
        {40.349457, -88.986137},
        {39.849426, -86.258278},
        {42.011539, -93.210526},
        {38.5266, -96.726486},
        {37.66814, -84.670067},
        {31.169546, -91.867805},
        {44.693947, -69.381927},
        {39.063946, -76.802101},
        {42.230171, -71.530106},
        {43.326618, -84.536095},
        {45.694454, -93.900192},
        {32.741646, -89.678696},
        {38.456085, -92.288368},
        {46.921925, -110.454353},
        {41.12537, -98.268082},
        {38.313515, -117.055374},
        {43.452492, -71.563896},
        {40.298904, -74.521011},
        {34.840515, -106.248482},
        {42.165726, -74.948051},
        {35.630066, -79.806419},
        {47.528912, -99.784012},
        {40.388783, -82.764915},
        {35.565342, -96.928917},
        {44.572021, -122.070938},
        {40.590752, -77.209755},
        {41.680893, -71.51178},
        {33.856892, -80.945007},
        {44.299782, -99.438828},
        {35.747845, -86.692345},
        {31.054487, -97.563461},
        {40.150032, -111.862434},
        {44.045876, -72.710686},
        {37.769337, -78.169968},
        {47.400902, -121.490494},
        {38.491226, -80.954453},
        {44.268543, -89.616508},
    {42.755966	-107.30249}};

}
