package edu.vt.cs5560.amiout.utils.io;

import java.io.File;

public class ClasspathLoader {
    public static String getFullPathOfFile(String filePrefix){
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
                            if (staticDir.getName().startsWith(filePrefix))
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
