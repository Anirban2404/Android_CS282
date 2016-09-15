package com.example.vanderbilt.barcodedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vanderbilt.barcodedetector.AndroidMultiPartEntity.ProgressListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import com.loopj.android.http.*;

public class UploadActivity extends Activity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    long totalSize = 0;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private Button btnUpload;
    private static int count = 0;
    private static ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        // Receiving the data from previous activity
        Intent i = getIntent();
        Intent downloadIntentBack = getIntent();
        count = downloadIntentBack.getIntExtra("count", 0);
        Log.i("TAG", "downloadCountBack::::: " + count);

        // image or video path that is captured in previous activity

        Log.i("TAG", "upload: " + count + ": " + count);

        if (count == 0) {
            ArrayList<String> imageList = (ArrayList<String>) i.getSerializableExtra("filePath");

            Log.i("TAG", "imageList.size(): " + imageList.size());
            filePath = imageList.get(count);
            Log.i("TAG", "upload: " + count + ": " + filePath);
            uploadImage(filePath);
        }
        if (count >= 1) {
            int c = count;
            //Intent downloadIntentBack = getIntent();
            //count = downloadIntentBack.getIntExtra("count", 0);
            ArrayList<String> imageList1 = (ArrayList<String>) downloadIntentBack.getSerializableExtra("filePath");
            Log.i("TAG", "imageList1.size(): " + imageList1.size());
            if (count == imageList1.size()) {
                Log.i("TAG", "ALL DONE");
                Intent intent = new Intent(UploadActivity.this, FinalActivity.class);
                startActivity(intent);
            }
            else {
                Log.i("TAG", "downloadCountBack: " + c);
                filePath = imageList1.get(c);
                Log.i("TAG", "upload: " + c + ": " + filePath);
                uploadImage(filePath);
            }
        }
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        final ArrayList<String> myList = imageList;
        outState.putStringArrayList("file_uri", myList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        imageList = savedInstanceState.getStringArrayList("file_uri");
    }


    public void uploadImage(final String filePath) {

        if (filePath != null) {
            //Log.i("TAG", "uploadpath: " + count + filePath);
            // Displaying the image or video on the screen
            previewMedia(true);

            UploadFileToServer _uploadTask = new UploadFileToServer();
            _uploadTask.execute();


        } else

        {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Displaying captured image/video on the screen
     */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            imgPreview.setImageBitmap(bitmap);
        }
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        //alert.show();
    }


    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);
                Log.i("TAG", "Asyncuploadpath: " + count + filePath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("129.59.234.224"));
                entity.addPart("email", new StringBody("anirban.vandy@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            //showAlert(result);
            //super.onPostExecute(result);
             /* Start download activity
            */
            Intent downloadIntentBack = getIntent();
            ArrayList<String> imageList = (ArrayList<String>) downloadIntentBack.getSerializableExtra("filePath");
            Log.i("TAG", "uploadCount: " + count);
            Intent downloadIntent = new Intent(UploadActivity.this, DownloadActivity.class);
            downloadIntent.putExtra("count", count);
            downloadIntent.putExtra("filePath", imageList);
            startActivity(downloadIntent);
        }
    }
}
