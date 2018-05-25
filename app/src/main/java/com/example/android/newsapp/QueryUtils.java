package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 20/3/18.
 */

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();
    private static final int connectTimeout = 15000;
    private static final int readTimeout = 10000;

    private QueryUtils() {
        throw new AssertionError();
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        if (requestUrl == null) {
            return null;
        }
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating url");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            Log.e(LOG_TAG, "null");
            return null;
        }
        Log.e(LOG_TAG, url.toString());
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Wrong response code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error requesting data");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading from stream");
        }
        return output.toString();
    }

    private static List<News> extractFromJson(String newsJson) {
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(newsJson);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                String title = "";
                if (resultObject.has("webTitle")) {
                    title = resultObject.getString("webTitle");
                }
                String sectionName = "";
                if (resultObject.has("sectionName")) {
                    sectionName = resultObject.getString("sectionName");
                }
                String author = "-";
                if (resultObject.has("tags")) {
                    JSONArray tagsArray = resultObject.getJSONArray("tags");
                    if (tagsArray.length() != 0) {
                        JSONObject tagsObject = tagsArray.getJSONObject(0);
                        author = tagsObject.getString("webTitle");
                    }
                }
                String url = "";
                if (resultObject.has("webUrl")) {
                    url = resultObject.getString("webUrl");
                }
                String publicationDate = "";
                if (resultObject.has("webPublicationDate")) {
                    publicationDate = resultObject.getString("webPublicationDate");
                }
                News news = new News(title, sectionName, author, url, publicationDate);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error extracting from json");
        }
        return newsList;
    }

    public static List<News> fetchNewsData(String requestUrl) {
        if (requestUrl == null) {
            return null;
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making http request");
        }
        return extractFromJson(jsonResponse);
    }
}
