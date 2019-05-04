package reverblabs.apps.aura.glide.artist;


import android.util.Log;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class ArtistImage implements Key {
    public final String artistName;
    public final int id;

    public ArtistImage(String artist, int mId){
        artistName = artist;
        id = mId - 10000;
    }

    public void updateDiskCacheKey(MessageDigest messageDigest){
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(id).array());
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof String)){
            return false;
        }

        String local = (String) o;
        return artistName.equals(local);
    }

    @Override
    public int hashCode(){
        Log.i("Artist Image", artistName + Integer.toString(id));
        return id;
    }


}
