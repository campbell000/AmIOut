package edu.vt.cs5560.amiout.data.DOE;

import edu.vt.cs5560.amiout.data.DOE.order.*;
import edu.vt.cs5560.amiout.domain.NERCRegion;
import edu.vt.cs5560.amiout.domain.OutageSample;
import edu.vt.cs5560.amiout.services.datasource.DOEOutageFileScraper;
import edu.vt.cs5560.amiout.services.datasource.DOEOutageStaticLoader;
import edu.vt.cs5560.amiout.services.datasource.DataFileSource;
import edu.vt.cs5560.amiout.utils.LossyParser;
import edu.vt.cs5560.amiout.utils.csv.POJO2CSV;
import edu.vt.cs5560.amiout.utils.http.HtmlDownloader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DOEOutageParser
{
   // DataFileSource doeSource = new DOEOutageFileScraper(); load files statically so we don't take down the government site....
    private static final DataFileSource doeSource = new DOEOutageStaticLoader();

    public static void main(String [] args) throws Exception
    {
        // Get all of the XLS files available for download
        List<File> excelFiles = doeSource.retrieveFiles();
        Collections.reverse(excelFiles);

        // Parse each excel file
        List<OutageSample> outages = parseOutagesFromExcelFiles(excelFiles);
        POJO2CSV.writeToCSV(outages);
    }



    private static List<OutageSample> parseOutagesFromExcelFiles(List<File> files) throws IOException, InvalidFormatException, ParseException
    {
        List<OutageSample> samples = new LinkedList<>();
        for (File f : files)
        {
            Workbook workbook = WorkbookFactory.create(f);
            System.out.println("Retrieving Sheets using for-each loop");
            for(Sheet sheet: workbook) {
                int rowNum = 0;
                for (Row row: sheet)
                {
                    OutageSample outage = getOutageFromRowVals(f.getName(), row, rowNum);
                    if (outage != null && outage.sampleIsUsable())
                        samples.add(outage);

                    rowNum++;
                }
            }
        }
        return samples;
    }

    private static OutageSample getOutageFromRowVals(String fileSource, Row row, int rowNum) throws ParseException {
        DOEFieldOrder fieldParser = getRowParser(row);
        OutageSample outage = null;
        String[] vals = getValues(row);
        try {
            if (rowContainsValues(vals)) {
                outage = new OutageSample();
                outage.setId(rowNum);
                outage.setSource(fileSource);
                outage.setAreaAffected(LossyParser.getVal(row, fieldParser.getAreaAffectedCol()));
                outage.setDisturbanceType(LossyParser.getVal(row, fieldParser.getEventTypeCol()));
                outage.setEnergyLossInMegaWatts(LossyParser.getInt(row, fieldParser.getLossCol()));
                outage.setNercRegion(LossyParser.getNerc(row, fieldParser.getNercRegionCol()));
                outage.setNumCustomersAffected(LossyParser.getInt(row, fieldParser.getNumAffectedCol()));

                String dateOcurredString = LossyParser.getVal(row, fieldParser.getDateOccurredCol());
                String timeOcurredString = LossyParser.getVal(row, fieldParser.getTimeOccurredCol());
                outage.setDateTimeOcurred(fieldParser.getDateFormat().parse(dateOcurredString+" "+timeOcurredString));

                String dateRestoredStr = LossyParser.getVal(row, fieldParser.getDateRestoredCol());
                String timeRestoredStr = LossyParser.getVal(row, fieldParser.getTimeRestoredCol());
                try {
                    outage.setRestorationDateTime(fieldParser.getDateFormat().parse(dateRestoredStr+" "+timeRestoredStr));
                }
                catch(Exception e){}


                doDOEFieldOrderSpecificProcessing(fieldParser, outage, vals);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return outage;
    }

    private static void doDOEFieldOrderSpecificProcessing(DOEFieldOrder fieldOrder, OutageSample outage, String[] vals)
    {
        if (fieldOrder instanceof DOE2002FieldOrder)
        {
            DOE2002FieldOrder o = (DOE2002FieldOrder) fieldOrder;
            String[] tokens = vals[fieldOrder.getDateRestoredCol()].split(" ");
            if (tokens.length == 1) {
                try {
                    Date d = o.getAltDateFormat2().parse(tokens[0]);
                    d.setYear(outage.getDateTimeOcurred().getYear());
                    outage.setRestorationDateTime(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (tokens.length == 2)
            {
                try {
                    String v = vals[fieldOrder.getDateRestoredCol()];
                    Date d = o.getAltDateFormat1().parse(v);
                    outage.setRestorationDateTime(d);
                }
                catch(Exception e) {}
            }
            else if (tokens.length == 3)
            {
                tokens = vals[fieldOrder.getDateRestoredCol()].split("[,\\s]+");
                try {
                    // Convert noon to "pm" and remove periods in "a.m."
                    if (tokens[2].contains("noon"))
                        tokens[2] = "pm";

                    tokens[2] = tokens[2].trim().replaceAll("\\.", "");
                    Date d = o.get2003RestoredDateFormat().parse(tokens[0]+" "+tokens[1]+" "+tokens[2]);
                    outage.setRestorationDateTime(d);
                }
                catch(Exception e) {}
            }
        }
    }

    private static String[] getValues(Row row)
    {
        String [] vals = new String[getNumValues(row)];
        int i = 0;
        for (Cell cell : row)
        {
            vals[i] = LossyParser.dataFormatter.formatCellValue(cell);
            i++;
        }
        return vals;
    }



    private static DOEFieldOrder getRowParser(Row row)
    {
        String[] vals = getValues(row);
        for (String val : vals)
        {
            // Try 2018
            DOEFieldOrder fieldOrder = new DOE2018FieldOrder();
            Date date = tryToParseDate(fieldOrder, val);
            if (date != null && dateHasYear(date, 118, 117, 116, 115))
                return fieldOrder;
            else if (date != null && dateHasYear(date, 115, 114, 113, 112, 111))
                return new DOE2012FieldOrder();

            // Try 2002
            fieldOrder = new DOE2002FieldOrder();
            date = tryToParseDate(fieldOrder, val);
            if (date != null && dateHasYear(date, 102, 103, 104, 105, 106, 107, 108, 109, 110))
                return fieldOrder;
            else if (date != null && dateHasYear(date, 111, 112, 113, 114))
                return new DOE2011FieldOrder();
            else{
                Date d = tryToParseDate(new DOE2015FieldOrder(), val);
                if (d != null && dateHasYear(d,  115))
                    return new DOE2015FieldOrder();
            }
        }
        System.err.print("couldn't find parser!");
        return null;
    }

    private static boolean dateHasYear(Date date, int...years)
    {
        for (int year : years)
        {
            if (date.getYear() == year)
                return true;
        }
        return false;
    }

    private static Date tryToParseDate(DOEFieldOrder order, String val)
    {
        try {
            return order.dateFormatForFileDetection().parse(val);
        }
        catch(Exception e){}
        return null;
    }

    private static boolean rowContainsValues(String[] row)
    {
        int notBlank = 0;
        for (String s : row) {
            if (StringUtils.isNotBlank(s))
                notBlank++;
        }
        return notBlank > 5;

    }

    private static int getNumValues(Row row)
    {
        int count = 0;
        for (Cell cell : row)
        {
            count++;
        }
        return count;
    }

}
