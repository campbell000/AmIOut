package edu.vt.cs5560.amiout.data.DOE.order;

import edu.vt.cs5560.amiout.data.DOE.DOEField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DOE2012FieldOrder implements DOEFieldOrder {
    @Override
    public int getDateOccurredCol() {
        return 0;
    }

    @Override
    public int getTimeOccurredCol() {
        return 1;
    }

    @Override
    public int getDateRestoredCol() {
        return 2;
    }

    @Override
    public int getTimeRestoredCol() {
        return 3;
    }

    @Override
    public int getAreaAffectedCol() {
        return 4;
    }

    @Override
    public int getNercRegionCol() {
        return 5;
    }

    @Override
    public int getDescCol() {
        return -1;
    }

    @Override
    public int getEventTypeCol() {
        return 6;
    }

    @Override
    public int getLossCol() {
        return 7;
    }

    @Override
    public int getNumAffectedCol() {
        return 8;
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
    }

    @Override
    public DateFormat dateFormatForFileDetection() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    public DateFormat getTimeFormat()
    {
        return new SimpleDateFormat("h:mm a", Locale.US);
    }
}
