package reverblabs.apps.aura.glide.artist;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class ArtistImageLoader implements ModelLoader<ArtistImage, InputStream> {

    private OkHttpClient client;

    public ArtistImageLoader(OkHttpClient mClient){
        client = mClient;
    }

    @Override
    public LoadData<InputStream> buildLoadData(ArtistImage model, int width, int height, Options options){
        return new LoadData<>(model, new ArtistImageDataFetcher(client, model));
    }

    @Override
    public boolean handles(ArtistImage model) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ArtistImage, InputStream>{
        private OkHttpClient okHttpClient;

        public Factory(){
            if (okHttpClient == null){
                okHttpClient = new OkHttpClient();
            }
        }

        @Override
        public ModelLoader<ArtistImage, InputStream> build(MultiModelLoaderFactory factory){
            return new ArtistImageLoader(okHttpClient);
        }

        @Override
        public void teardown() {
        }
    }
}
