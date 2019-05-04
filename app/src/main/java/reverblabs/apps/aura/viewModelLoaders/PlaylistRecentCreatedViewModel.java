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

public class PlaylistRecentCreatedViewModel extends ViewModel {
    private MutableLiveData<Cursor> songsCursor;

    public MutableLiveData<Cursor> getSongsCursor(Context cx) {
        if(songsCursor == null){
            songsCursor = new MutableLiveData<>();
            loadSongs(cx);
        }
        return songsCursor;
    }

    private void loadSongs(final Context context){
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
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.DATE_ADDED};

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                final StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(MediaStore.Audio.Media.IS_MUSIC + "=1");
                stringBuilder.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + " > " + ((System.currentTimeMillis() / 1000) - 60 * 60 * 24 * 7));

                return ContentResolverCompat.query(context.getContentResolver(),
                        uri, projection, stringBuilder.toString(), null,
                        MediaStore.Audio.Media.DATE_ADDED + " DESC", null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                songsCursor.setValue(cursor);
            }
        }.execute();
    }
}
