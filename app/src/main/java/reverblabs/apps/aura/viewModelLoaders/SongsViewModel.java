package reverblabs.apps.aura.viewModelLoaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SongsViewModel extends ViewModel {

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


                return ContentResolverCompat.query(context.getContentResolver(), uri, projection, MediaStore.Audio.Media.IS_MUSIC, null, "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC", null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                songsCursor.setValue(cursor);
            }
        }.execute();
    }
}
