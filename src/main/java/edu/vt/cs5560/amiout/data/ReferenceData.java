package edu.vt.cs5560.amiout.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReferenceData
{
    private Map<String, String> numToStateNameMap;
    private String stateIdNameMappingJson;

    public ReferenceData() throws IOException {
        this.numToStateNameMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("static/geojson/states.json");
        JsonNode parsedJson = new ObjectMapper().readTree(resource.getInputStream()).get("features");
        for (JsonNode featuresNode : parsedJson)
        {
            JsonNode stateInfo = featuresNode.get("properties");
            String state = stateInfo.get("NAME").asText();
            String stateId = stateInfo.get("STATE").asText();
            this.numToStateNameMap.put(stateId, state);
        }

        this.stateIdNameMappingJson = new ObjectMapper().writeValueAsString(this.numToStateNameMap);
    }

    public String getStateIdNameMapping()
    {
        return this.stateIdNameMappingJson;
    }

}
