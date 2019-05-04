package reverblabs.apps.aura.glide.album;


import android.util.Log;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class AlbumImage implements Key {
    public final String artistName;
    public final String albumName;
    public final int id;

    public AlbumImage(String artist, String album, int mId){
        artistName = artist;
        albumName = album;
        id = mId;
    }

    public void updateDiskCacheKey(MessageDigest messageDigest){
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(id + 10000).array());
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof String)){
            return false;
        }

        String local = (String) o;
        return albumName.equals(local);
    }

    @Override
    public int hashCode(){
        Log.i("Album Image", albumName + Integer.toString(id + 10000));
        return id + 10000;
    }


}
