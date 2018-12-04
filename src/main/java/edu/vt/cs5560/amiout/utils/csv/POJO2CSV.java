package edu.vt.cs5560.amiout.utils.csv;

import edu.vt.cs5560.amiout.domain.parsers.OutageSample;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class POJO2CSV {
    public static void writeToCSV(List<OutageSample> samples) throws IOException {
        CellProcessor[] processors = new CellProcessor[] {
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
        };

        ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter("out.csv"),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"id", "source", "dateTimeOcurred", "nercRegion", "areaAffected", "disturbanceType", "energyLossInMegaWatts",
                "numCustomersAffected", "restorationDateTime"};
        beanWriter.writeHeader(header);

        for (OutageSample outage : samples) {
            beanWriter.write(outage, header, processors);
        }
        beanWriter.close();
    }
}
