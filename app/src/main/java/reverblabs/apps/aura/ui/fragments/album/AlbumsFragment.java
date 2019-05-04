package reverblabs.apps.aura.ui.fragments.album;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.album.AdapterForAlbums;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.AlbumsViewModel;

public class AlbumsFragment extends BaseFragment {

    AdapterForAlbums adapterForAlbums;

    private AlbumsViewModel albumsViewModel;


    public AlbumsFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //int LOADER_ID = 0;

        //getLoaderManager().initLoader(LOADER_ID, null, this);
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {BaseColumns._ID,
//                MediaStore.Audio.Albums.ALBUM,
//                MediaStore.Audio.Albums.ARTIST,
//                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
//                MediaStore.Audio.Albums.FIRST_YEAR};
//
//        return new CursorLoader(getActivity(), uri, projection, null, null,"Lower(" + MediaStore.Audio.Albums.ALBUM + ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForAlbums.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForAlbums.swapCursor(null);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        albumsViewModel = ViewModelProviders.of(this).get(AlbumsViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);

        Context context = getContext();

        FastScrollRecyclerView recyclerView = rootView.findViewById(R.id.recycler);

        if (MusicHelper.isTablet(context)){
            if (MusicHelper.isLandscape(context)){
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 6);
                recyclerView.setLayoutManager(layoutManager);
            }else {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
                recyclerView.setLayoutManager(layoutManager);
            }
        }else if (MusicHelper.isLandscape(context)){
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
            recyclerView.setLayoutManager(layoutManager);
        }
        else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(layoutManager);
        }

        adapterForAlbums = new AdapterForAlbums(context.getContentResolver(), getActivity(), null, this);
        RecyclerView.Adapter adapter = adapterForAlbums;
        recyclerView.setAdapter(adapter);

        final Observer<Cursor> albumsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForAlbums.swapCursor(cursor);
            }
        };

        albumsViewModel.getAlbumsCursor(context).observe(this, albumsObserver);

        return rootView;
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return null;
    }

    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return null;
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return null;
    }

    @Override
    public void clearActionMode(){}

}
