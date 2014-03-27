package com.example.onbeatthis;

import sheetRenderer.MyGLSurfaceView;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import com.uraroji.garage.android.mp3recvoice.JSONCalls;
import com.uraroji.garage.android.mp3recvoice.RecMicToMp3;
import com.uraroji.garage.android.mp3recvoice.JSONCalls.JSONCallsListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Play extends Activity implements JSONCallsListener {

	
	private RecMicToMp3 mRecMicToMp3 = new RecMicToMp3(
			"/mnt/extSdCard/song_attempt.mp3", 8000);
	TextView statusTextView;
	Play bro;
	
	GLSurfaceView mGLView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		mGLView = new MyGLSurfaceView(this);
	
		LinearLayout opengl = (LinearLayout) findViewById(R.id.opengl);
		opengl.addView(mGLView);
		bro = this;
		statusTextView = (TextView) findViewById(R.id.StatusTextView);
		
		mRecMicToMp3.setHandle(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RecMicToMp3.MSG_REC_STARTED:
					statusTextView.setText("Recording ");
					break;
				case RecMicToMp3.MSG_REC_STOPPED:
					statusTextView.setText("Standby");
					break;
				case RecMicToMp3.MSG_ERROR_GET_MIN_BUFFERSIZE:
					statusTextView.setText("");
					Toast.makeText(Play.this,
							"Could not Record, Could not get MinBuffer Size-",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_CREATE_FILE:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Could Not Create Fil",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_REC_START:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Could Not Start Recording",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_AUDIO_RECORD:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Problems with Audio Record Class ",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_AUDIO_ENCODE:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Problems with Audio Encoding ",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_WRITE_FILE:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Could not write to file ",
							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_CLOSE_FILE:
					statusTextView.setText("");
					Toast.makeText(Play.this, "Could not close file ",
							Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		});

		Button startButton = (Button) findViewById(R.id.StartButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecMicToMp3.start();
				((MyGLSurfaceView) bro.mGLView).renderer.playing(true);
			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecMicToMp3.stop();
				((MyGLSurfaceView) bro.mGLView).renderer.playing(false);
				JSONCalls jcall = new JSONCalls(bro,Play.this);
				jcall.start();
				Toast.makeText(Play.this, "Attempting to send to Analysis Servers",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRecMicToMp3.stop();
	}

	@Override
	public void receivedAnalysis(String string) {
		statusTextView.setText(string);
	}

}
