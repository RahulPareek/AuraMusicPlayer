package reverblabs.apps.aura.ui.fragments.genre;


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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.adapters.genre.AdapterForGenres;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.GenreViewModel;

public class GenresFragment extends BaseFragment {

    AdapterForGenres adapterForGenres;

    private GenreViewModel genreViewModel;

    public GenresFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //int LOADER_ID = 0;

        //getLoaderManager().initLoader(LOADER_ID, null, this);
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {
//                MediaStore.Audio.Genres._ID,
//                MediaStore.Audio.Genres.NAME};
//
//        return new CursorLoader(getActivity(), uri, projection, null, null, "Lower(" + MediaStore.Audio.Genres.NAME + ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//       adapterForGenres.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForGenres.swapCursor(null);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        genreViewModel = ViewModelProviders.of(this).get(GenreViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_genres, container, false);

        FastScrollRecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager;

        Context context = getContext();

        if (MusicHelper.isTablet(context)){
            layoutManager = new GridLayoutManager(context, 2);
        }else {
            layoutManager = new LinearLayoutManager(context);
        }

        recyclerView.setLayoutManager(layoutManager);


        adapterForGenres = new AdapterForGenres(context, null, this);

        RecyclerView.Adapter adapter = adapterForGenres;
        recyclerView.setAdapter(adapter);


        final Observer<Cursor> genresObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForGenres.swapCursor(cursor);
            }
        };

        genreViewModel.getGenresCursor(context).observe(this, genresObserver);

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
