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

public class UserPlaylistViewModel extends ViewModel {

    private MutableLiveData<Cursor> songsCursor;

    public MutableLiveData<Cursor> getSongsCursor(Context cx, Long id) {
        if(songsCursor == null){
            songsCursor = new MutableLiveData<>();
            loadSongs(cx, id);
        }
        return songsCursor;
    }

    private void loadSongs(final Context context, final Long playlistId){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                String projection[] = {MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.Playlists.Members.DATA,
                        MediaStore.Audio.Playlists.Members.ARTIST,
                        MediaStore.Audio.Playlists.Members.ARTIST_ID,
                        MediaStore.Audio.Playlists.Members.ALBUM,
                        MediaStore.Audio.Playlists.Members.ALBUM_ID,
                        MediaStore.Audio.Playlists.Members.TITLE,
                        MediaStore.Audio.Playlists.Members.DURATION,
                        MediaStore.Audio.Playlists.Members.YEAR};

                Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);


                return ContentResolverCompat.query(context.getContentResolver(), uri,
                        projection, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                songsCursor.setValue(cursor);
            }
        }.execute();
    }
}
