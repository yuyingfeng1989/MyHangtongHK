package com.bluebud.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.bluebud.utils.LogUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class HttpClientGoogleDirections {
	public static final int SUCCESS = 1001;
	public static final int FAIL = -1001;
	private Handler mHandler;
	private Message msg = new Message();

	public void getPolylineOptions(LatLng origin, LatLng dest, Handler handler) {
		mHandler = handler;

		String url = getDirectionsUrl(origin, dest);
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
			if(null == result){
				msg.what = FAIL;
				mHandler.handleMessage(msg);
				return;
			}

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
				System.out.println("do in background:" + routes);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			if(null == result){
				msg.what = FAIL;
				mHandler.handleMessage(msg);
				return;
			}
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(8);

				// Changing the color polyline according to the mode
				lineOptions.color(Color.BLUE);
			}

			msg.what = SUCCESS;
			msg.obj = lineOptions;
			mHandler.handleMessage(msg);
		}
	}

	public class DirectionsJSONParser {
		/**
		 * Receives a JSONObject and returns a list of lists containing latitude
		 * and longitude
		 */
		public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

			List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
			JSONArray jRoutes = null;
			JSONArray jLegs = null;
			JSONArray jSteps = null;

			try {

				jRoutes = jObject.getJSONArray("routes");

				/** Traversing all routes */
				for (int i = 0; i < jRoutes.length(); i++) {
					jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
					List path = new ArrayList<HashMap<String, String>>();

					/** Traversing all legs */
					for (int j = 0; j < jLegs.length(); j++) {
						jSteps = ((JSONObject) jLegs.get(j))
								.getJSONArray("steps");

						/** Traversing all steps */
						for (int k = 0; k < jSteps.length(); k++) {
							String polyline = "";
							polyline = (String) ((JSONObject) ((JSONObject) jSteps
									.get(k)).get("polyline")).get("points");
							List<LatLng> list = decodePoly(polyline);

							/** Traversing all points */
							for (int l = 0; l < list.size(); l++) {
								HashMap<String, String> hm = new HashMap<String, String>();
								hm.put("lat", Double.toString(list
										.get(l).latitude));
								hm.put("lng", Double.toString(list
										.get(l).longitude));
								path.add(hm);
							}
						}
						routes.add(path);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			return routes;
		}

		/**
		 * Method to decode polyline points Courtesy :
		 * jeffreysambells.com/2010/05/27
		 * /decoding-polylines-from-google-maps-direction-api-with-java
		 * */
		private List<LatLng> decodePoly(String encoded) {

			List<LatLng> poly = new ArrayList<LatLng>();
			int index = 0, len = encoded.length();
			int lat = 0, lng = 0;

			while (index < len) {
				int b, shift = 0, result = 0;
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lat += dlat;

				shift = 0;
				result = 0;
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lng += dlng;

				LatLng p = new LatLng(((lat / 1E5)),
						((lng / 1E5)));
				poly.add(p);
			}
			return poly;
		}
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

			LogUtil.i(sb.toString());

			br.close();

		} catch (Exception e) {
			LogUtil.i("Exception while downloading url:" + e.toString());
			return null;
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		LogUtil.i("url:" + strUrl + "---->   downloadurl:" + data);
		return data;
	}

	/**
	 * 通过起点终点，组合成url
	 * 
	 * @param origin
	 * @param dest
	 * @return
	 */
	private String getDirectionsUrl(LatLng origin, LatLng dest) {
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		// String str_dest =
		// "destination=22.540465459898503,113.94415766000748";
		String sensor = "sensor=false";
		// Travelling Mode
		// mode(选用，默认值：driving)指定计算导航时使用的交通模式。
		// driving表示使用标准行车导航。
		// walking 要求使用人行道及行人步行导航。
		// bicycling 要求使用自行车导航。(只适用于美国)
		String mode = "mode=driving";
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		LogUtil.i("getDerectionsURL--->: " + url);
		return url;
	}
}
