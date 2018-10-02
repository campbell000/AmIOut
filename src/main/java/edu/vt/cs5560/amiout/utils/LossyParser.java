package edu.vt.cs5560.amiout.utils;

import edu.vt.cs5560.amiout.domain.NERCRegion;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LossyParser
{
    public static DataFormatter dataFormatter = new DataFormatter();

    public static String getVal(Row row, int cellNum)
    {
        return dataFormatter.formatCellValue(row.getCell(cellNum));
    }

    public static Time getTime(Row row, int col, DateFormat timeFormat)
    {
        try
        {
            String s = getVal(row, col);
            Date d = timeFormat.parse(s);
            SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss");
            return Time.valueOf(f.format(d));

        }
        catch(Exception e)
        {
            System.err.println("Error parsing Time from "+getVal(row, col));
            return null;
        }
    }

    public static Date getDate(Row row, int col, DateFormat format)
    {
        try
        {
            return format.parse(getVal(row, col));
        }
        catch(Exception e)
        {
            System.err.println("Error parsing Date from "+getVal(row, col));
            return null;
        }
    }

    public static Integer getInt(Row row, int col)
    {
        String s = getVal(row, col);
        try
        {
            return Integer.parseInt(s);
        }
        catch(Exception e)
        {
            System.err.println("Error parsing int from "+s);
            return null;
        }
    }

    public static NERCRegion getNerc(Row row, int col)
    {
        String s = getVal(row, col);
        try
        {
            return NERCRegion.valueOf(s);
        }
        catch(Exception e)
        {
            System.err.println("Error parsing Nerc from "+s);
            return null;
        }
    }
}
