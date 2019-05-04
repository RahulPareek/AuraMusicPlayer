package reverblabs.apps.aura.glide.album;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
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
import reverblabs.apps.aura.glide.GetAlbumImage;

public class AlbumImageDataFetcher  implements DataFetcher<InputStream>, Callback {

    private DataFetcher.DataCallback<? super InputStream> dataCallback;
    private InputStream inputStream;
    private AlbumImage albumImage;
    private OkHttpClient okHttpClient;
    private ResponseBody responseBody;
    private Context context;

    public AlbumImageDataFetcher(OkHttpClient client, AlbumImage mAlbum, Context mContext){
        okHttpClient = client;
        albumImage = mAlbum;
        context = mContext;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback){

        dataCallback = callback;

        try{

            final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(albumArtUri, albumImage.id);

            inputStream = context.getContentResolver().openInputStream(uri);
            dataCallback.onDataReady(inputStream);

        }catch(IOException e) {
        String url = GetAlbumImage.getAlbumImage(albumImage.albumName, albumImage.artistName);

            if(url != null) {
                Log.i("Load Data", url);
                Request request = new Request.Builder().url(url).build();

                okHttpClient.newCall(request).enqueue(this);
        }
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
