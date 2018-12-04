package edu.vt.cs5560.amiout.config;

import edu.vt.cs5560.amiout.data.ReferenceData;
import edu.vt.cs5560.amiout.services.DBService;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.sql.SQLException;

@org.springframework.context.annotation.Configuration
public class Configuration
{
    @Bean(name = "referenceData")
    public ReferenceData referenceData() throws IOException {
        return new ReferenceData();
    }

    @Bean
    public DBService dbService() throws SQLException, ClassNotFoundException {
        return new DBService();
    }
}
