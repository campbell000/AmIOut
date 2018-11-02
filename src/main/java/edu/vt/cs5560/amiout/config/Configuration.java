package edu.vt.cs5560.amiout.config;

import edu.vt.cs5560.amiout.data.ReferenceData;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class Configuration
{
    @Bean(name = "referenceData")
    public ReferenceData referenceData() throws IOException {
        return new ReferenceData();
    }
}
