package edu.vt.cs5560.amiout.services.datasource.doe;

import edu.vt.cs5560.amiout.services.datasource.DataFileSource;
import edu.vt.cs5560.amiout.utils.http.HtmlDownloader;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DOEOutageFileScraper implements DataFileSource
{
    public static final String SITE_ROOT = "https://www.oe.netl.doe.gov/";
    private static final boolean ONLY_GRAB_ONE = false;
    private static final int OFFSET = 0;
    private static int delay = 10;

    @Override
    public List<File> retrieveFiles() throws Exception
    {
        // Get HTML for outage data page, iterate over table
        List<File> excelFiles = new LinkedList<>();
        String doeOutageHtml = HtmlDownloader.downloadHtml(SITE_ROOT+"OE417_annual_summary.aspx");
        Document doc = Jsoup.parse(doeOutageHtml);
        Elements tableRows = doc.select("#ctl00_MainContent_GridView1 tr a");
        ListIterator<Element> iterator = tableRows.listIterator();
        int fileCount = 0;

        // For each excel link, download the file
        while (iterator.hasNext())
        {
            Element e = iterator.next();
            if (e.text().equals("XLS"))
            {
                fileCount++;
                if (fileCount > OFFSET)
                {
                    URL u = new URL(SITE_ROOT+e.attr("href"));
                    String filename = e.attr("href").substring(e.attr("href").indexOf("=") + 1);
                    filename = filename.substring(0, filename.indexOf("="));
                    File file = File.createTempFile(filename, null);
                    FileUtils.copyURLToFile(u, file);
                    excelFiles.add(file);
                    if (ONLY_GRAB_ONE)
                        break;

                    Thread.sleep(delay * 1000);
                }

            }
        }
        return excelFiles;
    }
}
