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

public class GenreSongsViewModel extends ViewModel {

    private MutableLiveData<Cursor> songsCursor;

    public MutableLiveData<Cursor> getSongsCursor(Context cx, Long id) {
        if(songsCursor == null){
            songsCursor = new MutableLiveData<>();
            loadSongs(cx, id);
        }
        return songsCursor;
    }

    private void loadSongs(final Context context, final Long genreId){
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

                return ContentResolverCompat.query(context.getContentResolver(),
                        MediaStore.Audio.Genres.Members.getContentUri("external",genreId),
                        projection,null,null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                songsCursor.setValue(cursor);
            }
        }.execute();
    }
}
