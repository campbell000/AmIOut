package edu.vt.cs5560.amiout.data.DOE.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DOE2002FieldOrder implements DOEFieldOrder {
    @Override
    public int getDateOccurredCol() {
        return 0;
    }

    @Override
    public int getTimeOccurredCol() {
        return 2;
    }

    @Override
    public int getDateRestoredCol() {
        return 7;
    }

    @Override
    public int getTimeRestoredCol() {
        return 7;
    }

    @Override
    public int getAreaAffectedCol() {
        return 3;
    }

    @Override
    public int getNercRegionCol() {
        return 1;
    }

    @Override
    public int getDescCol() {
        return -1;
    }

    @Override
    public int getEventTypeCol() {
        return 4;
    }

    @Override
    public int getLossCol() {
        return 5;
    }

    @Override
    public int getNumAffectedCol() {
        return 6;
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("MM/dd/yy h:mm");
    }

    public DateFormat getTimeFormat()
    {
        return new SimpleDateFormat("h:mm", Locale.US);
    }

    public DateFormat getAltDateFormat1() { return new SimpleDateFormat("MM/dd/yy hh:mm");}

    public DateFormat getAltDateFormat2() { return new SimpleDateFormat("dd-MMM");}

    public DateFormat get2003RestoredDateFormat() { return new SimpleDateFormat("MM/dd/yy h:mm a", Locale.US );}

    @Override
    public DateFormat dateFormatForFileDetection() {
        return new SimpleDateFormat("MM/dd/yy");
    }

}
