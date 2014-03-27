package com.uraroji.garage.android.mp3recvoice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.onbeatthis.Play;
import com.example.onbeatthis.R;

import android.app.Activity;
import android.media.AudioRecord;
import android.util.Log;
import android.view.View.OnClickListener;

public class JSONCalls {

	public final static String META_SCORE_KEY = "meta_score";
	public final static String SCORE_KEY = "score";
	public final static String ALBUM_KEY = "release";
	public final static String TITLE_KEY = "track";
	public final static String TRACK_ID_KEY = "track_id";
	public final static String ARTIST_KEY = "artist";
	Thread thread;
	private JSONCallsListener listener;
	//important, contains API KEY and server location
	private final String UPLOAD_SERVER_URL = "http://developer.echonest.com/api/v4/track/upload?api_key=Z3IQTFDZ9WFXZAKBD";
	Play stuff;
	public interface JSONCallsListener {
		public void receivedAnalysis(String string);
	}

	public JSONCalls(JSONCallsListener listener) {
		this.listener = listener;
		thread = new Thread();
		thread.start();
	}

	public String jSONtoString(HttpEntity entity) {
		String result = "";
		try {
			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				// now you have the string representation of the HTML
				// request
				instream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Fingerprinter", e.getLocalizedMessage());
		}
		return result;
	}
	

	public JSONCalls(Play mainActivity, JSONCallsListener listener) {
		this.listener = listener;
		thread = new Thread();
		stuff = mainActivity;
		try {
			parseInitial();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.start();
	}

	public void start() {
		new Thread() {

			public void run() {
				try {

					long time = System.currentTimeMillis();

					// fetch data from echonest
					time = System.currentTimeMillis();
					
					String urlstr = UPLOAD_SERVER_URL + "&filetype=mp3";
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(urlstr);

					post.setEntity(new FileEntity(new File(
							"/mnt/extSdCard/song_attempt.mp3"),
							"application/octet-stream"));
					// get response
					HttpResponse response = client.execute(post);
					// Examine the response status
					Log.d("Fingerprinter", response.getStatusLine().toString());

					// Get hold of the response entity
					HttpEntity entity = response.getEntity();
					// If the response does not enclose an entity, there is no
					// need
					// to worry about connection release

					String result = "";
					result = jSONtoString(entity);
					Log.d("Fingerprinter",
							"Results fetched in: "
									+ (System.currentTimeMillis() - time)
									+ " millis");
					Log.d("Fingerprinter", result);
					
					/* to get anaylsis of base file
					
					urlstr = UPLOAD_SERVER_URL + "&filetype=mp3";
					client = new DefaultHttpClient();
					post = new HttpPost(urlstr);

					post.setEntity(new FileEntity(new File(
							"/mnt/extSdCard/ff13-2 serah theme midi.mp3"),
							"application/octet-stream"));
					// get response
					response = client.execute(post);
					// Examine the response status
					Log.d("Fingerprinter", response.getStatusLine().toString());

					// Get hold of the response entity
					entity = response.getEntity();
					// If the response does not enclose an entity, there is no
					// need
					// to worry about connection release

					result = "";
					result = jSONtoString(entity);
					*/

					// parse JSON

					JSONObject jobj = new JSONObject(result);

					String id = jobj.getJSONObject("response")
							.getJSONObject("track").getString("id");
					Log.w("sadasas", id);
					time = System.currentTimeMillis();
					// make the thread sleep to let server process request
					thread.sleep(5000);
					HttpGet get = new HttpGet(
							"http://developer.echonest.com/api/v4/track/profile?api_key=Z3IQTFDZ9WFXZAKBD&format=json&bucket=audio_summary&id="
									+ id);
					response = client.execute(get);
					entity = response.getEntity();
					// If the response does not enclose an entity, there is no
					// need
					// to worry about connection release

					result = "";

					result = jSONtoString(entity);

					JSONObject jobj2 = new JSONObject(result);
					String analurl = jobj2.getJSONObject("response")
							.getJSONObject("track")
							.getJSONObject("audio_summary")
							.getString("analysis_url");
					Log.w("TEAM8 APP", "" + analurl);
					get = new HttpGet(analurl);
					response = client.execute(get);
					entity = response.getEntity();

					result = "";
					result = jSONtoString(entity);
					
					JSONObject jobj3 = new JSONObject(result);
					JSONArray jSegmentArray = jobj3.getJSONArray("segments");
					String jobj4;
					if ((jobj4= jSegmentArray.getJSONObject(0)
							.getString("start")) != null) {
						receivedAnalysis(jobj4);
						Log.w("sadsadsad", jobj3.getJSONArray("segments")
								.getJSONObject(0).getString("start"));
						Log.d("Fingerprinter",
								"Results fetched in: "
										+ (System.currentTimeMillis() - time)
										+ " millis");
						Log.d("Fingerprinter", result);

					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Fingerprinter", e.getLocalizedMessage());

				}

			}
		}.start();
	}

	public void receivedAnalysis(final String string) {
		if (listener == null)
			return;

		if (listener instanceof Activity) {
			Activity activity = (Activity) listener;
			activity.runOnUiThread(new Runnable() {
				public void run() {
					listener.receivedAnalysis(string);
				}
			});
		} else
			listener.receivedAnalysis(string);
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public void parseSongAttempt()
	{
		
	}
	
	public void parseInitial() throws JSONException
	{
		InputStream is = stuff.getResources().openRawResource(R.raw.serahthemefull);
		String result = "";
		result = convertStreamToString(is);
		JSONObject jobj = new JSONObject(result);
		JSONArray jSegmentArray = jobj.getJSONArray("segments");
		String  id = jSegmentArray.getJSONObject(0).getString("start");
		Log.w("you", id);
		ArrayList<MusicValues> serahThemeList = new ArrayList<MusicValues>();
		for(int x =0; x < jSegmentArray.length(); x++)
		{
			
			id = jSegmentArray.getJSONObject(x).getString("start");
			double ids = Double.parseDouble(id);
			MusicValues segmentValue = new MusicValues(ids); 
			JSONArray jSegmentPitches= jSegmentArray.getJSONObject(x).getJSONArray("pitches");
			
			for(int i = 0; i < jSegmentPitches.length(); i++)
			{	
				Double j = jSegmentPitches.getDouble(i);
				segmentValue.pitches.add(j);
				//Log.w("parserserah", "" + j);
				
			}
			
			
			serahThemeList.add(segmentValue);
			
		}
		for(int i = 0; i<serahThemeList.size(); i++)
		{
			Log.w("musicval", ""+serahThemeList.get(i).start);
		}
		
		
		
	}

}

