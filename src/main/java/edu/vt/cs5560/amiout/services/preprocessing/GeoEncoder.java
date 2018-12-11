package edu.vt.cs5560.amiout.services.preprocessing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.vt.cs5560.amiout.services.datasource.doe.ResultWrapper;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GeoEncoder {
    private static final String URL_PREFIX = "https://nominatim.openstreetmap.org/search?q=";
    private static final String URL_SUFFIX = "&format=json&polygon_geojson=1";
    private Gson gson = new Gson();
    private ObjectMapper mapper = new ObjectMapper();
    static File file = new File("resp.jsons");
    static List<String> lines = null;
    public static int count = 0;

    public Object getGeoEncodeForLocation(String locationString) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        if (lines == null)
            lines = Files.readAllLines(file.toPath());

        StringBuilder builder = new StringBuilder();
        RestTemplate client = null;

        builder.append(URL_PREFIX);
        builder.append(locationString);
        builder.append(URL_SUFFIX);
        String rawJson = getLocationJSON(client, builder);

        // FInd polygon if it exists, otherwise, find multipoly, otherwise
        Map[] guesses = gson.fromJson(rawJson, Map[].class);
        if (guesses.length > 0)
        {
            for (Map<String, Map> firstElement : guesses)
            {
                String type = (firstElement.get("geojson")).get("type").toString();
                if (type.equalsIgnoreCase("polygon"))
                    return mapper.readValue(gson.toJson(firstElement), GeoEncodeResult.class);
                else if (type.equalsIgnoreCase("multipolygon"))
                    return mapper.readValue(gson.toJson(firstElement), GeoEncodeResultMulti.class);
            }
        }
        return null;
    }

    public String getLocationJSON(RestTemplate client, StringBuilder builder) throws IOException {
        //client.getForObject(builder.toString(), String.class);
        String line32 = lines.get(count);
        count++;

        return line32;
    }

    public RestTemplate initRestClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Object results = new GeoEncoder().getGeoEncodeForLocation("California");
        System.out.println();
    }
}
