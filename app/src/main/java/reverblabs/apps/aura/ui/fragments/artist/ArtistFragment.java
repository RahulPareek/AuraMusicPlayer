package reverblabs.apps.aura.ui.fragments.artist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import reverblabs.apps.aura.ui.adapters.artist.AdapterForArtist;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.ArtistViewModel;

public class ArtistFragment extends BaseFragment {

    ContentResolver contentResolver;
    Context context;

    private AdapterForArtist adapterForArtist;

    private ArtistViewModel artistViewModel;

    public ArtistFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //int LOADER_ID = 0;

       // getLoaderManager().initLoader(LOADER_ID, null, this);
    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {
//                MediaStore.Audio.Artists._ID,
//                MediaStore.Audio.Artists.ARTIST,
//                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
//                MediaStore.Audio.Artists.NUMBER_OF_TRACKS};
//
//        return new CursorLoader(getActivity(), uri, projection, null, null, "Lower(" + MediaStore.Audio.Artists.ARTIST + ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForArtist.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForArtist.swapCursor(null);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getContext();
        contentResolver = context.getContentResolver();

        artistViewModel = ViewModelProviders.of(this).get(ArtistViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

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

        adapterForArtist =new AdapterForArtist(contentResolver, getActivity(),
                null, this);

        RecyclerView.Adapter adapter = adapterForArtist;
        recyclerView.setAdapter(adapter);

        final Observer<Cursor> artistObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForArtist.swapCursor(cursor);
            }
        };

        artistViewModel.getArtistsCursor(getContext()).observe(this, artistObserver);

        return rootView;
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
