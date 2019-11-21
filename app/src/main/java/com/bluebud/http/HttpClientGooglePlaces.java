//package com.bluebud.http;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Message;
//
//import com.bluebud.info.PoiReInfo;
//import com.bluebud.utils.Constants;
//import com.bluebud.utils.LogUtil;
//
//public class HttpClientGooglePlaces {
//	public static final int SUCCESS = 1002;
//	public static final int FAIL = -1002;
//	private Handler mHandler;
//	private Message msg = new Message();
//
//	public void getPlaces(String sInput, Handler handler) {
//		mHandler = handler;
//
//		String url = getPlacesUrl(sInput);
//		DownloadTask downloadTask = new DownloadTask();
//		downloadTask.execute(url);
//
//	}
//
//	// Fetches data from url passed
//	private class DownloadTask extends AsyncTask<String, Void, String> {
//
//		// Downloading data in non-ui thread
//		@Override
//		protected String doInBackground(String... url) {
//
//			// For storing data from web service
//			String data = "";
//
//			try {
//				// Fetching the data from web service
//				data = downloadUrl(url[0]);
//			} catch (Exception e) {
//				LogUtil.i(e.toString());
//				return null;
//			}
//			return data;
//		}
//
//		// Executes in UI thread, after the execution of
//		// doInBackground()
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			if(null == result){
//				msg.what = FAIL;
//				mHandler.handleMessage(msg);
//				return;
//			}
//
//			ParserTask parserTask = new ParserTask();
//
//			// Invokes the thread for parsing the JSON data
//			parserTask.execute(result);
//		}
//	}
//
//	/** A class to parse the Google Places in JSON format */
//	private class ParserTask extends
//			AsyncTask<String, Integer, List<PoiReInfo>> {
//
//		// Parsing the data in non-ui thread
//		@Override
//		protected List<PoiReInfo> doInBackground(String... jsonData) {
//
//			JSONObject jObject;
//			List<PoiReInfo> places = null;
//
//			try {
//				jObject = new JSONObject(jsonData[0]);
//				places = placesJSONParser(jObject);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//			return places;
//		}
//
//		// Executes in UI thread, after the parsing process
//		@Override
//		protected void onPostExecute(List<PoiReInfo> result) {
//			if(null == result){
//				msg.what = FAIL;
//				mHandler.handleMessage(msg);
//				return;
//			}
//
//			msg.what = SUCCESS;
//			msg.obj = result;
//			mHandler.handleMessage(msg);
//		}
//	}
//
//	public List<PoiReInfo> placesJSONParser(JSONObject jObject) {
//		LogUtil.i("Parser");
//		List<PoiReInfo> poiReInfos = new ArrayList<PoiReInfo>();
//
//		List<List<HashMap<String, String>>> places = new ArrayList<List<HashMap<String, String>>>();
//		JSONArray jPredictions = null;
//		JSONArray jTerms = null;
//
//		try {
//			jPredictions = jObject.getJSONArray("predictions");
//			for (int i = 0; i < jPredictions.length(); i++) {
//				JSONObject jPrediction = ((JSONObject) jPredictions.get(i));
//				String sDescription = jPrediction.getString("description");
//				jTerms = jPrediction.getJSONArray("terms");
//				List<String> lName = new ArrayList<String>();
//				for (int j = 0; j < jTerms.length(); j++) {
//					JSONObject jTerm = ((JSONObject) jTerms.get(i));
//					lName.add(jTerm.getString("value"));
//				}
//				HashMap<String, String> map = new HashMap<String, String>();
//				map.put("name", lName.get(0));
//				map.put("description", sDescription);
//				List<HashMap<String, String>> lMaps = new ArrayList<HashMap<String, String>>();
//				lMaps.add(map);
//				places.add(lMaps);
//
//				LogUtil.i("name:" + lName.get(0));
//				LogUtil.i("address:" + sDescription);
//
//				PoiReInfo poiReInfo = new PoiReInfo();
//				poiReInfo.name = lName.get(0);
//				poiReInfo.address = sDescription;
//				poiReInfos.add(poiReInfo);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return poiReInfos;
//	}
//
//	private String downloadUrl(String strUrl) throws IOException {
//		LogUtil.i("downloadUrl()");
//		String data = "";
//		InputStream iStream = null;
//		HttpURLConnection urlConnection = null;
//		try {
//			URL url = new URL(strUrl);
//			// Creating an http connection to communicate with url
//			urlConnection = (HttpURLConnection) url.openConnection();
//			// Connecting to url
//			urlConnection.connect();
//			// Reading data from url
//			iStream = urlConnection.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					iStream));
//			StringBuffer sb = new StringBuffer();
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//			}
//			data = sb.toString();
//			LogUtil.e(sb.toString());
//			br.close();
//
//		} catch (Exception e) {
//			LogUtil.e("Exception while downloading url:" + e.toString());
//			return null;
//		} finally {
//			iStream.close();
//			urlConnection.disconnect();
//		}
//		return data;
//	}
//
//	/**
//	 * 地址搜索，组合成url
//	 * @return
//	 */
//	private String getPlacesUrl(String sInput) {
//		// https://maps.googleapis.com/maps/api/place/queryautocomplete/json?key=AIzaSyBNXzMBj_tJwlgPOIIHLQ5yYGEZRN6B_2M&sensor=false&language=zh&input=%E5%8D%97%E5%B1%B1%E5%8C%BA";
//		String output = "json?";
//		String key = "key=" + Constants.GOOGLE_PLACES_API_KEY;
//		String sensor = "&sensor=false";
//		// String language = "&language=";
//		String input = "&input=";
//		try {
//			input = "&input=" + URLEncoder.encode(sInput, "utf8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String url = "https://maps.googleapis.com/maps/api/place/queryautocomplete/"
//				+ output + key + sensor + input;
//		LogUtil.e("getPlacesURL--->: " + url);
//		return url;
//	}
//}
