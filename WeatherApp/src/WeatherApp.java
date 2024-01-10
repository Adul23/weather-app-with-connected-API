// retrieve weather data from API for fetching the latest weather
// data from the external API to display it to user

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    // fetch data
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);
        // x and y coordinates latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");
        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" +
                latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }
            StringBuilder resultJson = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());
            while (sc.hasNext()) {
                resultJson.append(sc.nextLine());
            }
            sc.close();
            conn.disconnect();
            // parse through all the data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            // want to get the current hour's data
            // need an index
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);
            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);
            // get windspeed
            JSONArray windspeedData  = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);
            // build the weather json data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            return weatherData;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    // retrieves geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in location name to + to adhere to API's request format
        locationName = locationName.replace(" ", "+");
        // build API url
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try {
            // call api and try to get a response
            HttpURLConnection conn  = fetchApiResponse(urlString);
            // check status
            // 200 is successful
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;

            } else{
                // store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());
                // read and store data to stringbuilder
                while (sc.hasNext()) {
                    resultJson.append(sc.nextLine());
                }
                // close scanner and disconnect url
                sc.close();
                conn.disconnect();

                // parse the JSON string into a JSON abj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }


        }catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set request method to get
            conn.setRequestMethod("GET");
            // connect to our API
            conn.connect();
            return conn;
        }catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        // iterate through the time list and see what matches with current
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }

        }
        return 0;
    }
    private static String getCurrentTime() {
        LocalDateTime currentDataTime = LocalDateTime.now();
        // format date needs to be reformatted
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print
        String formattedDateTime = currentDataTime.format(formatter);

        return formattedDateTime;
    }
    // convert the
    //
    //
    // to words
    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            weatherCondition = "Clear";
        } else if(weathercode <= 3L && weathercode > 0L) {
            weatherCondition = "Cloudy";}
//        } else if(weathercode == 45L || weathercode == 48L) {
//            weatherCondition = "Fog";
//        }
//        else if(weathercode == 51L || weathercode == 53L || weathercode == 55L) {
//            weatherCondition = "Drizzle";
//        }
//        else if(weathercode == 56L ||  weathercode == 57L) {
//            weatherCondition = "Freezing Drizzle";
//        }
            else if (weathercode == 61L || weathercode == 63L || weathercode == 65L) {
                weatherCondition = "Rain";
            } else if (weathercode == 66L || weathercode == 67L) {
                weatherCondition = "Rain";
            } else if (weathercode == 71L || weathercode == 73L || weathercode == 75L) {
                weatherCondition = "Snow";
            } else if (weathercode == 77L) {
                weatherCondition = "Snow";
            } else if (weathercode >= 80L && weathercode <= 82L) {
                weatherCondition = "Rain";
            } else if (weathercode == 86L || weathercode == 85L) {
                weatherCondition = "Snow";
            }
//        else if(weathercode == 95L) {
//            weatherCondition = "ThunderStorm: Slight and moderate";
//        }
//        else if(weathercode == 96L || weathercode == 99L) {
//            weatherCondition = "ThunderStorm: with slight and heavy hal";
//        }

        return weatherCondition;
    }
}
