package reverblabs.apps.aura.ui.fragments.playlist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.adapters.playlist.AdapterForPlaylists;
import reverblabs.apps.aura.dialogs.PlaylistInputDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.interfaces.PlaylistCallback;
import reverblabs.apps.aura.model.Playlist;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.viewModelLoaders.PlaylistViewModel;

public class PlaylistsFragment extends BaseFragment implements View.OnClickListener,
        PlaylistCallback{


    AdapterForPlaylists adapterForPlaylists;

    Playlist playlist;

    FloatingActionButton createPlaylist;

    private PlaylistViewModel playlistViewModel;

    public PlaylistsFragment() {
    }

    MainActivityCallback mainActivityCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            mainActivityCallback =(MainActivityCallback) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        playlist = new Playlist();

        //int LOADER_ID = 0;

       // getLoaderManager().initLoader(LOADER_ID, null, this);
    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
//
//        String projection[] = {MediaStore.Audio.Playlists._ID,
//                MediaStore.Audio.Playlists.NAME};
//
//
//        return new CursorLoader(getActivity(), uri, projection, null, null, "Lower(" + MediaStore.Audio.Playlists.NAME+ ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForPlaylists.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForPlaylists.swapCursor(null);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        playlistViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view_playlists);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterForPlaylists = new AdapterForPlaylists(getContext(), null, this);

        RecyclerView.Adapter mAdapter = adapterForPlaylists;

        mRecyclerView.setAdapter(mAdapter);


        final Observer<Cursor> playlistsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForPlaylists.swapCursor(cursor);
            }
        };

        playlistViewModel.getPlaylistsCursor(getContext()).observe(this, playlistsObserver);

        createPlaylist = rootView.findViewById(R.id.createPlaylistButton);
        createPlaylist.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view){
        if(view == createPlaylist){
            PlaylistInputDialog playlistInputDialog = new PlaylistInputDialog();
            playlistInputDialog.initPlaylistInputDialog(this);
            playlistInputDialog.show(getFragmentManager(), Constants.TAG);

        }
    }

    public void createFragment(Bundle bundle){
        mainActivityCallback.createPlaylistFragment(bundle);
    }

    @Override
    public void onCreateNewPlaylist(String name){
        Playlist playlist = new Playlist();
        long pId = playlist.createNewPlaylist(getContext().getContentResolver(), name);
        adapterForPlaylists.notifyNewPlaylistCreated(pId, name);
    }

    @Override
    public void onAddToExistingPlaylist(long playlistId, String name){
    }

    public ArrayList<Integer> getPositionOfSelectedItems(){
        return null;
    }

    public ArrayList<Song> getActionModeSelectedItems(){
        return null;
    }

    public DeleteInterFace getDeleteInterface(){
        return null;
    }

    public void clearActionMode(){

    }
}
