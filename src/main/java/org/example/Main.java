package org.example;

import com.opencsv.CSVWriter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<ForexNewsBean> forexNewsBean = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        while (startDate.isBefore(LocalDate.now())) {
            System.out.println(startDate);
            forexNewsBean.addAll(getForexNewsByDate(startDate.toString()));
            startDate = startDate.plusDays(1);
        }
        String filePath = "output.csv";
        String[] header = {
                "Event Name",
                "Currency",
                "Time",
                "Date",
                "Actual",
                "Forecast",
                "Previous"
        };
        writeListToCSV(forexNewsBean, filePath, header);
    }

    private static List<ForexNewsBean> getForexNewsByDate(String date) {
        List<ForexNewsBean> forexNewsBean = new ArrayList<>();
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
            params.add(new BasicNameValuePair("country[]", "72"));
            params.add(new BasicNameValuePair("country[]", "5"));
            params.add(new BasicNameValuePair("dateFrom", date));
            params.add(new BasicNameValuePair("dateTo", date));
            params.add(new BasicNameValuePair("timeZone", "8"));
            params.add(new BasicNameValuePair("timeFilter", "timeRemain"));
            params.add(new BasicNameValuePair("currentTab", "custom"));
            params.add(new BasicNameValuePair("limit_from", "0"));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpClient.execute(request);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));
            if (jsonObject != null && jsonObject.get("data") != null) {
                String htmlData = (String) jsonObject.get("data");
                htmlData = htmlData.replaceAll("\\\\/", "/");
                htmlData = htmlData.replaceAll("\\\\", "");
                htmlData = "<html>\n" +
                        "\t<head/>\n" +
                        "\t<body>\n" +
                        "\t\t<table>" + htmlData +
                        "</table>\n" +
                        "\t</body>\n" +
                        "</html>";
                try {
// Parse the HTML content
                    Document document = Jsoup.parse(htmlData);
                    List<Element> rows = document.select("tr"); // td
                    for (Element row : rows) {
                        Elements itemToFind = row.select("a");
                        if (itemToFind.size() == 1) {
                            String textEvent = itemToFind.get(0).text();
                            if (textEvent.contains("Manufacturing PMI")) {
                                if (textEvent.contains("ISM Manufacturing PMI")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "ISM Manufacturing PMI", date));
                                } else if (textEvent.contains("ISM Non-Manufacturing PMI")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "ISM Non-Manufacturing PMI", date));
                                }
                            } else if (textEvent.contains("CPI")) {
                                if (textEvent.contains("Core CPI (YoY)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Core CPI (YoY)", date));
                                } else if (textEvent.contains("Core CPI (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Core CPI (MoM)", date));
                                } else if (textEvent.contains("CPI (YoY)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "CPI (YoY)", date));
                                } else if (textEvent.contains("CPI (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "CPI (MoM)", date));
                                }
                            } else if (textEvent.contains("PPI")) {
                                if (textEvent.contains("Core PPI (YoY)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Core PPI (YoY)", date));
                                } else if (textEvent.contains("Core PPI (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Core PPI (MoM)", date));
                                } else if (textEvent.contains("PPI (YoY)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "PPI (YoY)", date));
                                } else if (textEvent.contains("PPI (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "PPI (MoM)", date));
                                }
                            } else if (textEvent.contains("Unemployment Rate")) {
                                if (textEvent.startsWith("Unemployment Rate")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Unemployment Rate", date));
                                }
                            } else if (textEvent.contains("Exports")) {
                                if (textEvent.startsWith("Exports")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Exports", date));
                                }
                            } else if (textEvent.contains("Imports")) {
                                if (textEvent.startsWith("Imports")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Imports", date));
                                }
                            } else if (textEvent.contains("Nonfarm payrolls")) {
                                if (textEvent.startsWith("Nonfarm payrolls")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Nonfarm payrolls", date));
                                }
                            } else if (textEvent.contains("Retail Sales")) {
                                if (textEvent.contains("Retail Sales (YoY)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Retail Sales (YoY)", date));
                                } else if (textEvent.contains("Retail Sales (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Retail Sales (MoM)", date));
                                }
                            } else if (textEvent.contains("Building permits")) {
                                if (textEvent.contains("Building permits (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Building permits (MoM)", date));
                                } else if (textEvent.contains("Building permits")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Building permits", date));
                                }
                            } else if (textEvent.contains("Existing Home Sales")) {
                                if (textEvent.contains("Existing Home Sales (MoM)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Existing Home Sales (MoM)", date));
                                } else if (textEvent.contains("Existing Home Sales")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Existing Home Sales", date));
                                }
                            } else if (textEvent.contains("Initial Jobless Claims")) {
                                forexNewsBean.add(createNewForexNewsBean(row, "Initial Jobless Claims", date));
                            } else if (textEvent.contains("Nonfarm Payrolls")) {
                                if (textEvent.startsWith("Nonfarm Payrolls")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "Nonfarm Payrolls", date));
                                }
                            } else if (textEvent.contains("GDP (QoQ)")) {
                                if (textEvent.startsWith("GDP (QoQ)")) {
                                    forexNewsBean.add(createNewForexNewsBean(row, "GDP (QoQ)", date));
                                }
                            } else if (textEvent.contains("Interest Rate Decision")) {
                                forexNewsBean.add(createNewForexNewsBean(row, "Interest Rate Decision", date));
                            } else if (textEvent.contains("Consumer Confidence")) {
                                forexNewsBean.add(createNewForexNewsBean(row, "Consumer Confidence", date));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (
                ParseException e) {
            throw new RuntimeException(e);
        }
        return forexNewsBean;
    }

    private static ForexNewsBean createNewForexNewsBean(Element row, String eventName, String date) {
        String time = Objects.requireNonNull(row.select("td.time").first()).text();
        String currency = Objects.requireNonNull(row.select("td.flagCur").first()).text();
        String actual = Objects.requireNonNull(row.select("td.act").first()).text();
        String forecast = Objects.requireNonNull(row.select("td.fore").first()).text();
        String previous = Objects.requireNonNull(row.select("td.prev").first()).text();
        return new ForexNewsBean(eventName, currency, time, LocalDate.parse(date), actual, forecast, previous);
    }

    public static void writeListToCSV(List<ForexNewsBean> data, String filePath, String[] header) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath), ';', '"', '"', "\n")) {
            writer.writeNext(header);
            for (ForexNewsBean row : data) {
                String[] rowString = {
                        row.getEventName(),
                        row.getEventCurrency(),
                        row.getTime(),
                        String.valueOf(row.getEventDate()),
                        row.getActual(),
                        row.getForecast(),
                        row.getPrevious()
                };
                writer.writeNext(rowString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}