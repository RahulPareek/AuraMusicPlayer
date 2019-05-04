package reverblabs.apps.aura.playback;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.MediaBrowserServiceCompat;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.appWidgets.WidgetLarge;
import reverblabs.apps.aura.appWidgets.WidgetLargeAlbumArt;
import reverblabs.apps.aura.appWidgets.WidgetSmall;
import reverblabs.apps.aura.glide.CustomTarget;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.PlaybackManagerCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class AuraMusicService extends MediaBrowserServiceCompat implements PlaybackManagerCallback {


    public Equalizer equalizer = null;
    public BassBoost bassBoost = null;
    public PresetReverb presetReverb = null;
    public Virtualizer virtualizer = null;

    private PlaybackManager playbackManager;

    private boolean transientAudioLoss;
    private boolean audioFocusCanDuck;
    private  boolean audioFocusLoss;

    AudioManager audioManager;

    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private PlaybackStateCompat playbackState;

    private MediaControllerCompat mediaController;

    private static long sLastClickTime = 0;
    private static final int DOUBLE_CLICK_DELAY = 600;

    NotificationManager notificationManager;

    private boolean audioNoisyReceiverRegistered;

    IntentFilter audioNoisyReceiverFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private static int sDelayedClicks;

//
//    @Override
//    public IBinder onBind(Intent intent){
//        super.onBind(intent);
//        return bind
//    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(Constants.TAG, "Service OnCreate");
        super.onCreate();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        playbackManager = new PlaybackManager(getApplicationContext(), this);

        initMediaPlayerAndSession();
    }

    public void initMediaPlayerAndSession(){

        Context context = getApplicationContext();

        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(audioBecomingNoisyReceiver, filter);

        ComponentName mediaButtonReceiver = new ComponentName(context,
                MediaButtonReceiver.class);

        mediaSessionCompat = new MediaSessionCompat(context, Constants.TAG,
                mediaButtonReceiver, null);
        setSessionToken(mediaSessionCompat.getSessionToken());

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


        playbackStateBuilder = new PlaybackStateCompat.Builder();

        mediaSessionCompat.setCallback(mediaSessionCallback);

        metadataBuilder = new MediaMetadataCompat.Builder();

        playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_SEEK_TO |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_STOP |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH);

        playbackStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                0, 1.0f ,
                SystemClock.elapsedRealtime());


        try {
            updateSession();
        }
        catch (RemoteException e){
            e.printStackTrace();
        }

//        if (MusicHelper.useAuraEqualizer(context)) {
//
//            equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
//            equalizer.setEnabled(true);
//
//            if (SharedPrefsFile.getEqualizerPresetMode(context) == equalizer.getNumberOfPresets()) {
//
//                ArrayList<Short> values = SharedPrefsFile.getCustomPresetLevels(context);
//
//                if (values != null) {
//
//                    for (short i = 0; i < equalizer.getNumberOfBands(); i++) {
//                        equalizer.setBandLevel(i, values.get(i));
//                    }
//                }
//            }
//            else {
//                equalizer.usePreset(SharedPrefsFile.getEqualizerPresetMode(context));
//            }
//
//            bassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
//            if (bassBoost.getStrengthSupported()) {
//
//
//                bassBoost.setEnabled(true);
//                try {
//                    bassBoost.setStrength(SharedPrefsFile.getBassBoostValue(context));
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            virtualizer = new Virtualizer(0, mediaPlayer.getAudioSessionId());
//            if (virtualizer.getStrengthSupported()) {
//                virtualizer.setEnabled(true);
//                try {
//                    virtualizer.setStrength(SharedPrefsFile.getVirtualizerValue(context));
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            presetReverb = new PresetReverb(0, mediaPlayer.getAudioSessionId());
//            presetReverb.setEnabled(true);
//            try {
//                presetReverb.setPreset(SharedPrefsFile.getPresetReverb(context));
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }

        try {
            notificationManager = new NotificationManager(this, context,
                    mediaSessionCompat);
        }
        catch (RemoteException e){
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver ,
                new IntentFilter(Constants.LOCAL_BROADCAST));
    }

    private void updateSession() throws RemoteException{

        mediaController = new MediaControllerCompat(getApplicationContext(),
                mediaSessionCompat.getSessionToken());

        mediaController.registerCallback(mediaControllerCallback);
    }


    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                 Bundle rootHints){
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result){

        MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder().setMediaId(parentId).build(),
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(mediaItem);

        result.sendResult(mediaItems);
    }


    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {

            notifyAndroidMusic(Constants.PLAYBACK_STATE_CHANGED);
            updateWidget();

            super.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadataCompat){
            updateWidget();
            super.onMetadataChanged(metadataCompat);
        }
    };


    @Override
    public void onPrepared(){
        setMediaSessionMetadata();
        setPlaybackState(PlaybackStateCompat.STATE_PLAYING);

        updateWidget();

        mediaSessionCompat.setActive(true);
        notifyAndroidMusic(Constants.META_CHANGED);

        registerAudioNoisyReceiver();

        Context context = getApplicationContext();
//
//        if (!MusicHelper.useAuraEqualizer(context)) {
//
//                Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.getAudioSessionId());
//                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
//                sendBroadcast(intent);
//        }

        SharedPrefsFile.saveSongToRecentlyPlayed(context, QueueManager.getCurrentSong());

    }
//
//    public boolean getAudioFocus(){
//        int result = audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
//        if(result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//            return false;
//        }
//        else {
//            audioFocusLoss = false;
//            return true;
//        }
//    }

    @Override
    public void onPause(){
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    @Override
    public void onPlay(){
        setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
    }


   public void setMediaSessionMetadata(){

        Song nowPlaying = QueueManager.getCurrentSong();

       GlideApp.with(getApplicationContext())
               .asBitmap()
               .load(new AlbumImage(nowPlaying.getArtist(),
                       nowPlaying.getAlbum(),
                       (int) nowPlaying.getAlBumId()))
               .diskCacheStrategy(DiskCacheStrategy.DATA)
               .apply(new RequestOptions().
                       override(Constants.LARGE_SIZE, Constants.LARGE_SIZE))
               .placeholder(R.drawable.album_art)
               .error(R.drawable.album_art)
               .into(new CustomTarget() {

                   @Override
                   public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                       metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                               resource);
                       mediaSessionCompat.setMetadata(metadataBuilder.build());
                   }
               }) ;

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, nowPlaying.getID().toString());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, nowPlaying.getTitle());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, nowPlaying.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, nowPlaying.getAlbum());
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, nowPlaying.Duration);

        mediaSessionCompat.setMetadata(metadataBuilder.build());
        Log.i(Constants.TAG, "Song Changed. Setting Metadata");
    }

    public void setPlaybackState(int playbackState) {
        if (mediaSessionCompat == null) {
            return;
        }

        playbackStateBuilder.setState(playbackState, playbackManager.getPosition(),
                1.0f, SystemClock.elapsedRealtime());

        mediaSessionCompat.setPlaybackState(playbackStateBuilder.build());
        Log.i(Constants.TAG, "Song Changed. Setting Playback state");

        notifyAndroidMusic(Constants.PLAYBACK_STATE_CHANGED);
    }


    public void notifyAndroidMusic(String value){

        Intent intent = new Intent("reverblabs.apps.aura");

        if(value.equals(Constants.META_CHANGED)){

            Song nowPlaying = QueueManager.getCurrentSong();

            intent.putExtra("id", nowPlaying.getID());
            intent.putExtra("artist", nowPlaying.getArtist());
            intent.putExtra("album", nowPlaying.getAlbum());
            intent.putExtra("track", nowPlaying.getTitle());
            intent.putExtra("playing", true);
            intent.putExtra("playing", true);
            intent.setAction("com.android.music.metachanged");
        }
        else if (value.equals(Constants.PLAYBACK_STATE_CHANGED)){
            if(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                intent.putExtra("playing", true);
            }
            else {
                intent.putExtra("playing", false);
            }
            intent.setAction("com.android.music.playstatechanged");
        }


        sendBroadcast(intent);
    }

    public void updateWidget(){

        Song nowPlaying = QueueManager.getCurrentSong();

        Context context = getApplicationContext();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        WidgetSmall.checkEnabled(context, appWidgetManager);
        WidgetSmall.updateWidget(nowPlaying, context, appWidgetManager,
                mediaController.getMetadata());
//
//        WidgetLarge.checkEnabled(context, appWidgetManager);
//        WidgetLarge.updateWidget(nowPlaying, context, appWidgetManager,
//                mediaController.getMetadata());

        WidgetLargeAlbumArt.checkEnabled(context, appWidgetManager);
        WidgetLargeAlbumArt.updateWidget(nowPlaying, context, appWidgetManager,
                mediaController.getMetadata());

    }


    private void registerAudioNoisyReceiver(){
        if(!audioNoisyReceiverRegistered){
            registerReceiver(audioBecomingNoisyReceiver, audioNoisyReceiverFilter);
            audioNoisyReceiverRegistered = true;
        }
    }



    private void unregisterAudioNoisyReceiver(Context context){
        if(audioNoisyReceiverRegistered){
            unregisterReceiver(audioBecomingNoisyReceiver);
            audioNoisyReceiverRegistered = false;
        }
    }

    private BroadcastReceiver audioBecomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (MusicHelper.pauseAudioPlayback(context)) {

                if (playbackManager != null && playbackManager.isPlaying()) {
                    mediaController.getTransportControls().pause();
                }
                Toast.makeText(context, getString(R.string.headphones_disconnected), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class MediaButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                processKey(event);
            }
        }
    }

    public void processKey(KeyEvent event){
        if (event == null){
            return;
        }

        int action = event.getAction();

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                if (action == KeyEvent.ACTION_DOWN) {
                    long time = SystemClock.uptimeMillis();
                    if (time - sLastClickTime < DOUBLE_CLICK_DELAY) {

                        Handler handler = new Handler();
                        DelayedClickCounter dcc = new DelayedClickCounter(time);
                        handler.postDelayed(dcc, DOUBLE_CLICK_DELAY);
                    } else {
                        playbackManager.playOrPause();
                    }
                    sLastClickTime = time;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (action == KeyEvent.ACTION_DOWN)
                    playbackManager.playNext();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (action == KeyEvent.ACTION_DOWN)
                    playbackManager.playPrevious();
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (action == KeyEvent.ACTION_DOWN)
                    playbackManager.playOrPause();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (action == KeyEvent.ACTION_DOWN)
                    playbackManager.playOrPause();
                break;
            default:
                return;
        }

        return;
    }

    private final MediaSessionCompat.Callback mediaSessionCallback = new
            MediaSessionCompat.Callback() {
          @Override
        public boolean onMediaButtonEvent(Intent intent) {


              String action = intent.getAction();


              if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {

                  KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                  if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK){

                      if (event.getAction() == KeyEvent.ACTION_DOWN) {

                          long time = SystemClock.uptimeMillis();

                          if (time - sLastClickTime < DOUBLE_CLICK_DELAY) {

                              Handler handler = new Handler();
                              DelayedClickCounter dcc = new DelayedClickCounter(time);
                              handler.postDelayed(dcc, DOUBLE_CLICK_DELAY);
                          }

                          sLastClickTime = time;

                      }

                  }
              }

              return super.onMediaButtonEvent(intent);
          }

        @Override
        public void onPlay() {
            super.onPlay();
            playbackManager.play();
        }

        @Override
        public void onPause() {
            super.onPause();
            playbackManager.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            playbackManager.playNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            playbackManager.playPrevious();
        }

        @Override
        public void onStop() {
            super.onStop();
            stopMediaPlayer();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            playbackManager.seekTo((int)pos);
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        }
    };

    private class DelayedClickCounter implements Runnable {

        private long mSerial;

        public DelayedClickCounter(long serial) {
            mSerial = serial;
        }

        @Override
        public void run() {
            sDelayedClicks++;
            if (mSerial != sLastClickTime)
                return;

            String act;
            switch (sDelayedClicks) {
                case 1:
                    act = Constants.BROADCAST_FORWARD;
                    break;
                default:
                    act = Constants.BROADCAST_BACK;
            }
            sDelayedClicks = 0;
            runAction(act);
        }
    }

    private void runAction(String act) {
        if (act == null)
            return;

        if (act.equals(Constants.BROADCAST_FORWARD)){
            playbackManager.playNext();
        }
        else if (act.equals(Constants.BROADCAST_BACK)){
            playbackManager.playPrevious();
        }
        else if (act.equals(Constants.BROADCAST_PLAY_OR_PAUSE)){
            playbackManager.playOrPause();
        }
    }


    BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(Constants.LOCAL_BROADCAST);
            String action = bundle.getString(Constants.LOCAL_BROADCAST);
            try {

               if (action.equals(Constants.BROADCAST_SHUFFLE)) {

                } else if (action.equals(Constants.BROADCAST_BACK)){
                   if (playbackManager != null){
                       playbackManager.playPrevious();
                   }
               }
                else if (action.equals(Constants.BROADCAST_FORWARD)){
                   playbackManager.playNext();
               }
                else if (action.equals(Constants.BROADCAST_PLAY_OR_PAUSE)){
                   playbackManager.playOrPause();
               }
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    };

//
//    public void onAudioFocusChange(int focusChange){
//        switch (focusChange){
//            case AudioManager.AUDIOFOCUS_GAIN:
//                if(transientAudioLoss && ((playbackState.getState() == PlaybackStateCompat.STATE_PAUSED) ||
//                        (playbackState.getState() == PlaybackStateCompat.STATE_STOPPED))){
//                    transientAudioLoss = false;
//                    playOrPauseMediaPlayer();
//
//                }
//
//                if(audioFocusCanDuck){
//                    audioFocusCanDuck = false;
//                    mediaPlayer.setVolume(1.0f, 1.0f);
//                }
//
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS:
//                mediaPlayer.pause();
//                audioFocusLoss = true;
//
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    mediaControllerCompat.getTransportControls().pause();
//                    transientAudioLoss = true;
//                }
//
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//
//                mediaPlayer.setVolume(0.1f,0.1f);
//                audioFocusCanDuck = true;
//
//                break;
//        }
//    }

    public void stopMediaPlayer(){
        if(playbackManager == null){
            return;
        }


        if (equalizer != null) {
            equalizer.setEnabled(false);
            equalizer.release();
        }

        if (bassBoost != null){
            bassBoost.setEnabled(false);
            bassBoost.release();
        }

        if (virtualizer != null){
            virtualizer.setEnabled(false);
            virtualizer.release();
        }

        if (presetReverb != null){
            presetReverb.setEnabled(false);
            presetReverb.release();
        }

//        if (!MusicHelper.useAuraEqualizer(context)){
//            final Intent intent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
//            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.getAudioSessionId());
//            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
//            sendBroadcast(intent);
//        }

        playbackManager.releaseResources();

        notificationManager.cancelNotification();

        //audioManager.abandonAudioFocus(this);

        mediaController.unregisterCallback(mediaControllerCallback);
        mediaSessionCompat.setActive(false);
        mediaSessionCompat.release();

        unregisterReceiver(audioBecomingNoisyReceiver);
    }

    public void closeService(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
        stopSelf();
    }

    @Override
    public void onDestroy(){
        if(playbackManager!=null){
            closeService();
            stopMediaPlayer();
        }
    }


}