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

public class PlaylistViewModel extends ViewModel {
    private MutableLiveData<Cursor> playlistsCursor;

    public MutableLiveData<Cursor> getPlaylistsCursor(Context cx) {
        if(playlistsCursor == null){
            playlistsCursor = new MutableLiveData<>();
            loadSongs(cx);
        }
        return playlistsCursor;
    }

    private void loadSongs(final Context context){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

                String projection[] = {MediaStore.Audio.Playlists._ID,
                        MediaStore.Audio.Playlists.NAME};


                return ContentResolverCompat.query(context.getContentResolver(), uri,
                        projection, null, null,
                        "Lower(" + MediaStore.Audio.Playlists.NAME+ ")ASC",
                        null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                playlistsCursor.setValue(cursor);
            }
        }.execute();
    }
}
