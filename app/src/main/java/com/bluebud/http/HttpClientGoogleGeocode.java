package com.bluebud.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.SystemUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpClientGoogleGeocode {
    public static final int SUCCESS_LOCATION = 1003;
    public static final int FAIL_LOCATION = -1003;
    public static final int SUCCESS_LOCATION_NAME = 1004;
    public static final int FAIL_LOCATION_NAME = -1004;
    public static final int SUCCESS_GEOFENCE = 1005;
    public static final int FAIL_GEOFENCE = -1005;
    public static final int SUCCESS_ROUTE_START = 1006;
    public static final int FAIL_ROUTE_START = -1006;
    public static final int SUCCESS_ROUTE_END = 1007;
    public static final int FAIL_ROUTE_END = -1007;
    public static final int SUCCESS_ROUTE = 1008;
    public static final int FAIL_ROUTE = -1008;
    public static final int SUCCESS_ALARM = 1009;
    public static final int SUCCESS_CUR_LOCATION = 1010;
    public static final int FAIL_CUR_LOCATION = -1010;
    public static final int SUCCESS_NEAR_BY_SEARCH = 1011;
    public static final int FAIL_NEAR_BY_SEARCH = -1011;
    private int SUCCESS;
    private int FAIL;
    public static final int MODE_LOCATION = 0;
    public static final int MODE_LOCATION_NAME = 1;
    public static final int MODE_GEOFENCE = 2;
    public static final int MODE_ROUTE_START = 3;
    public static final int MODE_ROUTE_END = 4;
    public static final int MODE_ROUTE = 5;
    public static final int MODE_ALARM = 6;
    public static final int MODE_CUR_LOCATION = 7;
    public static final int MODE_NEAR_BY_SEARCH = 8;
    private int iMode = MODE_LOCATION;

    private Handler mHandler;
    private MyGeocodeCallback mCallback;
    private Message msg = new Message();


    public void getFromLocation(int mode, LatLng ll, Handler handler) {
        mHandler = handler;

        if (mode == MODE_LOCATION) {
            iMode = MODE_LOCATION;
            SUCCESS = SUCCESS_LOCATION;
            FAIL = FAIL_LOCATION;
        } else if (mode == MODE_CUR_LOCATION) {
            iMode = MODE_CUR_LOCATION;
            SUCCESS = SUCCESS_CUR_LOCATION;
            FAIL = FAIL_CUR_LOCATION;
        } else if (mode == MODE_GEOFENCE) {
            iMode = MODE_GEOFENCE;
            SUCCESS = SUCCESS_GEOFENCE;
            FAIL = FAIL_GEOFENCE;
        } else if (mode == MODE_ROUTE_START) {
            iMode = MODE_ROUTE_START;
            SUCCESS = SUCCESS_ROUTE_START;
            FAIL = FAIL_ROUTE_START;
        } else if (mode == MODE_ROUTE_END) {
            iMode = MODE_ROUTE_END;
            SUCCESS = SUCCESS_ROUTE_END;
            FAIL = FAIL_ROUTE_END;
        } else if (mode == MODE_NEAR_BY_SEARCH) {
            iMode = MODE_NEAR_BY_SEARCH;
            SUCCESS = SUCCESS_NEAR_BY_SEARCH;
            FAIL = FAIL_NEAR_BY_SEARCH;
        }

        String url = getFromLocationUrl(ll);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    private Marker mMarker;

    public void getFromLocation(int mode, Marker marker, LatLng ll, Handler handler) {
        mHandler = handler;
        mMarker = marker;
        iMode = MODE_ROUTE;
        SUCCESS = SUCCESS_ROUTE;
        FAIL = FAIL_ROUTE;

        String url = getFromLocationUrl(ll);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    public void getNearByEarch(LatLng ll, int radius, String types, Handler handler) {
        mHandler = handler;
        iMode = MODE_NEAR_BY_SEARCH;
        SUCCESS = SUCCESS_NEAR_BY_SEARCH;
        FAIL = FAIL_NEAR_BY_SEARCH;

        String url = getNearBySearchUrl(ll, radius, types);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    private int iPosition;

    public void getFromLocation(int mode, int position, LatLng ll, Handler handler) {
        mHandler = handler;
        iPosition = position;
        iMode = MODE_ALARM;
        SUCCESS = SUCCESS_ALARM;

        String url = getFromLocationUrl(ll);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    public void getFromLocation(LatLng ll, MyGeocodeCallback callback) {
        mCallback = callback;
        iPosition = 0;
        iMode = MODE_ALARM;
        SUCCESS = SUCCESS_ALARM;

        String url = getFromLocationUrl(ll);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    public void getFromLocationName(String sAddress, Handler handler) {
        mHandler = handler;

        iMode = MODE_LOCATION_NAME;
        SUCCESS = SUCCESS_LOCATION_NAME;
        FAIL = FAIL_LOCATION_NAME;

        String url = getFromLocationNameUrl(sAddress);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            LogUtil.e(data);
            try {
                // Fetching the data from web service
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
    private class ParserTask extends AsyncTask<String, Integer, Object> {

        // Parsing the data in non-ui thread
        @Override
        protected Object doInBackground(String... jsonData) {
            Object object = new Object();

            try {
                JSONObject jObject = new JSONObject(jsonData[0]);
                LogUtil.i("search nearby =" + jObject.toString());

                if (iMode == MODE_LOCATION_NAME) {
                    object = locationNameJSONParser(jObject);
                } else {
                    object = locationJSONParser(jObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return object;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(Object object) {
            if (null == object) {
                msg.what = FAIL;
                mHandler.handleMessage(msg);
                return;
            }

            if (iMode == MODE_ROUTE) {
                mMarker.setSnippet(object.toString());
                msg.what = SUCCESS;
                msg.obj = mMarker;
                mHandler.handleMessage(msg);
                return;
            } else if (iMode == MODE_ALARM) {
                msg.what = SUCCESS;
                msg.arg1 = iPosition;
                msg.obj = object.toString();
                if (mHandler != null) {
                    mHandler.handleMessage(msg);
                } else {
                    mCallback.onGetAddressSucceed(object.toString());
                }
                return;
            }

            msg.what = SUCCESS;
            msg.obj = object;
            mHandler.handleMessage(msg);
        }
    }

    private String locationJSONParser(JSONObject jObject) {
        JSONArray jsonArray;
        JSONObject jsonObject;
        String sAddress = "";
        try {
            jsonArray = jObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                jsonObject = jsonArray.getJSONObject(0);
                sAddress = jsonObject.getString("formatted_address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("formatted_address:" + sAddress);
        return sAddress;
    }

    private LatLng locationNameJSONParser(JSONObject jObject) {
        LatLng ll = null;

        JSONArray jsonArray;
        JSONObject jsonObject;
        JSONObject jLocation;
        try {
            jsonArray = jObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                jsonObject = jsonArray.getJSONObject(0).getJSONObject("geometry");
                jLocation = jsonObject.getJSONObject("location");
                double lat = jLocation.getDouble("lat");
                double lng = jLocation.getDouble("lng");
                ll = new LatLng(lat, lng);
                LogUtil.i("location:" + ll.latitude + " " + ll.longitude);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ll;
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
            LogUtil.i("Exception while downloading url:" + e.toString());
            return null;
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getFromLocationUrl(LatLng ll) {
        String latlng = "latlng=" + ll.latitude + "," + ll.longitude;
        String output = "json";
        String sensor = "&sensor=false";
        String language = "&language=" + SystemUtil.getSystemLanguage();
        String key = "&key=AIzaSyBR14mnl32eeUgok-i3uuJN_gLphham9Dw";
//		String url = "http://maps.googleapis.com/maps/api/geocode/" + output
//				+ "?" + latlng + language + sensor;
        String url = "https://maps.googleapis.com/maps/api/geocode/" + output
                + "?" + latlng + language + sensor + key;
        LogUtil.e("Url--->: " + url);
        return url;
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

    private String getFromLocationNameUrl(String sAddress) {
        String address = "address=";
        try {
            address = "address=" + URLEncoder.encode(sAddress, "utf8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String output = "json";
        String sensor = "&sensor=false";
        String language = "&language=" + SystemUtil.getSystemLanguage();
        String url = "http://maps.googleapis.com/maps/api/geocode/" + output
                + "?" + address + language + sensor;
        LogUtil.e("Url--->: " + url);
        return url;
    }
}
