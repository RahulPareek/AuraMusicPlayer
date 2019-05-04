package reverblabs.apps.aura.glide;


import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import androidx.annotation.NonNull;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.glide.album.AlbumImageLoader;
import reverblabs.apps.aura.glide.artist.ArtistImage;
import reverblabs.apps.aura.glide.artist.ArtistImageLoader;

@GlideModule
public class AuraGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
    }

    @Override
    public void registerComponents(@NonNull Context context,@NonNull Glide glide,@NonNull Registry registry) {
        registry.append(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory());
        registry.append(AlbumImage.class, InputStream.class, new AlbumImageLoader.Factory(context));
    }
}
