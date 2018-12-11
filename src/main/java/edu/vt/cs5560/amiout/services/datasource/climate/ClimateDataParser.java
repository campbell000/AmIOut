package edu.vt.cs5560.amiout.services.datasource.climate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * This class unzips the climatedata.zip file in src/main/resources/static-datasets and parses all of the data
 * into java objects.
 */
public class ClimateDataParser
{
    private static final int DATE_COL = 1;
    private static final int LON_COL= 3;
    private static final int LAT_COL = 4;
    private static final int MAX_TEMP_COL = 5;
    private static final int MIN_TEMP_COL = 6;
    private static final int AVG_TEMP_COL = 8;
    private static final int RAINFALL_COL = 9;
    private boolean onlyParse2018 = true;

    public static void main(String[]args) throws Exception {
        ClimateFileLoader loader = new ClimateFileLoader();
        List<File> climateFiles = loader.retrieveFiles();
        List<ClimateSample> samples = new LinkedList<>();
        SimpleDateFormat format = new SimpleDateFormat(ClimateSample.DATE_FORMAT);
        int samplesBad = 0;

        for (File file : climateFiles)
        {
            if (file.getName().contains("2018"))
            {
                Scanner scan = new Scanner(file);
                while (scan.hasNextLine())
                {
                    String tokens[] = scan.nextLine().split("\\s+");
                    ClimateSample sample = new ClimateSample();
                    sample.setAvgTempCelsium(parseDouble(tokens, AVG_TEMP_COL));
                    sample.setMinTempCelsius(parseDouble(tokens, MIN_TEMP_COL));
                    sample.setMaxTempCelsius(parseDouble(tokens, MAX_TEMP_COL));
                    sample.setLon(parseDouble(tokens, LON_COL));
                    sample.setLat(parseDouble(tokens, LAT_COL));
                    sample.setPrecipitationInMM(parseDouble(tokens, RAINFALL_COL));
                    sample.setDate(format.parse(tokens[DATE_COL]));
                    samples.add(sample);
                    if (sample.getMaxTempCelsius() == ClimateSample.UNKNOWN_VALUE)
                        samplesBad++;

                }
                scan.close();
            }
        }

        // Delete the unzipped files so that intelliJ doesn't have to keep copying them to the build directory
        // whenever we run/build.
        FileUtils.cleanDirectory(new File("src/main/resources/static-datasets/climate/unzippedFiles"));
    }

    private static double parseDouble(String[] strs, int col)
    {
        String str = strs[col];
        boolean isInvalidValue = StringUtils.isBlank(str) || str.equals("-9999.0")
                || str.equals("-99.000") || str.contains("999");

        if (isInvalidValue)
            return ClimateSample.UNKNOWN_VALUE;
        else
            return Double.parseDouble(str);
    }
}
