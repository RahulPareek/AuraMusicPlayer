package reverblabs.apps.aura.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.playback.PlaybackManager;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.ui.adapters.AlbumArtPagerAdapter;
import reverblabs.apps.aura.interfaces.FragmentToActivity;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class MusicPlayingFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager album_art_pager;
    private ImageView back;
    private ImageView play_or_pause;
    private ImageView forward;
    private ImageView add_to_playlist;
    private ImageView repeat;
    private ImageView shuffle;
    private SeekBar seekBar;
    private TextView startduration;
    private TextView endDuration;
    private boolean musicshuffleMode;

    private AlbumArtPagerAdapter albumArtPagerAdapter;

    private Toolbar toolbar;

    private MediaControllerCompat mediaController;

    private FloatingActionButton queue;

    private Handler handler;

    public FragmentToActivity mCallBack;

    public MusicPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_music_playing, container, false);

        album_art_pager =  rootView.findViewById(R.id.album_art_pager);


        back =  rootView.findViewById(R.id.back);
        play_or_pause = rootView.findViewById(R.id.play_or_pause);
        forward = rootView.findViewById(R.id.forward);

        repeat = rootView.findViewById(R.id.repeat);
        shuffle = rootView.findViewById(R.id.shuffle);

        seekBar = rootView.findViewById(R.id.seekBar);

        queue = rootView.findViewById(R.id.queue);

        back.setOnClickListener(this);
        play_or_pause.setOnClickListener(this);
        forward.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        queue.setOnClickListener(this);

        back.setImageResource(R.drawable.back);
        forward.setImageResource(R.drawable.forward);

        setRepeatIcon();
        shuffle.setImageResource(R.drawable.shuffle);

        startduration = rootView.findViewById(R.id.start_duration);
        endDuration = rootView.findViewById(R.id.end_duration);

        toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getContext()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.navigation_back);
            actionBar.setTitle("");
        }



        if(mediaController != null) {
            setImagesAndTitle(mediaController.getMetadata());
            setPlayOrPause(mediaController.getPlaybackState());
        }
        return rootView;
    }


    public void registerMediaController(){

        mediaController = MediaControllerCompat.getMediaController(getActivity());
            mediaController.registerCallback(mediaControllerCallback);
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mCallBack = (FragmentToActivity) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        createAndSetAdapter();
    }

    private void createAndSetAdapter(){
        albumArtPagerAdapter = new AlbumArtPagerAdapter(getChildFragmentManager());
        albumArtPagerAdapter.notifyDataSetChanged();
        album_art_pager.setAdapter(albumArtPagerAdapter);
        album_art_pager.setCurrentItem(QueueManager.getCurrentQueueIndex());
        album_art_pager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {

        int nowPlayingPosition = QueueManager.getCurrentQueueIndex();

        if (position - nowPlayingPosition == 1) {
            mediaController.getTransportControls().skipToNext();
        } else if (position - nowPlayingPosition == -1) {
            mediaController.getTransportControls().skipToPrevious();
        }


    }


    public void setImagesAndTitle(MediaMetadataCompat mediaMetadataCompat){

        album_art_pager.setCurrentItem(QueueManager.getCurrentQueueIndex());

        endDuration.setText(MusicHelper.getDurationInSeconds(getContext(),
                mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) /1000));

        initializeSeekBar();
    }

    public void setPlayOrPause(PlaybackStateCompat playbackState){
        if(playbackState.getState() == PlaybackStateCompat.STATE_PLAYING){

            play_or_pause.setImageResource(R.drawable.pause);

        }
        else {
            play_or_pause.setImageResource(R.drawable.play);
        }
    }

    private void setRepeatIcon(){
        int musicrepeatMode = SharedPrefsFile.getRepeatMode(getContext());

        if(musicrepeatMode == 0) {

            repeat.setImageResource(R.drawable.repeat_off);
        }
        else if (musicrepeatMode == 1){
            repeat.setImageResource(R.drawable.repeat_once);
        }
        else if (musicrepeatMode == 2){
            repeat.setImageResource(R.drawable.repeat);

        }
    }

    public void initializeSeekBar(){
        seekBar.setMax(QueueManager.getCurrentSong().Duration/1000);
        handler = new Handler();


        if(getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (QueueManager.getCurrentSong() != null && getContext() != null) {
                    int currentPosition =(int) PlaybackManager.getPosition()/1000;
                    if (mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING
                            || mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
                        seekBar.setProgress(currentPosition);
                        startduration.setText(MusicHelper.getDurationInSeconds(getContext(),
                                currentPosition));
                    }
                    else {
                        int pos = SharedPrefsFile.getMediaPlayerPosition(getContext());
                        seekBar.setProgress(pos/1000);
                        startduration.setText(MusicHelper.getDurationInSeconds(getContext(),
                                pos/1000));
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar mseekBar){}

            @Override
            public void onStartTrackingTouch(SeekBar mseekBar){}

            @Override
            public void onProgressChanged(SeekBar mseekbar, int progress, boolean fromUser){
                if(mediaController != null && fromUser){
                    mediaController.getTransportControls().seekTo(progress*1000);
                }
            }
        });
    }



    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            setImagesAndTitle(metadata);

            super.onMetadataChanged(metadata);

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {

            setPlayOrPause(state);
            super.onPlaybackStateChanged(state);
        }
    };


    public void onClick(View view) {
        Context context = getContext();
        musicshuffleMode = SharedPrefsFile.getShuffleMode(context);

        if (view == back) {
            mCallBack.issueBroadcast(Constants.BROADCAST_BACK);
        }

        else if (view == play_or_pause) {
            PlaybackStateCompat playbackStateCompat = mediaController.getPlaybackState();
            if(playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING){
                mediaController.getTransportControls().pause();
            }
            else {
                mediaController.getTransportControls().play();
            }
        }

        else if (view == forward) {
            mediaController.getTransportControls().skipToNext();
        }

        else if(view==shuffle){

            mCallBack.issueBroadcast(Constants.BROADCAST_SHUFFLE);

            if(!musicshuffleMode){
                musicshuffleMode = true;
                SharedPrefsFile.setShuffleMode(context, musicshuffleMode);
                Toast.makeText(context,R.string.shuffle_mode_on,Toast.LENGTH_SHORT ).show();
            }

            else {
                musicshuffleMode = false;
                SharedPrefsFile.setShuffleMode(context, musicshuffleMode);
                Toast.makeText(context,R.string.shuffle_mode_off,Toast.LENGTH_SHORT ).show();
            }

            createAndSetAdapter();
        }

        else if(view==repeat){

            int musicrepeatMode = SharedPrefsFile.getRepeatMode(context);

            if(musicrepeatMode == 0) {
                musicrepeatMode = 1;
                SharedPrefsFile.setRepeatMode(context, musicrepeatMode);
                repeat.setImageResource(R.drawable.repeat_once);
                Toast.makeText(context, R.string.repeat_this_song, Toast.LENGTH_SHORT).show();
            }
            else if (musicrepeatMode == 1){
                musicrepeatMode = 2;
                SharedPrefsFile.setRepeatMode(context, musicrepeatMode);
                repeat.setImageResource(R.drawable.repeat);
                Toast.makeText(context, R.string.repeat_all_songs, Toast.LENGTH_SHORT).show();
            }
            else if (musicrepeatMode == 2){
                musicrepeatMode = 0;
                SharedPrefsFile.setRepeatMode(context, musicrepeatMode);
                repeat.setImageResource(R.drawable.repeat_off);
                Toast.makeText(context, R.string.repeat_mode_off, Toast.LENGTH_SHORT).show();
            }

            createAndSetAdapter();
        }

        else if(view == queue){
            mCallBack.onQueueMethodClicked();
        }

        else if(view == add_to_playlist){
            ArrayList<Song> temp = new ArrayList<>();
           temp.add(QueueManager.getCurrentSong());
            mCallBack.createPlaylistDialog(temp);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (albumArtPagerAdapter != null) {
            albumArtPagerAdapter.notifyDataSetChanged();
        }
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
        }
    }


}
