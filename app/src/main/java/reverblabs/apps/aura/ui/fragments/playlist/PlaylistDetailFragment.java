package reverblabs.apps.aura.ui.fragments.playlist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.playlist.AdapterForPlaylistAllSongs;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;
import reverblabs.apps.aura.viewModelLoaders.PlaylistRecentCreatedViewModel;
import reverblabs.apps.aura.viewModelLoaders.UserPlaylistViewModel;

public class PlaylistDetailFragment extends BaseFragment {

    AdapterForPlaylistAllSongs adapterForPlaylistAllSongs;

    String playlistName;
    long playlistId;


    int RECENTLY_ADDED_ID = 0;
    int USER_CREATED_PLAYLIST = 1;

    private PlaylistRecentCreatedViewModel playlistRecentCreatedViewModel;
    private UserPlaylistViewModel userPlaylistViewModel;

    public PlaylistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        playlistName = bundle.getString(Constants.PLAYLIST_NAME);
        playlistId = bundle.getLong(Constants.PLAYLIST_ID);

        playlistRecentCreatedViewModel = ViewModelProviders.of(this).get(PlaylistRecentCreatedViewModel.class);
        userPlaylistViewModel = ViewModelProviders.of(this).get(UserPlaylistViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        Context context = getContext();

        RecyclerView mRecyclerView=rootView.findViewById(R.id.recycler_view_default_playlist);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(context);

        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterForPlaylistAllSongs = new AdapterForPlaylistAllSongs(getActivity(),
                context.getContentResolver(), this, null, playlistName, playlistId);

        if(playlistName.equals(getString(R.string.recently_added))) {

            //getLoaderManager().initLoader(RECENTLY_ADDED_ID, null, this);
            final Observer<Cursor> songsObserver = new Observer<Cursor>() {
                @Override
                public void onChanged(Cursor cursor) {
                    adapterForPlaylistAllSongs.swapCursor(cursor, playlistName);
                }
            };

            playlistRecentCreatedViewModel.getSongsCursor(context).observe(this, songsObserver);

        }

        else if(playlistName.equals(getString(R.string.favourites))){
            ArrayList<Song> list = SharedPrefsFile.getFavouritesPlaylist(context);
            adapterForPlaylistAllSongs.setDataSet(list);
        }

        else if (playlistName.equals(getString(R.string.recently_played))){
            ArrayList<Song> list = SharedPrefsFile.getRecentlyPlayedPlaylist(context);
            adapterForPlaylistAllSongs.setDataSet(list);
        }
        else {

           // getLoaderManager().initLoader(USER_CREATED_PLAYLIST, null, this);
            final Observer<Cursor> songsObserver = new Observer<Cursor>() {
                @Override
                public void onChanged(Cursor cursor) {
                    adapterForPlaylistAllSongs.swapCursor(cursor, playlistName);
                }
            };

            userPlaylistViewModel.getSongsCursor(context, playlistId).observe(this, songsObserver);
        }

        RecyclerView.Adapter mAdapter = adapterForPlaylistAllSongs;

        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.navigation_back);
            actionBar.setTitle(playlistName);
        }

        return rootView;
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//
//        if (id == RECENTLY_ADDED_ID) {
//
//            String projection[] = {MediaStore.Audio.Media._ID,
//                    MediaStore.Audio.Media.DATA,
//                    MediaStore.Audio.Media.ARTIST,
//                    MediaStore.Audio.Media.ARTIST_ID,
//                    MediaStore.Audio.Media.ALBUM,
//                    MediaStore.Audio.Media.ALBUM_ID,
//                    MediaStore.Audio.Media.TITLE,
//                    MediaStore.Audio.Media.DURATION,
//                    MediaStore.Audio.Media.YEAR,
//                    MediaStore.Audio.Media.DATE_ADDED};
//
//            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//            final StringBuilder stringBuilder = new StringBuilder();
//
//            stringBuilder.append(MediaStore.Audio.Media.IS_MUSIC + "=1");
//            stringBuilder.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + " > " + ((System.currentTimeMillis() / 1000) - 60 * 60 * 24 * 7));
//
//            return new CursorLoader(context, uri, projection, stringBuilder.toString(), null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
//        }
//        else if (id == USER_CREATED_PLAYLIST){
//
//            String projection[] = {MediaStore.Audio.Playlists.Members.AUDIO_ID,
//                    MediaStore.Audio.Playlists.Members.DATA,
//                    MediaStore.Audio.Playlists.Members.ARTIST,
//                    MediaStore.Audio.Playlists.Members.ARTIST_ID,
//                    MediaStore.Audio.Playlists.Members.ALBUM,
//                    MediaStore.Audio.Playlists.Members.ALBUM_ID,
//                    MediaStore.Audio.Playlists.Members.TITLE,
//                    MediaStore.Audio.Playlists.Members.DURATION,
//                    MediaStore.Audio.Playlists.Members.YEAR};
//
//            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
//
//
//            return new CursorLoader(context, uri, projection, null, null, null);
//        }
//
//        else {
//            return null;
//        }
//
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForPlaylistAllSongs.swapCursor(cursor, playlistName);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForPlaylistAllSongs.swapCursor(null, playlistName);
//    }


    @Override
    public void clearActionMode(){
        adapterForPlaylistAllSongs.actionMode = false;
        adapterForPlaylistAllSongs.clearSelections();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return adapterForPlaylistAllSongs.getPositionOfSelectedItems();
    }


    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return adapterForPlaylistAllSongs.getActionModeSelectedItems();
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return adapterForPlaylistAllSongs;
    }
}
