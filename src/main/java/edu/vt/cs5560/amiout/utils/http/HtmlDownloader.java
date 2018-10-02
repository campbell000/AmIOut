package edu.vt.cs5560.amiout.utils.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HtmlDownloader
{
    public static String downloadHtml(String page) throws Exception
    {
        URL url = new URL(page);
        InputStream is = url.openStream();  // throws an IOException
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        is.close();
        return builder.toString();
    }
}
