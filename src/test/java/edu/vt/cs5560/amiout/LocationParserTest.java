package edu.vt.cs5560.amiout;

import edu.vt.cs5560.amiout.services.datasource.doe.DOEOutageParser;
import org.junit.Test;

import java.util.List;

/**
 * Created by Alex_Lappy_486 on 11/29/18.
 */
public class LocationParserTest
{
    @Test
    public void testLocationParser() {
        List<String> locs = DOEOutageParser.getLocations("North Carolina: South Carolina:");
        locs = DOEOutageParser.getLocations("North Carolina:");
        locs = DOEOutageParser.getLocations("Mississippi: Forrest County;");
        locs = DOEOutageParser.getLocations("Missouri: Jackson County, Clay County, Platte County, Andrew County; Kansas: Johnson County;");
        locs = DOEOutageParser.getLocations("Arkansas: Mississippi: Louisiana: Texas:");
        System.out.println("ASD");
    }
}
