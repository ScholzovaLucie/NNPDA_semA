package org.example.sema.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class LocationService {

    private static final String API_KEY = "ec4ae2832c56cc2b966df1271e748b07";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";

    public double getTemperatureByCoordinates(double latitude, double longitude) throws JSONException {
        try {
            String url = String.format(API_URL, latitude, longitude, API_KEY);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getJSONObject("main").getDouble("temp");
        } catch (Exception e) {
            return 0;
        }
    }
}
