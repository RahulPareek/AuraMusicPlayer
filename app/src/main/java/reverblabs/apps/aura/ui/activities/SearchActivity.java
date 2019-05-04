package reverblabs.apps.aura.ui.activities;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.ui.adapters.AdapterForSearch;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class SearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    Context context;
    ContentResolver contentResolver;

    String queryText;

    int ALBUM_LOADER = 0;
    int ARTIST_LOADER = 1;
    int SONG_LOADER = 2;


    List searchResults = Collections.emptyList();

    AdapterForSearch adapterForSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_layout);

        context = getApplicationContext();
        contentResolver = getContentResolver();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterForSearch = new AdapterForSearch(context, this);
        recyclerView.setAdapter(adapterForSearch);

        searchResults = new ArrayList();

        Intent intent = getIntent();

        queryText = intent.getStringExtra(SearchManager.QUERY);

        getSupportLoaderManager().initLoader(ALBUM_LOADER, null, this);
        getSupportLoaderManager().initLoader(ARTIST_LOADER, null, this);
        getSupportLoaderManager().initLoader(SONG_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);

        MenuItem searchItem = menu.findItem(R.id.search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });

        menu.findItem(R.id.search).expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

            if (id == ALBUM_LOADER) {
                Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

                String[] projection = {BaseColumns._ID,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Albums.FIRST_YEAR};

                String where = MediaStore.Audio.Albums.ALBUM + " LIKE ?";

                String[] whereVal = {"%" + queryText + "%"};

                return new CursorLoader(this, uri, projection, where, whereVal, null);

            } else if (id == ARTIST_LOADER) {
                Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

                String where = MediaStore.Audio.Artists.ARTIST + " LIKE ?";

                String[] whereVal = {"%" + queryText + "%"};

                return new CursorLoader(this, uri, projection, where, whereVal, null);
            } else if (id == SONG_LOADER) {

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

                String where = MediaStore.Audio.Media.TITLE + " LIKE ?";

                String[] whereVal = {"%" + queryText + "%"};


                return new CursorLoader(this, uri, projection, where, whereVal, null);
            }
        else {
                return null;
            }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor){

            if (loader.getId() == ALBUM_LOADER) {

                List<Album> albums = MusicHelper.extractAlbumsFromCursor(cursor);

                if(albums != null) {
                    if (!(albums.isEmpty())) {
                        searchResults.add(getString(R.string.albums_tab));
                        searchResults.addAll((Collection) albums);
                    }
                }

            } else if (loader.getId() == ARTIST_LOADER) {

                List<Artist> artists = MusicHelper.extractArtistFromCursor(cursor);

                if(artists != null) {
                    if (!(artists.isEmpty())) {
                        searchResults.add(getString(R.string.artists_tab));
                        searchResults.addAll((Collection) artists);

                    }
                }
            } else if (loader.getId() == SONG_LOADER) {

                List<Song> songs = MusicHelper.extractSongsFromCursor(cursor);

                if (songs != null) {
                    if (!(songs.isEmpty())) {
                        searchResults.add(getString(R.string.songs_tab));
                        searchResults.addAll((Collection) songs);
                    }
                }

            }

        adapterForSearch.updateSearchResults(searchResults);
        adapterForSearch.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader loader){
    }

    @Override
    public boolean onQueryTextChange(final String newText){
        queryText = newText;

        searchResults.clear();
        adapterForSearch.notifyDataSetChanged();

        if(queryText.equals(""))
            return true;

        getSupportLoaderManager().restartLoader(ALBUM_LOADER, null, this);
        getSupportLoaderManager().restartLoader(ARTIST_LOADER, null, this);
        getSupportLoaderManager().restartLoader(SONG_LOADER, null, this);

        return true;

    }

    @Override
    public boolean onQueryTextSubmit(final String newText) {
        queryText = newText;

        searchResults.clear();
        adapterForSearch.notifyDataSetChanged();

        getSupportLoaderManager().restartLoader(ALBUM_LOADER, null, this);
        getSupportLoaderManager().restartLoader(ARTIST_LOADER, null, this);
        getSupportLoaderManager().restartLoader(SONG_LOADER, null, this);

        return true;
    }

    public void navigateToAlbum(Bundle bundle){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Constants.LAUNCH_ALBUM_FRAGMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void navigateToArtist(Bundle bundle){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Constants.LAUNCH_ARTIST_FRAGMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void createDeleteDialog(String name, ArrayList<Song> temp, ArrayList<Integer> pos){

        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, name, "set" , context, pos);
        deleteDialog.deleteInterFace = adapterForSearch;
        deleteDialog.show(getSupportFragmentManager(), Constants.TAG);
    }


    public void createPlaylistDialog(ArrayList<Song> temp){
        ArrayList<Long> songId = new ArrayList<>();

        for (int i=0; i < temp.size() ;i++){
            songId.add(temp.get(i).getID());
        }
        PlaylistDialog playlistDialog = new PlaylistDialog();

        MusicHelper musicHelper = new MusicHelper(context, contentResolver);
        musicHelper.songId = songId;
        musicHelper.songList = temp;
        playlistDialog.playlistCallback = musicHelper;
        playlistDialog.show(getSupportFragmentManager(), Constants.TAG);
    }

    public void createDetailsDialog(String path){
        DetailsDialog detailsDialog = new DetailsDialog();
        detailsDialog.init(path);
        detailsDialog.show(getSupportFragmentManager(), Constants.TAG);
    }

}
