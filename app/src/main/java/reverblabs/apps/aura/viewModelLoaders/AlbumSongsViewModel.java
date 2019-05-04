package reverblabs.apps.aura.viewModelLoaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.CursorLoader;

public class AlbumSongsViewModel extends ViewModel {
    private MutableLiveData<Cursor> songsCursor;

    public MutableLiveData<Cursor> getSongsCursor(Context cx, String name) {
        if(songsCursor == null){
            songsCursor = new MutableLiveData<>();
            loadSongs(cx, name);
        }
        return songsCursor;
    }

    private void loadSongs(final Context context, final String albumName){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                String projection[] = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ARTIST_ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.YEAR};

                String selection =  MediaStore.Audio.Media.ALBUM + "=?";

                String[] selectionArgs = {albumName};

                return ContentResolverCompat.query(context.getContentResolver(), uri,
                        projection, selection, selectionArgs,
                        "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC",
                        null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                songsCursor.setValue(cursor);
            }
        }.execute();
    }
}
