package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            String url = "https://www.investing.com/economic-calendar/Service/getCalendarFilteredData";
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            request.addHeader("sec-fetch-dest", "empty");
            request.addHeader("sec-fetch-mode", "cors");
            request.addHeader("sec-fetch-site", "same-origin");
            request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67");
            request.addHeader("x-requested-with", "XMLHttpRequest");

            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("country%5B%5D", "72"));
            params.add(new BasicNameValuePair("country%5B%5D", "5"));
            params.add(new BasicNameValuePair("dateFrom", "2022-01-03"));
            params.add(new BasicNameValuePair("dateTo", "2022-01-03"));
            params.add(new BasicNameValuePair("timeZone", "8"));
            params.add(new BasicNameValuePair("timeFilter", "timeRemain"));
            params.add(new BasicNameValuePair("currentTab", "custom"));
            params.add(new BasicNameValuePair("limit_from", "0"));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpClient.execute(request);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));
            if (jsonObject != null && !(Boolean) jsonObject.get("error")) {
                JSONArray allSentiments = (JSONArray) jsonObject.get("symbols");
                OffsetDateTime update = OffsetDateTime.now();
                try {
                    for (Object x : allSentiments) {
                        JSONObject sentiment = (JSONObject) x;
                        if (currency.equals(sentiment.get("name"))) {
                            ForexSentimentBean bean = new ForexSentimentBean();
                            bean.setTokenForexSentiment(0L);
                            bean.setUpdateTimestamp(update);
                            bean.setShortPosition(new BigDecimal(((Long) sentiment.get("shortPercentage")).toString()));
                            bean.setLongPosition(new BigDecimal(((Long) sentiment.get("longPercentage")).toString()));
                            bean.setCurrency(findCurrency(currencies, currency, ""));
                            sentiments.add(bean);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
//            URL requestUrl = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
//            connection.setRequestMethod("POST");
//            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            //connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
//            //connection.setDoOutput(true);
//
//            byte[] requestDataBytes = requestData.getBytes(StandardCharsets.UTF_8);
//            ///connection.getOutputStream().write(requestDataBytes);
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                StringBuilder response = new StringBuilder();
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//
//                String jsonResponse = response.toString();
//                System.out.println(jsonResponse);
//            } else {
//                System.out.println("Request failed with response code: " + responseCode);
//            }

//            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}