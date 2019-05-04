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
import reverblabs.apps.aura.model.Artist;

public class ArtistViewModel extends ViewModel {

    private MutableLiveData<Cursor> artistsCursor;
    private Context context;

    public MutableLiveData<Cursor> getArtistsCursor(Context cx) {
        if(artistsCursor == null){
            artistsCursor = new MutableLiveData<>();
            context = cx;
            loadArtists();
        }
        return artistsCursor;
    }

    private void loadArtists(){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

                return ContentResolverCompat.query(context.getContentResolver(), uri, projection, null, null, "Lower(" + MediaStore.Audio.Artists.ARTIST + ")ASC", null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                artistsCursor.setValue(cursor);
            }
        }.execute();
    }
}
