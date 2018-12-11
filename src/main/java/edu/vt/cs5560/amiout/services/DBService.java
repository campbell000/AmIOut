package edu.vt.cs5560.amiout.services;

import edu.vt.cs5560.amiout.domain.parsers.GeoPoint;
import edu.vt.cs5560.amiout.domain.parsers.OutageSample;
import edu.vt.cs5560.amiout.domain.ui.*;
import edu.vt.cs5560.amiout.services.datasource.climate.ClimateSample;

import java.sql.*;
import java.util.*;

/**
 * Created by Alex_Lappy_486 on 12/2/18.
 */
public class DBService
{
    public static final String AGG_QUERY =
            "WITH dumped AS (" +
                    " SELECT outages.id as dumped_outageid, (ST_Dump(areaaffected)).geom as dumped_outage_geo " +
                    " FROM outages where outages.daterange && '[START,END]'::daterange" +
                    "), partition_dumped as (\n" +
                    "\tSELECT __PART_partition.__ID as dumped_stateid, (ST_Dump(wkb_geometry)).geom as dumped_partition_geo  \n" +
                    "\tFROM __PART_partition\n" +
                    ")" +
            " select distinct(dumped_outageid) from dumped" +
            "   inner join partition_dumped on ST_Intersects(partition_dumped.dumped_partition_geo, dumped.dumped_outage_geo)"+
            "   WHERE dumped_stateid = ? " +
                    "group by dumped_outageid";

    public static final String PARTITION_QUERY =
      "WITH dumped AS (" +
           "\tSELECT outages.id as dumped_outageid, outages.nercregion, disturbancetype, energyloss, customersaffected, daterange, \n" +
           "\t(ST_Dump(areaaffected)).geom as dumped_outage_geo \n" +
           "\tFROM outages where outages.daterange && '[START,END]'::daterange\n" +
       ") select dumped_outageid, dumped.nercregion, dumped.disturbancetype, dumped.energyloss, dumped.customersaffected, dumped.daterange from dumped " +
                    "inner join __PART_partition on ST_Intersects(__PART_partition.wkb_geometry, dumped.dumped_outage_geo)   \n" +
                    "WHERE __PART_partition.__ID = ? ";

    public static final String WEATHER_QUERY = "SELECT avg(t_daily_avg) as temp, avg(cast(p_daily_calc as float)) as rain,  geostr, cluster FROM weather, (\n" +
            "    SELECT unnest(ST_ClusterWithin(geom, 0.1)) AS cluster, ST_AsText(ST_Centroid(unnest(ST_ClusterWithin(geom, 0.1)))) as geostr\n" +
            "    FROM weather where weather.lst_date >= 'START' and weather.lst_date <= 'END' and weather.t_daily_avg > -50 and cast(weather.p_daily_calc as float) > -1\n" +
            ") as weather_clustered\n" +
            "WHERE ST_Contains(ST_CollectionExtract(cluster, 1), geom) and weather.lst_date >= 'START' and weather.lst_date <= 'END' and weather.t_daily_avg > -50 and cast(weather.p_daily_calc as float) > -1\n" +
            "GROUP BY cluster, geostr";

    private Connection connection;

    Map<String, List<String>> partitionIDs = new HashMap<>();

    public DBService() throws SQLException, ClassNotFoundException {
        Connection c = null;
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/",
                        "Alex_Lappy_486", "Alex_Lappy_486");
        c.setAutoCommit(false);
        System.out.println("Opened database successfully");
        this.connection =  c;

        initPartitionIDs();
    }

    private void initPartitionIDs() throws SQLException {
        this.partitionIDs.put("states", new LinkedList<>());
        this.partitionIDs.put("counties", new LinkedList<>());
        this.partitionIDs.put("regions", new LinkedList<>());

        ResultSet set = this.connection.createStatement().executeQuery("SELECT GEO_ID FROM COUNTIES_PARTITION");
        while (set.next())
        {
            this.partitionIDs.get("counties").add(set.getString(1));
        }

         set = this.connection.createStatement().executeQuery("SELECT id FROM STATES_PARTITION");
        while (set.next())
        {
            this.partitionIDs.get("states").add(set.getString(1));
        }

        set = this.connection.createStatement().executeQuery("SELECT geoid FROM REGIONS_PARTITION");
        while (set.next())
        {
            this.partitionIDs.get("regions").add(set.getString(1));
        }
    }

    private String generateQuery(AggregateQuery query, String partitionID) throws SQLException {
        List<String> partitionIDs;
        String id_col = query.getPartitioning().equalsIgnoreCase("counties") ? "geo_id" : "id";
        if (query.getPartitioning().equalsIgnoreCase("regions"))
            id_col = "geoid";
        String queryWithNoValues = AGG_QUERY.replaceAll("__PART", query.getPartitioning());
        queryWithNoValues = queryWithNoValues.replaceAll("__ID", id_col);
        queryWithNoValues = queryWithNoValues.replaceAll("START", query.getStartDate());
        queryWithNoValues = queryWithNoValues.replaceAll("END", query.getEndDate());

        PreparedStatement preparedStatement = this.connection.prepareStatement(queryWithNoValues);
        preparedStatement.setString(1, partitionID);
        String finalQuery = preparedStatement.toString();
        System.out.println("PERFORMING: "+finalQuery);
        return finalQuery;

    }

    public List<ClimateSample> getWeatherData(AggregateQuery query) throws SQLException {
        List<ClimateSample> samples = new LinkedList<>();
        String queryWithNoValues = WEATHER_QUERY.replaceAll("START", query.getStartDate());
        queryWithNoValues = queryWithNoValues.replaceAll("END", query.getEndDate());
        System.out.println("WEATHER: "+queryWithNoValues);
        Statement s = this.connection.createStatement();
        ResultSet set = s.executeQuery(queryWithNoValues);
        int count = 0;
        while (set.next())
        {
            ClimateSample climateSample = new ClimateSample();
            String center = set.getString(3);
            String lon = center.split(" ")[0].replace("POINT(", "");
            String lat = center.split(" ")[1].replace(")", "");
            climateSample.setAvgTempCelsium(set.getDouble(1));
            climateSample.setPrecipitationInMM(set.getDouble(2));
            climateSample.setLat(Double.parseDouble(lat));
            climateSample.setLon(Double.parseDouble(lon));
            samples.add(climateSample);
        }
        return samples;

    }

    public AggregateQueryResponse doAggregateQuery(AggregateQuery query) throws SQLException {
        AggregateQueryResponse resp = new AggregateQueryResponse();
        for (String partitionID : this.partitionIDs.get(query.getPartitioning()))
        {
            Statement s = this.connection.createStatement();
            ResultSet set = s.executeQuery(generateQuery(query, partitionID));
            int count = 0;
            while (set.next())
                count++;
            resp.getPartitionIdToCountMap().put(partitionID, count);
        }

        if (query.isShowWeather())
            resp.setClimateSamples(getWeatherData(query));

        return resp;
    }

    public Collection<UIDataPoint> getOutagesInPartition(String partitionType, String partitionID, String startDate, String endDate) throws SQLException {
        String id_col = partitionType.equalsIgnoreCase("counties") ? "geo_id" : "id";
        if (partitionType.equalsIgnoreCase("regions"))
            id_col = "geoid";
        String queryWithNoValues = PARTITION_QUERY.replaceAll("__PART", partitionType);
        queryWithNoValues = queryWithNoValues.replaceAll("__ID", id_col);
        queryWithNoValues = queryWithNoValues.replaceAll("START", startDate);
        queryWithNoValues = queryWithNoValues.replaceAll("END", endDate);

        PreparedStatement preparedStatement = this.connection.prepareStatement(queryWithNoValues);
        preparedStatement.setString(1, partitionID);
        String finalQuery = preparedStatement.toString();
        System.out.println("PERFORMING: "+finalQuery);
        Statement s = this.connection.createStatement();
        ResultSet set = s.executeQuery(finalQuery);

        //TODO: Cant figure out how to return a distinct row per outage when  breaking up multipolygons into polygons.
        //TODO: For now, do it manually in code.
        int count = 0;
        Map<Integer,UIDataPoint> points = new HashMap<>();
        while (set.next())
        {
            int id = set.getInt(1);
            if (!points.containsKey(id))
            {
                Object nercregion = set.getObject(2);
                Object disturbanceType = set.getObject(3);
                Object energyLoss = set.getObject(4);
                Object customersAffected = set.getObject(5);
                Object dateRange = set.getObject(6);
                Map<String, Object> data = new HashMap<>();
                data.put("nercRegion", nercregion);
                data.put("disturbanceType", disturbanceType);
                data.put("energyLossInMegaWatts", energyLoss);
                data.put("numCustomersAffected", customersAffected);
                data.put("dateRange", dateRange.toString());
                points.put(id, new UIDataPoint(UIDataType.OUTAGE, data));
            }
        }

        // Once we have the points, get the centroids for each point
        String sql = "SELECT ID, ST_AsText(ST_Centroid(outages.areaaffected)) from outages where id = ANY ('{"+getIdString(points.keySet())+"}'::int[])";
        s = this.connection.createStatement();
        set = s.executeQuery(sql);
        while (set.next())
        {
            int id = set.getInt(1);
            String center = set.getString(2);
            String lon = center.split(" ")[0].replace("POINT(", "");
            String lat = center.split(" ")[1].replace(")", "");

            Map<String, Object> outage = (Map) points.get(id).getDataPoint();
            outage.put("centerPoint", new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon)));
        }
        return points.values();


    }

    private String getIdString(Collection<Integer> points)
    {
        StringJoiner joiner = new StringJoiner(",");
        for (Integer p : points)
        {
            joiner.add(p.toString());
        }
        return joiner.toString();
    }
}
