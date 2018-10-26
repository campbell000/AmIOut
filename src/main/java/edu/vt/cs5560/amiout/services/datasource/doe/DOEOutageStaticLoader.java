package edu.vt.cs5560.amiout.services.datasource.doe;

import edu.vt.cs5560.amiout.services.datasource.DataFileSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DOEOutageStaticLoader implements DataFileSource
{
    public List<File> retrieveFiles() throws Exception {
        File resourceDir = new File(getDoeStaticDataFileDir());
        return Arrays.asList(resourceDir.listFiles());

    }

    public static String getDoeStaticDataFileDir(){
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for(final String element : classPathElements){
            if (element.contains("AmIOut\\out\\production\\resources"))
            {
                File resourceDir = new File(element);
                for (File resourceFile : resourceDir.listFiles())
                {
                    if (resourceFile.getName().contains("static-datasets"))
                    {
                        for (File staticDir : resourceFile.listFiles())
                        {
                            if (staticDir.getName().contains("doe"))
                            {
                                return staticDir.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
