package reverblabs.apps.aura.ui.fragments.genre;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import reverblabs.apps.aura.ui.adapters.genre.AdapterForGenreAllSongs;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.GenreSongsViewModel;

import static reverblabs.apps.aura.R.string.song;


public class GenreDetailFragment extends BaseFragment implements View.OnClickListener{

    ContentResolver contentResolver;
    Context context;

    FloatingActionButton floatingActionButton;

    public ActionMode actionMode;

    ImageView album_art;
    TextView genre_name;
    TextView no_of_songs;

    long genreId;
    String genreName;

    AdapterForGenreAllSongs adapterForGenreAllSongs;

    MainActivityCallback mainActivityCallback;

    private GenreSongsViewModel genreSongsViewModel;

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

        //int LOADER_ID = 0;

        //getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genreSongsViewModel = ViewModelProviders.of(this).get(GenreSongsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_genre_detail, container, false);

        context = getContext();
        contentResolver = context.getContentResolver();

        Bundle bundle = getArguments();
        genreId = bundle.getLong(Constants.GENRE_ID);
        genreName = bundle.getString(Constants.GENRE_NAME);


        RecyclerView mRecyclerView = rootView.findViewById(R.id.genre_detail_recycler_view);

        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(context);

        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterForGenreAllSongs = new AdapterForGenreAllSongs(getActivity(), null, this);

        RecyclerView.Adapter mAdapter = adapterForGenreAllSongs;
        mRecyclerView.setAdapter(mAdapter);


        final Observer<Cursor> songsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForGenreAllSongs.swapCursor(cursor);
                setResources();
            }
        };

        genreSongsViewModel.getSongsCursor(context, genreId).observe(this, songsObserver);

        mRecyclerView.setNestedScrollingEnabled(false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar_genre);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.navigation_back);
        }

        album_art =  rootView.findViewById(R.id.genre_detail_album_art);
        genre_name =  rootView.findViewById(R.id.genre_detail_genre_name);
        no_of_songs = rootView.findViewById(R.id.genre_detail_no_of_songs);


        floatingActionButton = rootView.findViewById(R.id.genrefloatingButton);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater ){

        inflater.inflate(R.menu.search_btn, menu);
    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
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
//        return new CursorLoader(context, MediaStore.Audio.Genres.Members.getContentUri("external",genreId), projection,null,null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForGenreAllSongs.swapCursor(cursor);
//        setResources();
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForGenreAllSongs.swapCursor(null);
//    }


    private void setResources(){
        floatingActionButton.setOnClickListener(this);

        album_art.setImageResource(R.drawable.album_art);

        genre_name.setText(genreName);
        int noOfSongs = adapterForGenreAllSongs.getItemCount();
        if(noOfSongs==1){
            no_of_songs.setText(noOfSongs+ " " + getString(song));
        }
        else {
            no_of_songs.setText(noOfSongs+ " " + getString(R.string.songs));
        }

    }

    @Override
    public void onClick(View view) {
     if (view == floatingActionButton){
            ArrayList<Song> arrayList = adapterForGenreAllSongs.getGenreAllSongDataSet();
          //  HelperClass.auraMusicService.handleShuffleFromFloatingButtom(arrayList);
        }
    }


    @Override
    public void clearActionMode(){
        adapterForGenreAllSongs.actionMode = false;
        adapterForGenreAllSongs.clearSelections();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return adapterForGenreAllSongs.getPositionOfSelectedItems();
    }


    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return adapterForGenreAllSongs.getActionModeSelectedItems();
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return adapterForGenreAllSongs;
    }
}
