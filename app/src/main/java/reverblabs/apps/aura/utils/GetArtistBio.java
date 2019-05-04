package reverblabs.apps.aura.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetArtistBio extends AsyncTask<Void, Void, String> {

    private String artist;
    private TextView textView;
    private ProgressBar progressBar;

    public GetArtistBio(String mName, TextView mTextView, ProgressBar mProgressBar){
        artist = mName;
        textView = mTextView;
        progressBar = mProgressBar;
    }

    @Override
    protected void onPreExecute(){
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... params){

        String result;

        Uri uri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendQueryParameter("method", "artist.getInfo")
                .appendQueryParameter("artist", artist)
                .appendQueryParameter("autocorrect", "1")
                .appendQueryParameter("api_key", Constants.API_KEY)
                .appendQueryParameter("format", Constants.FORMAT).build();

        try {
            URL url = new URL(uri.toString());

            HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setUseCaches(true);
            connection.setRequestMethod("POST");

            connection.connect();


            int response = connection.getResponseCode();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }

            result = stringBuilder.toString();

            if (in != null){
                in.close();
            }
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("artist");
            JSONObject extractedInfo = data.getJSONObject("bio");
            return extractedInfo.getString("content");

        }
        catch (IOException | JSONException e){
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result){

        progressBar.setVisibility(View.GONE);

        if (result != null) {
            textView.setText(result);
        }
    }
}
