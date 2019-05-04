package reverblabs.apps.aura.ui.fragments.artist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.artist.ArtistImage;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.album.AdapterForAllAlbumsOfArtist;
import reverblabs.apps.aura.ui.adapters.artist.AdapterForArtistAllSongs;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.ArtistAlbumsViewModel;
import reverblabs.apps.aura.viewModelLoaders.ArtistSongsViewModel;

public class ArtistDetailFragment extends BaseFragment implements View.OnClickListener{

    ImageView album_art;
    ImageView artist_info;

    FloatingActionButton floatingActionButton;

    public ActionMode actionMode;

    TextView name;

    long artistId;
    String artistName;

    AdapterForArtistAllSongs adapterForArtistAllSongs;
    AdapterForAllAlbumsOfArtist adapterForAllAlbumsOfArtist;

    public ContentResolver contentResolver;
    public Context context;

    private ArtistAlbumsViewModel artistAlbumsViewModel;
    private ArtistSongsViewModel artistSongsViewModel;

    private MainActivityCallback mainActivityCallback;

    //private ImageFetcher imageFetcher;


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

//        getLoaderManager().initLoader(0, null, this);
//        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        contentResolver= context.getContentResolver();

        artistAlbumsViewModel = ViewModelProviders.of(this).get(ArtistAlbumsViewModel.class);
        artistSongsViewModel = ViewModelProviders.of(this).get(ArtistSongsViewModel.class);

        //imageFetcher = Utils.getImageFetcher(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist_detail, container, false);


        Bundle bundle = getArguments();
        artistName = bundle.getString("artist");
        artistId = bundle.getLong("id");


        RecyclerView mRecyclerViewAlbums = rootView.findViewById(R.id.recycler_view_artist_all_albums);
        RecyclerView.LayoutManager mLayoutManagerAlbums = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewAlbums.setLayoutManager(mLayoutManagerAlbums);

        adapterForAllAlbumsOfArtist = new AdapterForAllAlbumsOfArtist(getActivity(), null, this);
        RecyclerView.Adapter mAdpaterAlbums = adapterForAllAlbumsOfArtist;
        mRecyclerViewAlbums.setAdapter(mAdpaterAlbums);

        mRecyclerViewAlbums.setNestedScrollingEnabled(false);


        RecyclerView mRecyclerViewSongs= rootView.findViewById(R.id.recycler_view_artist_all_songs);
        mRecyclerViewSongs.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManagerSongs=new LinearLayoutManager(context);

        mRecyclerViewSongs.setLayoutManager(mLayoutManagerSongs);

        adapterForArtistAllSongs = new AdapterForArtistAllSongs(getActivity(),
                null, this);
        RecyclerView.Adapter mAdapterSongs = adapterForArtistAllSongs;

        final Observer<Cursor> albumsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForAllAlbumsOfArtist.swapCursor(cursor);
            }
        };

        artistAlbumsViewModel.getArtistAlbumsCursor(context, artistId).observe(this,
                albumsObserver);

        final Observer<Cursor> songsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForArtistAllSongs.swapCursor(cursor);
            }
        };

        artistSongsViewModel.getArtistSongsCursor(context, artistName).observe(this,
                songsObserver);

        mRecyclerViewSongs.setAdapter(mAdapterSongs);

        mRecyclerViewSongs.setNestedScrollingEnabled(false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar_artist);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.navigation_back);
        }

        album_art = rootView.findViewById(R.id.album_art);
        artist_info = rootView.findViewById(R.id.artist_info);

        name = rootView.findViewById(R.id.artist_name);
        name.setText(artistName);

        floatingActionButton = rootView.findViewById(R.id.artistfloatingButton);

        setResources();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.search_btn, menu);
    }

//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//       if(id==0){
//           String[] projection = {BaseColumns._ID,
//                   MediaStore.Audio.Artists.Albums.ALBUM,
//                   MediaStore.Audio.Artists.Albums.ARTIST,
//                   MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
//                   MediaStore.Audio.Artists.Albums.FIRST_YEAR};
//
//           return new CursorLoader(context, MediaStore.Audio.Artists.Albums.getContentUri("external",artistId), projection,null, null, "Lower(" + MediaStore.Audio.Artists.Albums.ALBUM + ")ASC");
//
//       }
//        else {
//           String projection[] = {MediaStore.Audio.Media._ID,
//                   MediaStore.Audio.Media.DATA,
//                   MediaStore.Audio.Media.ARTIST,
//                   MediaStore.Audio.Media.ARTIST_ID,
//                   MediaStore.Audio.Media.ALBUM,
//                   MediaStore.Audio.Media.ALBUM_ID,
//                   MediaStore.Audio.Media.TITLE,
//                   MediaStore.Audio.Media.DURATION,
//                   MediaStore.Audio.Media.YEAR};
//
//           String selection =  MediaStore.Audio.Artists.ARTIST+ "=?";
//
//           String[] selectionArgs = {artistName};
//
//           return new CursorLoader(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC");
//       }
//    }

//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        int id = loader.getId();
//        if(id==0) {
//            adapterForAllAlbumsOfArtist.swapCursor(cursor);
//        }
//        else {
//            adapterForArtistAllSongs.swapCursor(cursor);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        int id = loader.getId();
//        if(id==0){
//            adapterForAllAlbumsOfArtist.swapCursor(null);
//        }
//        else {
//            adapterForArtistAllSongs.swapCursor(null);
//        }
//    }

    private void setResources(){

        floatingActionButton.setOnClickListener(this);
        artist_info.setOnClickListener(this);

        GlideApp.with(context)
                .load(new ArtistImage(artistName, (int) artistId))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.LARGE_SIZE, Constants.LARGE_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(album_art);

        //imageFetcher.loadImage(artistId, null, artistName, "Artist", false, album_art, Constants.LARGE);

        artist_info.setImageResource(R.drawable.artist_info);
    }


    @Override
    public void onClick(View view) {
     if (view == floatingActionButton){
            ArrayList<Song> arrayList = adapterForArtistAllSongs.getArtistAllSongsDataset();
           // HelperClass.auraMusicService.handleShuffleFromFloatingButtom(arrayList);
     }
        else if (view == artist_info){
         mainActivityCallback.createArtistBioFragment(artistName);
     }

    }


    @Override
    public void clearActionMode(){
        adapterForArtistAllSongs.actionMode = false;
        adapterForArtistAllSongs.clearSelections();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return adapterForArtistAllSongs.getPositionOfSelectedItems();
    }


    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return adapterForArtistAllSongs.getActionModeSelectedItems();
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return adapterForArtistAllSongs;
    }

}
