package edu.vt.cs5560.amiout.services.datasource;

import java.io.File;
import java.util.List;

public interface DataFileSource
{
    public List<File> retrieveFiles() throws Exception;
}
