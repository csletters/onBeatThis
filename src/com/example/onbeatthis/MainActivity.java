package com.example.onbeatthis;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView myTextView=(TextView)findViewById(R.id.title);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Airplane.ttf");
        myTextView.setTypeface(typeFace);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void signUserIn(View view) {	
		Intent intent = new Intent(this, LogInActivity.class);		
		startActivity(intent);		
	}
    
    public void createAccount(View view){
    	Intent intent = new Intent(this, CreateAccount.class);		
		startActivity(intent);  
    }
    
}
