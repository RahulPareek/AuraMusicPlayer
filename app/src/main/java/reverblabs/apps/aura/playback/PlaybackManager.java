package reverblabs.apps.aura.playback;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.PlaybackManagerCallback;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class PlaybackManager{

    private static final String PLAYBACK_MANAGER = "Playback Manager";
    public static final String META_CHANGED = "Meta Changed";
    public static final String PLAYBACK_STATE_CHANGED = "Playback State Changed";

    private AudioManager audioManager;

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;

    private static final int AUDIO_NO_FOCUS = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;

    private int currentAudioFocusState = AUDIO_NO_FOCUS;

    private static SimpleExoPlayer exoPlayer;
    private ExoPlayerEventListener exoPlayerEventListener = new ExoPlayerEventListener();

    private PlaybackManagerCallback playbackManagerCallback;

    private Context context;

    private Long mediaID;

    public PlaybackManager(Context cx, PlaybackManagerCallback callback){
        context = cx;
        playbackManagerCallback = callback;
    }


    private void prepareExoPlayer(){
        if(exoPlayer == null && context != null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context,
                    new DefaultTrackSelector());

            exoPlayer.addListener(exoPlayerEventListener);
        }

        if(mediaID == null || !QueueManager.getCurrentSong().getID().equals(mediaID)) {

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build();

            exoPlayer.setAudioAttributes(audioAttributes, true);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    context, Util.getUserAgent(context,
                    context.getString(R.string.app_name)), null);

            mediaID = QueueManager.getCurrentSong().getID();

            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , mediaID);

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);

            exoPlayer.prepare(mediaSource);
        }
        playbackManagerCallback.onPrepared();
    }



    public void setPlayWhenReady(Boolean value){
        if(exoPlayer !=null) {
            exoPlayer.setPlayWhenReady(value);
        }
    }


    public void play(){
        setPlayWhenReady(true);
        prepareExoPlayer();
    }



    public void pause(){
        if(exoPlayer != null){
            exoPlayer.setPlayWhenReady(false);
            playbackManagerCallback.onPause();
        }
    }

    public void playOrPause(){
        if(exoPlayer != null && isPlaying()){
            pause();
        }else{
            play();
        }
    }

    public void playPrevious(){
        if(QueueManager.hasPrevious()){
            if(getPosition() < 3000){
                QueueManager.updateToPreviousSong();
                play();
            }
            else{
                seekTo(0);
            }
        }
        else{
            seekTo(0);
        }
    }


    public void playNext(){
        if(QueueManager.hasNext()){
            QueueManager.updateToNextSong();
            play();
        }
        else{
            if(SharedPrefsFile.getRepeatMode(context) == 2){
                QueueManager.setCurrentQueueIndex(0);
                play();
            }
            else {
                pause();
            }
        }
    }



    public boolean isPlaying(){
        if(exoPlayer != null && exoPlayer.getPlayWhenReady()){
            return true;
        }

        return false;
    }



    public void seekTo(long position){
        exoPlayer.seekTo(position);
    }

    public void onCompletion(){
        playNext();
    }


    public static long getPosition(){
        if(exoPlayer != null){
            return exoPlayer.getCurrentPosition();
        }

        return 0;
    }


    public void releaseResources(){
        if(exoPlayer != null){
            exoPlayer.stop(false);
            exoPlayer.release();
            exoPlayer.removeListener(exoPlayerEventListener);
            exoPlayer = null;
        }
    }


    private final class ExoPlayerEventListener implements Player.EventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState){

            switch (playbackState){
                case Player.STATE_READY:
                    Log.i("Exo Player State Ready", "State Ready");
                    break;

                case Player.STATE_ENDED:
                    Log.i("State ended", "Exo player state ended");
                    onCompletion();
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error){
            String message;

            switch (error.type){
                case ExoPlaybackException.TYPE_SOURCE:
                    message = error.getSourceException().getMessage();
                    break;

                case ExoPlaybackException.TYPE_RENDERER:
                    message = error.getRendererException().getMessage();
                    break;

                case ExoPlaybackException.TYPE_UNEXPECTED:
                    message = error.getUnexpectedException().getMessage();
                    break;

                default:
                    message = "Unknown" + error;
            }

            Log.i(PLAYBACK_MANAGER, message);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }
    }

}
