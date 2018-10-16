package edu.vt.cs5560.amiout.data.DOE.order;

import org.assertj.core.util.Lists;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DOEFieldOrder
{
    public int getDateOccurredCol();

    public int getTimeOccurredCol();

    public int getDateRestoredCol();

    public int getTimeRestoredCol();

    public int getAreaAffectedCol();

    public int getNercRegionCol();

    public int getDescCol();

    public int getEventTypeCol();

    public int getLossCol();

    public int getNumAffectedCol();

    public DateFormat getDateFormat();

    public DateFormat dateFormatForFileDetection();
}
