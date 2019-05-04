package reverblabs.apps.aura.ui.fragments;


import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.ui.adapters.AdapterQueueFragment;
import reverblabs.apps.aura.interfaces.FragmentToActivity;
import reverblabs.apps.aura.interfaces.OnStartDragListener;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.SimpleItemTouchHelperCallback;

public class QueueFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper itemTouchHelper;

    private ContentResolver resolver;

    private AdapterQueueFragment adapterQueueFragment;

    private View rootView;

    private TextView song_name;
    private TextView artist_name;
    private TextView queue_length;

    ImageView background;
    ImageView albumArt;

    private MediaControllerCompat mediaController;


    public QueueFragment() {
        // Required empty public constructor
    }

    private FragmentToActivity fragmentToActivity;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            fragmentToActivity =(FragmentToActivity) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_queue, container, false);


        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view_queue);

        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterQueueFragment = new AdapterQueueFragment(resolver, getActivity(), this, this);

        RecyclerView.Adapter mAdapter = adapterQueueFragment;

        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapterQueueFragment);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        song_name = rootView.findViewById(R.id.song_name_queue);
        artist_name = rootView.findViewById(R.id.artist_name_queue);
        queue_length = rootView.findViewById(R.id.queue_length);

        background = rootView.findViewById(R.id.now_playing_detail_background_album_art);
        albumArt = rootView.findViewById(R.id.album_art_queue);

        mediaController = MediaControllerCompat.getMediaController(getActivity());
        mediaController.registerCallback(mediaControllerCallback);

        setBackground(MediaControllerCompat.getMediaController(getActivity()).getMetadata());


        int scrollPosition = QueueManager.getCurrentQueueIndex();

        mRecyclerView.scrollToPosition(scrollPosition);

        return rootView;
    }


    public void setQueueSizeText(){
      queue_length.setText(QueueManager.getCurrentQueueIndex() + 1 + "/" + adapterQueueFragment.queueListSize());
    }


    public void setBackground(MediaMetadataCompat metadataCompat){

        Context context = getContext();

        Song nowPlaying = QueueManager.getCurrentSong();

        song_name.setText(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        artist_name.setText(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        queue_length.setText(QueueManager.getCurrentQueueIndex() + 1 + "/" + adapterQueueFragment.queueListSize());

        GlideApp.with(context)
                .load(new AlbumImage(MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        (int) nowPlaying.getAlBumId()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(albumArt);

        GlideApp.with(context)
                .load(new AlbumImage(MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        (int) nowPlaying.getAlBumId()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(background);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder){
        itemTouchHelper.startDrag(viewHolder);
    }



    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if(getActivity() != null) {
                setBackground(metadata);
            }

            super.onMetadataChanged(metadata);

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
        }
    };

    public void createArtistFragment(Bundle bundle){
        fragmentToActivity.navigateToArtist(bundle);
    }

    public void createAlbumFragment(Bundle bundle){
        fragmentToActivity.navigateToAlbum(bundle);
    }

    public void playSong(int pos){
        QueueManager.setCurrentQueueIndex(pos);
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

        if(mediaController != null){
            mediaController.getTransportControls().play();
        }
    }
}
