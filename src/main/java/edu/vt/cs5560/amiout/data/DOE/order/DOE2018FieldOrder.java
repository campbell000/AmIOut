package edu.vt.cs5560.amiout.data.DOE.order;

import edu.vt.cs5560.amiout.data.DOE.DOEField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DOE2018FieldOrder implements DOEFieldOrder {
    @Override
    public int getDateOccurredCol() {
        return 1;
    }

    @Override
    public int getTimeOccurredCol() {
        return 2;
    }

    @Override
    public int getDateRestoredCol() {
        return 3;
    }

    @Override
    public int getTimeRestoredCol() {
        return 4;
    }

    @Override
    public int getAreaAffectedCol() {
        return 5;
    }

    @Override
    public int getNercRegionCol() {
        return 6;
    }

    @Override
    public int getDescCol() {
        return 7;
    }

    @Override
    public int getEventTypeCol() {
        return 8;
    }

    @Override
    public int getLossCol() {
        return 9;
    }

    @Override
    public int getNumAffectedCol() {
        return 10;
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    public DateFormat getTimeFormat()
    {
        return new SimpleDateFormat("h:mm a", Locale.US);
    }
}
