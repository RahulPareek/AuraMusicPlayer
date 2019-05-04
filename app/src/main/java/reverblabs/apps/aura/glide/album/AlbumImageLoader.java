package reverblabs.apps.aura.glide.album;

import android.content.Context;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class AlbumImageLoader  implements ModelLoader<AlbumImage, InputStream> {

    private OkHttpClient client;
    private Context context;

    public AlbumImageLoader(OkHttpClient mClient, Context mContext){
        client = mClient;
        context = mContext;
    }

    @Override
    public LoadData<InputStream> buildLoadData(AlbumImage model, int width, int height, Options options){
        return new LoadData<>(model, new AlbumImageDataFetcher(client, model, context));
    }

    @Override
    public boolean handles(AlbumImage model) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<AlbumImage, InputStream> {
        private OkHttpClient okHttpClient;
        private Context context;

        public Factory(Context mContext){
            context = mContext;
            if (okHttpClient == null){
                okHttpClient = new OkHttpClient();
            }
        }

        @Override
        public ModelLoader<AlbumImage, InputStream> build(MultiModelLoaderFactory factory){
            return new AlbumImageLoader(okHttpClient, context);
        }

        @Override
        public void teardown() {
        }
    }
}
