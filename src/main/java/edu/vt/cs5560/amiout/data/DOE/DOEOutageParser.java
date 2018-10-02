package edu.vt.cs5560.amiout.data.DOE;

import edu.vt.cs5560.amiout.data.DOE.order.DOE2018FieldOrder;
import edu.vt.cs5560.amiout.data.DOE.order.DOEFieldOrder;
import edu.vt.cs5560.amiout.domain.NERCRegion;
import edu.vt.cs5560.amiout.domain.OutageSample;
import edu.vt.cs5560.amiout.utils.LossyParser;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DOEOutageParser
{

    public static final String SITE_ROOT = "https://www.oe.netl.doe.gov/";
    private static final boolean ONLY_GRAB_ONE = true;


    public static void main(String [] args) throws Exception
    {
        // Get all of the XLS files available for download
        List<File> excelFiles = getExcelFilesForOutageData();

        // Parse each excel file
        List<OutageSample> outages = parseOutagesFromExcelFiles(excelFiles);
    }

    private static List<File> getExcelFilesForOutageData() throws Exception
    {
        // Get HTML for outage data page, iterate over table
        List<File> excelFiles = new LinkedList<>();
        String doeOutageHtml = HtmlDownloader.downloadHtml(SITE_ROOT+"OE417_annual_summary.aspx");
        Document doc = Jsoup.parse(doeOutageHtml);
        Elements tableRows = doc.select("#ctl00_MainContent_GridView1 tr a");
        ListIterator<Element> iterator = tableRows.listIterator();

        // For each excel link, download the file
        while (iterator.hasNext())
        {
            Element e = iterator.next();
            if (e.text().equals("XLS"))
            {
                File file = File.createTempFile("aaa", null);
                URL u = new URL(SITE_ROOT+e.attr("href"));
                FileUtils.copyURLToFile(u, file);
                excelFiles.add(file);
                if (ONLY_GRAB_ONE)
                    break;
            }
        }
        return excelFiles;
    }

    private static List<OutageSample> parseOutagesFromExcelFiles(List<File> files) throws IOException, InvalidFormatException, ParseException
    {
        List<OutageSample> samples = new LinkedList<>();
        for (File f : files)
        {
            Workbook workbook = WorkbookFactory.create(f);
            System.out.println("Retrieving Sheets using for-each loop");
            for(Sheet sheet: workbook) {
                for (Row row: sheet)
                {
                    OutageSample outage = getOutageFromRowVals(row);
                    if (outage != null && outage.sampleIsUsable())
                        samples.add(outage);
                }
            }
        }
        return samples;
    }

    private static OutageSample getOutageFromRowVals(Row row) throws ParseException {
        DOEFieldOrder fieldParser = getRowParser(row);
        OutageSample outage = null;
        String[] vals = getValues(row);
        try {
            if (rowContainsValues(vals)) {
                outage = new OutageSample();
                outage.setAreaAffected(LossyParser.getVal(row, fieldParser.getAreaAffectedCol()));
                outage.setDate(LossyParser.getDate(row, fieldParser.getDateOccurredCol(), fieldParser.getDateFormat()));
                outage.setDisturbanceType(LossyParser.getVal(row, fieldParser.getEventTypeCol()));
                outage.setEnergyLossInMegaWatts(LossyParser.getInt(row, fieldParser.getLossCol()));
                outage.setNercRegion(LossyParser.getNerc(row, fieldParser.getNercRegionCol()));
                outage.setNumCustomersAffected(LossyParser.getInt(row, fieldParser.getNumAffectedCol()));
                outage.setRestorationDate(LossyParser.getDate(row, fieldParser.getDateRestoredCol(), fieldParser.getDateFormat()));
                outage.setRestorationTime(LossyParser.getTime(row, fieldParser.getTimeRestoredCol(), fieldParser.getTimeFormat()));
                outage.setTime(LossyParser.getTime(row, fieldParser.getTimeOccurredCol(), fieldParser.getTimeFormat()));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return outage;
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
        return new DOE2018FieldOrder();
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
