package reverblabs.apps.aura.viewModelLoaders;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArtistAlbumsViewModel extends ViewModel {

    private MutableLiveData<Cursor> artistAlbumsCursor;
    private Long id;

    public MutableLiveData<Cursor> getArtistAlbumsCursor(Context cx, Long artistId) {
        if(artistAlbumsCursor == null){
            artistAlbumsCursor = new MutableLiveData<>();
            id = artistId;
            loadAlbums(cx);
        }
        return artistAlbumsCursor;
    }

    private void loadAlbums(final Context context){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                String[] projection = {BaseColumns._ID,
                        MediaStore.Audio.Artists.Albums.ALBUM,
                        MediaStore.Audio.Artists.Albums.ARTIST,
                        MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Artists.Albums.FIRST_YEAR};

                return ContentResolverCompat.query(context.getContentResolver(),
                        MediaStore.Audio.Artists.Albums.getContentUri("external",id),
                        projection,null, null,
                        "Lower(" + MediaStore.Audio.Artists.Albums.ALBUM + ")ASC",
                            null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                artistAlbumsCursor.setValue(cursor);
            }
        }.execute();
    }
}
