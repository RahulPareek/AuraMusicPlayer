package reverblabs.apps.aura.glide.artist;

import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import reverblabs.apps.aura.glide.GetArtistImage;

public class ArtistImageDataFetcher implements DataFetcher<InputStream>, Callback {

    private DataCallback<? super InputStream> dataCallback;
    private InputStream inputStream;
    private ArtistImage artistImage;
    private OkHttpClient okHttpClient;
    private ResponseBody responseBody;


    public ArtistImageDataFetcher(OkHttpClient client, ArtistImage mArtist){
        okHttpClient = client;
        artistImage = mArtist;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback){

        dataCallback = callback;

        String url = GetArtistImage.getArtistImage(artistImage.artistName);

        if(url != null) {
            Log.i("Load Data", url);
            Request request = new Request.Builder().url(url).build();

            okHttpClient.newCall(request).enqueue(this);
        }
    }

    @Override
    public void onFailure(@NonNull Call call, IOException e) {
        dataCallback.onLoadFailed(e);
    }

    @Override
    public void onResponse(@NonNull Call call,@NonNull final Response response) throws IOException {
        if (!response.isSuccessful() && response.body() == null) {
            throw new IOException("Unexpected code " + response);
        } else {
            responseBody = response.body();
            inputStream = responseBody.byteStream();
            dataCallback.onDataReady(inputStream);
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass(){
        return InputStream.class;
    }

    @Override
    public void cleanup(){
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if(responseBody != null){
                responseBody.close();
            }
        }catch (IOException e){

        }
    }

    @Override
    public void cancel(){

    }

    @NonNull
    @Override
    public DataSource getDataSource(){
        return DataSource.REMOTE;
    }

}
