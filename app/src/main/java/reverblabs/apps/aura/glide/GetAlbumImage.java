package reverblabs.apps.aura.glide;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAlbumImage {

    private static final int MAX_SIZE = 833000;

    public static final String API_KEY = "3849cb0ebae45453da473abda7d97d80";
    public static final String SECRET_KEY = "6aa4911795effa2d580174fe30864a40";
    public static final String  BASE_URL = "https://ws.audioscrobbler.com/2.0/?";
    public static final String FORMAT = "json";


    public static String getAlbumImage(String album, String artist){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("method", "album.getInfo")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("artist", artist)
                .appendQueryParameter("album", album)
                .appendQueryParameter("autocorrect", "1")
                .appendQueryParameter("format", FORMAT).build();


        try {
            URL url = new URL(uri.toString());

            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();

            InputStream in = new BufferedInputStream(response.body().byteStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }

            if (in != null){
                in.close();
            }

            JSONObject result = new JSONObject(stringBuilder.toString());
            JSONObject data = result.getJSONObject("album");
            JSONArray imageArray = data.getJSONArray("image");

            JSONObject image = imageArray.getJSONObject(4);

            URL imageURL = new URL(image.getString("#text"));

            HttpURLConnection imageUrlConnection = (HttpURLConnection) imageURL.openConnection();

            int fileSize = imageUrlConnection.getContentLength();

            if (fileSize > MAX_SIZE){
                JSONObject finalImage = imageArray.getJSONObject(3);

                return new URL(finalImage.getString("#text")).toString();
            }
            else {
                JSONObject finalImage = imageArray.getJSONObject(4);

                return new URL(finalImage.getString("#text")).toString();
            }

        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return null;
    }


}
