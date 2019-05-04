package reverblabs.apps.aura.viewModelLoaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.CursorLoader;

public class AlbumsViewModel extends ViewModel {
    private MutableLiveData<Cursor> albumsCursor;

    public MutableLiveData<Cursor> getAlbumsCursor(Context cx) {
        if(albumsCursor == null){
            albumsCursor = new MutableLiveData<>();
            loadAlbums(cx);
        }
        return albumsCursor;
    }

    private void loadAlbums(final Context context){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

                String[] projection = {BaseColumns._ID,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Albums.FIRST_YEAR};

                return ContentResolverCompat.query(context.getContentResolver(), uri,
                        projection, null, null,
                        "Lower(" + MediaStore.Audio.Albums.ALBUM + ")ASC",
                            null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                albumsCursor.setValue(cursor);
            }
        }.execute();
    }
}
