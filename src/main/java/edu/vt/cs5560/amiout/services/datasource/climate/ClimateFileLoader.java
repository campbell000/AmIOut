package edu.vt.cs5560.amiout.services.datasource.climate;

import edu.vt.cs5560.amiout.services.datasource.DataFileSource;
import edu.vt.cs5560.amiout.utils.io.ClasspathLoader;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Gets all climate data files. Data comes from ftp://ftp.ncdc.noaa.gov/pub/data/uscrn/products/daily01
 */
public class ClimateFileLoader implements DataFileSource
{
    private static final String TARGET_DIR = "src/main/resources/static-datasets/climate/unzippedFiles";
    @Override
    public List<File> retrieveFiles() throws Exception
    {
        // Create the dir that will hold the extracted files
        File targetDir = new File(TARGET_DIR);
        targetDir.mkdirs();

        // Get the path of the climate zip file that should be residing in src/main/resources
        String pathOfClimateDir = ClasspathLoader.getFullPathOfFile("climate");
        ZipFile zipFile = new ZipFile(pathOfClimateDir+File.separator+"climatedata.zip");

        // Write each file to disk (resources dir)
        List<File> files = new LinkedList<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            File copiedFile = new File(targetDir, Paths.get(entry.getName()).getFileName().toString());
            copiedFile.createNewFile();
            files.add(copiedFile);
            String content = IOUtils.toString(zipFile.getInputStream(entry));
            FileWriter writer = new FileWriter(copiedFile);
            writer.write(content);
            writer.close();
        }
        return files;
    }
}
