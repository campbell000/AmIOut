package edu.vt.cs5560.amiout.controllers;

import edu.vt.cs5560.amiout.domain.parsers.GeoPoint;
import edu.vt.cs5560.amiout.domain.parsers.OutageSample;
import edu.vt.cs5560.amiout.domain.ui.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController
{
    @RequestMapping("/")
    public String loadIndexPage()
    {
        return "index";
    }

    @RequestMapping("/aggregateQuery")
    public @ResponseBody AggregateQueryResponse aggregateQuery(@RequestBody AggregateQuery aggregateQuery)
    {
        AggregateQueryResponse resp = new AggregateQueryResponse();
        resp.setPartitionType(aggregateQuery.getPartitioning());
        resp.getPartitionIdToCountMap().put("4", 50);
        resp.getPartitionIdToCountMap().put("1", 100);
        resp.getPartitionIdToCountMap().put("2", 150);
        resp.getPartitionIdToCountMap().put("3", 200);
        return resp;
    }

    @RequestMapping("/partitionQuery")
    public @ResponseBody PartitionQueryResponse aggregateQuery(@RequestBody PartitionQuery partitionQuery)
    {
        PartitionQueryResponse resp = new PartitionQueryResponse();
        resp.setDataPoints(generateRandomPoints());
        return resp;
    }

    private List<UIDataPoint> generateRandomPoints()
    {
        List<UIDataPoint> points = new LinkedList<>();
        for (int i = 0; i < 50; i++)
        {
            double randomLat = 39 + (44 - 39) * new Random().nextDouble();
            double randomLon = -101 + (-87 - -101) * new Random().nextDouble();

            OutageSample sample = new OutageSample();
            sample.setRestorationDateTime(new Date());
            sample.setAreaAffected("Falls Church, Virginia");
            sample.setDateTimeOcurred(new Date());
            sample.setDisturbanceType("Weather");
            sample.setEnergyLossInMegaWatts(50000);
            sample.setNumCustomersAffected(50000);
            sample.setCenterPoint(new GeoPoint(randomLat, randomLon));
            points.add(new UIDataPoint(UIDataType.OUTAGE, sample));
        }
        return points;
    }
}
