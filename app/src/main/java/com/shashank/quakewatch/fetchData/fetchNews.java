package com.shashank.quakewatch.fetchData;

import android.text.TextUtils;
import android.util.Log;

import com.shashank.quakewatch.customAdapterAndLoader.news;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class fetchNews {

    private static final String LOG_TAG = "message";

    private fetchNews() {
    }

    private static List<news> extractArticlesFromJson(String NewsJSON) {
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }


        List<news> news = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(NewsJSON);

            JSONArray newsArray = baseJsonResponse.getJSONArray("articles");

            for (int i = 0; i < newsArray.length(); i++) {

                JSONObject currentNews = newsArray.getJSONObject(i);

                String description = currentNews.getString("description");
                String title = currentNews.getString("title");
//                String url = currentNews.getString("link");
                String url = currentNews.getString("url");
//                String imgURL = currentNews.getString("image_url");
                String imgURL = currentNews.getString("image");
                news news1 = new news(description, imgURL, url, title);
                news.add(news1);
            }

        } catch (JSONException e) {
            Log.e("fetchnews", "Problem parsing the earthquake JSON results",e);
        }
        return news;
    }
    public static List<news> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.",e);
        }
        return extractArticlesFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ",e);
        }
        return url;
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.addRequestProperty("Access-Control-Allow-Origin", "*");
            urlConnection.addRequestProperty("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode() + "\n" + urlConnection.getErrorStream());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
