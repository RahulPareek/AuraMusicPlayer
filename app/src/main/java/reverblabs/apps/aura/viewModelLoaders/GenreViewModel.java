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

public class GenreViewModel extends ViewModel {
    private MutableLiveData<Cursor> genresCursor;

    public MutableLiveData<Cursor> getGenresCursor(Context cx) {
        if(genresCursor == null){
            genresCursor = new MutableLiveData<>();
            loadGenres(cx);
        }
        return genresCursor;
    }

    private void loadGenres(final Context context){
        new AsyncTask<Void, Void, Cursor>(){

            @Override
            protected Cursor doInBackground(Void... voids){
                Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Audio.Genres._ID,
                        MediaStore.Audio.Genres.NAME};

                return ContentResolverCompat.query(context.getContentResolver(), uri,
                        projection, null, null,
                        "Lower(" + MediaStore.Audio.Genres.NAME + ")ASC",
                        null);
            }

            @Override
            protected void onPostExecute(Cursor cursor){
                genresCursor.setValue(cursor);
            }
        }.execute();
    }
}
