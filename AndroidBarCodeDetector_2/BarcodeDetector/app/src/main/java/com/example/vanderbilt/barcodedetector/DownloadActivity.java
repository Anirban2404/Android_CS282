package com.example.vanderbilt.barcodedetector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DownloadActivity extends Activity {

    private ProgressBar progressBar;
    private ImageView imgPreview;
    private static Bitmap downloadBitmap;
    static int count;
    private static ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = (ProgressBar) findViewById(R.id.hprogressBar);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        Log.i("TAG", "Downloading is going to Start..");
        new DownloadImage().execute();

    }

    /*
     * Downloading the image using AsyncTask
	 */

    private class DownloadImage extends AsyncTask<Integer, Integer, Bitmap> {
        // Taking the URL as input
        final String url = "http://129.59.234.224/uploads/output.jpg";

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        protected void onPreExecute() {
            // URL length and protocol checking
            Log.i("TAG", "Start downloading via asyncTask..");
            progressBar.setProgress(0);
            super.onPreExecute();
        }


        /*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
        @Override
        protected Bitmap doInBackground(Integer... params) {
            // Downloading the image
            try {
                downloadBitmap = downloadBitmap(url);
                Log.i("TAG", "Downloading in progress..");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return downloadBitmap;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        protected void onPostExecute(Bitmap result) {
            // Set the Downloaded image at imageview
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageBitmap(downloadBitmap);
            progressBar.setVisibility(View.GONE);

            Intent intent = getIntent();
            count = intent.getIntExtra("count", 0);
            Log.i("TAG", "downloadCount: " + count);
            ArrayList<String> imageList = (ArrayList<String>) intent.getSerializableExtra("filePath");
            Log.i("TAG", "downimageList.size(): " + imageList.size());
            Log.i("TAG", "Download via AyscTask is done..");
            Log.i("TAG", "downloadCount2: " + count);
            count++;
            Log.i("TAG", "downloadCount3: " + count);


            try {
                String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
                String PATH = ExternalStorageDirectoryPath + "/" + "DCIM/LoadFolder" + "/";
                File folder = new File(PATH);
                if (!folder.exists()) {
                    folder.mkdir();//If there is no folder it will be created.
                }
                String tagetFileName = "output_" + count + ".jpg";
                OutputStream output = new FileOutputStream(PATH + tagetFileName);
                result.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();
                Log.i("TAG", "File Downloaded at " + PATH + tagetFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent uploadIntent = new Intent(DownloadActivity.this, UploadActivity.class);
            uploadIntent.putExtra("count", count);
            uploadIntent.putExtra("filePath", imageList);
            startActivity(uploadIntent);
        }


    }


    /*
	 * To download the image from web via protocol
	 * @param  url  an absolute URL giving the base location of the image
	 * @return      the image at the specified URL
	 * @see         Image
	 */
    Bitmap downloadBitmap(String url) throws IOException {

        long startTime = System.currentTimeMillis();
        Log.d("DownloadManager", "download begining");
        HttpUriRequest request = new HttpGet(url.toString());
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        long endTime = System.currentTimeMillis();
        System.gc();
		/* checking the URL is OK or not */
        if (200 == statusCode) {
            HttpEntity entity = response.getEntity();
            byte[] bytes = EntityUtils.toByteArray(entity);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Log.d("DownloadManager", "download ready in"
                    + ((endTime - startTime) / 1000) + " sec");
            int size = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, size, true);

            return scaled;
        } else {
			/* if URL is invalid */
            throw new IOException("Download failed, HTTP response code "
                    + statusCode + " - " + statusLine.getReasonPhrase());
        }
    }
}
