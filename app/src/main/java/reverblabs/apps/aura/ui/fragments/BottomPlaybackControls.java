package reverblabs.apps.aura.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.ui.activities.MusicPlayingActivity;
import reverblabs.apps.aura.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomPlaybackControls extends Fragment implements View.OnClickListener {


    private ImageView albumArt;
    private TextView songName;
    private TextView artistName;
    private ImageView playPause;


    public BottomPlaybackControls() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_playback_controls, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MusicPlayingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        albumArt = view.findViewById(R.id.album_art);
        songName = view.findViewById(R.id.first_line);
        artistName = view.findViewById(R.id.second_line);
        playPause = view.findViewById(R.id.bottom_bar_play_pause);

        playPause.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view){
        if(view == playPause){
            MediaControllerCompat mediaController = MediaControllerCompat.
                    getMediaController(getActivity());

            PlaybackStateCompat playbackState = mediaController.getPlaybackState();

            if(playbackState.getState() == PlaybackStateCompat.STATE_PLAYING){
                mediaController.getTransportControls().pause();
            }
            else{
                mediaController.getTransportControls().play();
            }
        }
    }


    @Override
    public void onStart(){
        super.onStart();

        if(getActivity() != null) {

            MediaControllerCompat mediaController = MediaControllerCompat.
                    getMediaController(getActivity());

            if (mediaController != null) {
                onConnected();
            }
        }
    }



    @Override
    public void onStop(){
        super.onStop();
        if(getActivity() != null) {
            MediaControllerCompat mediaController = MediaControllerCompat.
                    getMediaController(getActivity());

            if (mediaController != null) {
                mediaController.unregisterCallback(callback);
            }
        }
    }


    public void onConnected(){
        MediaControllerCompat mediaController = MediaControllerCompat.
                getMediaController(getActivity());

        if(mediaController !=  null){
            onMetaDataChanged(mediaController.getMetadata());
            onPlaybackChanged(mediaController.getPlaybackState());

            mediaController.registerCallback(callback);
        }
    }


    private void onMetaDataChanged(MediaMetadataCompat metadata){
        if(metadata != null && getActivity() != null) {

            GlideApp.with(getActivity())
                    .load(new AlbumImage(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
                            Integer.parseInt(metadata.getString(MediaMetadataCompat.
                                    METADATA_KEY_MEDIA_ID))))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(new RequestOptions().
                            override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                    .transition(GenericTransitionOptions.with(R.anim.fade_in))
                    .placeholder(R.drawable.album_art)
                    .error(R.drawable.album_art)
                    .into(albumArt);

            songName.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            artistName.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        }
    }

    private void onPlaybackChanged(PlaybackStateCompat playbackState){
        if(playbackState != null && getActivity() != null) {
            if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                playPause.setImageResource(R.drawable.pause);
            } else {
                playPause.setImageResource(R.drawable.play);
            }
        }
    }

    private final MediaControllerCompat.Callback callback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            onPlaybackChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            onMetaDataChanged(metadata);
        }
    };


}
