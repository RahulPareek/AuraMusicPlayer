package reverblabs.apps.aura.ui.fragments.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.album.AdapterForAlbumAllSongs;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.fragments.BaseFragment;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.viewModelLoaders.AlbumSongsViewModel;

public class AlbumDetailFragment extends BaseFragment implements View.OnClickListener{

    private Context context;

    private FloatingActionButton floatingActionButton;

    private ImageView album_art;
    private TextView album_name;
    private TextView artist_name;
    private TextView album_year;
    private TextView no_of_songs;

    private long albumId;
    private String albumName;
    private String albumArtist;
    private int albumNoOfSongs;
    private String albumYear;

    AdapterForAlbumAllSongs adapterForAlbumAllSongs;

    private AlbumSongsViewModel albumSongsViewModel;

    //private ImageFetcher imageFetcher;

    Toolbar toolbar;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //int LOADER_ID = 0;

       // getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        setRetainInstance(true);

        albumSongsViewModel = ViewModelProviders.of(this).get(AlbumSongsViewModel.class);

        //imageFetcher = Utils.getImageFetcher(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_album_detail, container, false);

        Bundle bundle = getArguments();
        albumId = bundle.getLong("albumid");
        albumName = bundle.getString("album");
        albumArtist = bundle.getString("artist");
        albumYear = bundle.getString("year");
        albumNoOfSongs = bundle.getInt("count");

        RecyclerView mRecyclerView=rootView.findViewById(R.id.albums_detail_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);

        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterForAlbumAllSongs = new AdapterForAlbumAllSongs(getActivity(),
                null, this);
        RecyclerView.Adapter mAdapter = adapterForAlbumAllSongs;

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        final Observer<Cursor> songsObserver = new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapterForAlbumAllSongs.swapCursor(cursor);
                setResources();
            }
        };

        albumSongsViewModel.getSongsCursor(context, albumName).observe(this,
                songsObserver);


        toolbar =  rootView.findViewById(R.id.toolbar_album);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.navigation_back);
        }

        album_art =  rootView.findViewById(R.id.album_detail_album_art);

        album_name =  rootView.findViewById(R.id.album_detail_album_name);
        artist_name =  rootView.findViewById(R.id.album_detail_artist_name);
        album_year =  rootView.findViewById(R.id.album_detail_album_year);
        no_of_songs =  rootView.findViewById(R.id.album_detail_no_of_songs);


        floatingActionButton =  rootView.findViewById(R.id.albumfloatingButton);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.search_btn, menu);
    }



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
//        String selection =  MediaStore.Audio.Media.ALBUM + "=?";
//
//        String[] selectionArgs = {albumName};
//
//        return new CursorLoader(context, uri, projection, selection, selectionArgs, "Lower(" + MediaStore.Audio.Media.TITLE + ")ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader , Cursor cursor){
//        adapterForAlbumAllSongs.swapCursor(cursor);
//        setResources();
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader){
//        adapterForAlbumAllSongs.swapCursor(null);
//    }


    private void setResources(){

        GlideApp.with(context)
                .load(new AlbumImage(albumArtist, albumName, (int) albumId))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.LARGE_SIZE, Constants.LARGE_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(album_art);

        //imageFetcher.loadImage(albumId, albumName, albumArtist, "Album", false, album_art, Constants.LARGE);

        album_name.setText(albumName);
        artist_name.setText(albumArtist);

        albumNoOfSongs = adapterForAlbumAllSongs.getDatasetSize();

        if(albumNoOfSongs ==1) {
            no_of_songs.setText(Integer.toString(albumNoOfSongs) + " " + getString(R.string.song));
        }
        else{
            no_of_songs.setText(Integer.toString(albumNoOfSongs) + " " + getString(R.string.songs));
        }
        album_year.setText(albumYear);

        floatingActionButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view == floatingActionButton){
            ArrayList<Song> arrayList = adapterForAlbumAllSongs.getAlbumSongsDataSet();
            //mainActivityCallback.handleShuffleFromFloatingButtom(arrayList);
        }
    }


    @Override
    public void clearActionMode(){
        adapterForAlbumAllSongs.actionMode = false;
        adapterForAlbumAllSongs.clearSelections();
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return adapterForAlbumAllSongs.getPositionOfSelectedItems();
    }


    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return adapterForAlbumAllSongs.getActionModeSelectedItems();
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return adapterForAlbumAllSongs;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
