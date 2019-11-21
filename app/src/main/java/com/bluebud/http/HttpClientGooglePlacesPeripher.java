package com.bluebud.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.bluebud.info.LatLng1;
import com.bluebud.info.PeripherInfo;
import com.bluebud.info.PoiReInfo;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.SystemUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpClientGooglePlacesPeripher {

    public static final int SUCCESS = 1011;
    public static final int FAIL = -1011;
    public static final int iMode = 8;


    private Handler mHandler;
    private Message msg = new Message();

    public void getNearByEarch(LatLng ll, int radius, String types, Handler handler) {
        LogUtil.i("走了新方法");
        mHandler = handler;
        String url = getNearBySearchUrl(ll, radius, types);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    private String getNearBySearchUrl(LatLng ll, int radius, String types) {
        String location = "location=" + ll.latitude + "," + ll.longitude;
        String radiuss = "&" + "radius=" + radius;
        String output = "json";
        String sensor = "&sensor=true";
        String type = "&types=" + types;
        String key = "&key=" + "AIzaSyBR14mnl32eeUgok-i3uuJN_gLphham9Dw";
        String language = "&language=" + SystemUtil.getSystemLanguage();
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output
                + "?" + location + radiuss + sensor + type + key;
        LogUtil.e("Near BySearch Url--->: " + url);

        //"http://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-41.319282,174.818717&radius=1000&sensor=true&types=cafe&key=your_api_key"
        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                LogUtil.i(e.toString());
                return null;
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result) {
                msg.what = FAIL;
                mHandler.handleMessage(msg);
                return;
            }

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<PoiReInfo>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<PoiReInfo> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<PoiReInfo> places = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                places = placesJSONParser(jObject);
                LogUtil.i("最终解析后" + places.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return places;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<PoiReInfo> result) {
            if (null == result) {
                msg.what = FAIL;
                mHandler.handleMessage(msg);
                return;
            }

            msg.what = SUCCESS;
            msg.obj = result;
            mHandler.handleMessage(msg);
        }
    }

    public List<PoiReInfo> placesJSONParser(JSONObject jObject) {
        LogUtil.i("Parser");
        List<PoiReInfo> poiReInfos = new ArrayList<PoiReInfo>();

        JSONArray jPredictions = null;

        try {
            jPredictions = jObject.getJSONArray("results");

            LogUtil.i("near by =" + jPredictions.toString());
            for (int i = 0; i < jPredictions.length(); i++) {
                LogUtil.i("走了解析啊");
                JSONObject jPrediction = ((JSONObject) jPredictions.get(i));

                JSONObject jsonObject = jPrediction.getJSONObject("geometry");

                PeripherInfo peripherInfo = GsonParse.serverGooglePeripher(jPrediction.toString());
                LogUtil.i("解析出来了" + peripherInfo.toString());
                LatLng1 latLng1 = GsonParse.GooglePeripherLag(peripherInfo.geometry.toString());
                LogUtil.i("latLng1=" + latLng1.lat + "," + latLng1.lng);
                PoiReInfo poiReInfo = new PoiReInfo();
                poiReInfo.address = peripherInfo.vicinity;
                poiReInfo.name = peripherInfo.name;
                poiReInfo.lat = latLng1.lat;
                poiReInfo.lon = latLng1.lng;
                poiReInfos.add(poiReInfo);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poiReInfos;
    }

    private String downloadUrl(String strUrl) throws IOException {
        LogUtil.i("downloadUrl()");
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            LogUtil.e(sb.toString());
            br.close();

        } catch (Exception e) {
            LogUtil.e("Exception while downloading url:" + e.toString());
            return null;
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
