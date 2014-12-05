package com.anirban.mapdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Anirban Bhattacharjee
 * 
 */

/*My own activity class LifecycleLoggingActivity class 
 * which extends Activity and override the functions 
 */

/*To develop a simple Android application that displays a map 
 * given a pair of latitude and longitude coordinates.
 */

public class MapDemoActivity extends LifecycleLoggingActivity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_demo);
		Log.d(getClass().getSimpleName(), "onCreate()");
	}

	
	/*
	 * User has to choose whether to run on Google API or open in Browser
	 */
	
	// Calling the function for android:onClick

	public void show_location(View v) {
		try {

			// initialize UI elements
			Log.i("Button", "Button pushed..Show the Location on Google Map");
			final EditText Latitude = (EditText) findViewById(R.id.editText1);
			final EditText Longitude = (EditText) findViewById(R.id.editText2);

			// Read the latitude and longitude from the input fields
			String latString = Latitude.getText().toString();
			String lonString = Longitude.getText().toString();

			// Toast declarations
			Context context = getApplicationContext();
			CharSequence text1 = "Out of Range, Please enter properly!!";
			CharSequence text2 = "Please enter the value in number format!!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast1 = Toast.makeText(context, text1, duration);
			Toast toast2 = Toast.makeText(context, text2, duration);

			// Only execute if user has put entries in both lat and long fields.
			if (latString.length() == 0 || lonString.length() == 0) {
				Log.d(getClass().getSimpleName(), "Please enter value");
				toast2.show();
			}

			else {
				double lati = Double.valueOf(latString.trim()).doubleValue();
				double longi = Double.valueOf(lonString.trim()).doubleValue();

				// Only execute if user has put correct rannge i both lat and
				// long fields.

				if ((lati >= -90 && lati <= 90)
						&& (longi >= -180 && longi <= 180)) {
					double lat = Double.parseDouble(latString);
					double lon = Double.parseDouble(lonString);
					final String MAP_URL = "geo:0,0?q=";
					startActivity(new Intent(
							android.content.Intent.ACTION_VIEW,
							Uri.parse(MAP_URL + lat + "," + lon)));
				} else {
					Log.d(getClass().getSimpleName(),
							"Out of Range, Please enter properly");
					toast1.show();
				}
			}

		} catch (NumberFormatException e) {
			Log.e("Geocoder", "Please enter number", e);
		} catch (Exception e) {
			Log.e("Geocoder", "I/O Failure; is network available?", e);
		}
	}
}
