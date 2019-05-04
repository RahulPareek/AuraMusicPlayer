package reverblabs.apps.aura.viewModelLoaders;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.CursorLoader;

public class ArtistSongsViewModel extends ViewModel {
    private MutableLiveData<Cursor> artistSongsCursor;
    private Context context;
    private String name;

    public MutableLiveData<Cursor> getArtistSongsCursor(Context cx, String artistName) {
        if(artistSongsCursor == null){
            artistSongsCursor = new MutableLiveData<>();
            context = cx;
            name = artistName;
            loadSongs();
        }
        return artistSongsCursor;
    }

    private void loadSongs(){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                String projection[] = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ARTIST_ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.YEAR};

                String selection =  MediaStore.Audio.Artists.ARTIST+ "=?";

                String[] selectionArgs = {name};

                return ContentResolverCompat.query(context.getContentResolver(),
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                        selection, selectionArgs,
                        "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC", null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                artistSongsCursor.setValue(cursor);
            }
        }.execute();
    }
}
