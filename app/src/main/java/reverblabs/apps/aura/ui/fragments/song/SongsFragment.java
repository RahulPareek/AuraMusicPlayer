package reverblabs.apps.aura.ui.fragments.song;


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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.song.CustomAdapterToListSongs;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.SongsViewModel;

public class SongsFragment extends BaseFragment implements View.OnClickListener{

    private SongsViewModel songsViewModel;

    public SongsFragment() {
    }


    CustomAdapterToListSongs customAdapterToListSongs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        int LOADER_ID = 0;

       // getLoaderManager().initLoader(LOADER_ID, null, this);
     }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//        String projection[] = {MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.ARTIST_ID,
//                MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.ALBUM_ID,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.YEAR};
//
//
//        return new CursorLoader(getActivity(), uri, projection, MediaStore.Audio.Media.IS_MUSIC, null, "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        customAdapterToListSongs.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        customAdapterToListSongs.swapCursor(null);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        songsViewModel = ViewModelProviders.of(this).get(SongsViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);


        FastScrollRecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);


        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.floatingActionButton);

        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);

        customAdapterToListSongs = new CustomAdapterToListSongs(getActivity(),
                null, this);

        RecyclerView.Adapter mAdapter = customAdapterToListSongs;

        recyclerView.setAdapter(mAdapter);

        floatingActionButton.setOnClickListener(this);

        final Observer<Cursor> songsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                customAdapterToListSongs.swapCursor(cursor);
            }
        };

        songsViewModel.getSongsCursor(getContext()).observe(this, songsObserver);

        return rootView;
    }

    @Override
    public void onClick(View view){
        ArrayList<Song> arrayList = customAdapterToListSongs.getDataSet();
       // HelperClass.auraMusicService.handleShuffleFromFloatingButtom(arrayList);
    }


    @Override
    public void clearActionMode(){
        customAdapterToListSongs.actionMode = false;
        customAdapterToListSongs.clearSelections();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return customAdapterToListSongs.getPositionOfSelectedItems();
    }


    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return customAdapterToListSongs.getActionModeSelectedItems();
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return customAdapterToListSongs;
    }

}
