package reverblabs.apps.aura.playback;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.CustomTarget;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.playback.AuraMusicService;
import reverblabs.apps.aura.ui.activities.MainActivity;
import reverblabs.apps.aura.utils.Constants;

public class NotificationManager extends BroadcastReceiver {

    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "reverblabs.apps.aura.MUSIC_CHANNEL_ID";

    private static final int REQUEST_CODE = 8;

    private AuraMusicService auraMusicService;

    private MediaSessionCompat mediaSessionCompat;

    private MediaMetadataCompat mediaMetadata;
    private PlaybackStateCompat playbackState;

    private MediaControllerCompat mediaControllerCompat;
    private MediaSessionCompat.Token sessionToken;
    private MediaControllerCompat.TransportControls transportControls;

    private Context context;

    PendingIntent cancelIntent;

    private final android.app.NotificationManager notificationManager;

    PendingIntent backIntent;
    PendingIntent forwardIntent;
    PendingIntent playIntent;
    PendingIntent pauseIntent;

    private boolean started = false;


    public NotificationManager(AuraMusicService service, Context cx,
                               MediaSessionCompat mediaSession) throws RemoteException{

        mediaSessionCompat = mediaSession;

        auraMusicService = service;
        context = cx;

        updateSession();

        String pkg = context.getPackageName();

        notificationManager = (android.app.NotificationManager)
                auraMusicService.getSystemService(Context.NOTIFICATION_SERVICE);

        backIntent = PendingIntent.getBroadcast(auraMusicService,REQUEST_CODE,
                new Intent(Constants.NOTIFIACTION_PREVIOUS).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        forwardIntent = PendingIntent.getBroadcast(auraMusicService,REQUEST_CODE,
                new Intent(Constants.NOTIFICATION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        playIntent = PendingIntent.getBroadcast(auraMusicService,REQUEST_CODE,
                new Intent(Constants.NOTIFICATION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        pauseIntent = PendingIntent.getBroadcast(auraMusicService,REQUEST_CODE,
                new Intent(Constants.NOTIFICATION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);


        cancelIntent = PendingIntent.getBroadcast(auraMusicService, REQUEST_CODE,
                new Intent(Constants.NOTIFICATION_CANCEL).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mediaMetadata = mediaControllerCompat.getMetadata();
        playbackState = mediaControllerCompat.getPlaybackState();

//        if (!MusicHelper.isLollipop()){
//            imageFetcher = new ImageFetcher(context);
//        }

        mediaControllerCompat.registerCallback(mediaControllerCallback);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NOTIFIACTION_PREVIOUS);
        filter.addAction(Constants.NOTIFICATION_NEXT);
        filter.addAction(Constants.NOTIFICATION_PLAY);
        filter.addAction(Constants.NOTIFICATION_PAUSE);
        auraMusicService.registerReceiver(this, filter);


        notificationManager.cancelAll();
    }

    private void updateSession() throws RemoteException{
        MediaSessionCompat.Token freshToken = mediaSessionCompat.getSessionToken();

        if(sessionToken == null && freshToken !=null || sessionToken!=null && !sessionToken.equals(freshToken)){

            if(mediaControllerCompat != null){
                mediaControllerCompat.unregisterCallback(mediaControllerCallback);
            }

            sessionToken = freshToken;

            if(sessionToken != null){
                mediaControllerCompat = new MediaControllerCompat(context, sessionToken);
                transportControls = mediaControllerCompat.getTransportControls();

                if(started){
                    mediaControllerCompat.registerCallback(mediaControllerCallback);
                }
            }
        }

    }


    public void cancelNotification(){

        started = false;
        mediaControllerCompat.unregisterCallback(mediaControllerCallback);

        try{
            notificationManager.cancel(NOTIFY_ID);
            auraMusicService.unregisterReceiver(this);
            auraMusicService.stopForeground(true);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onReceive(Context context, Intent intent){

        final String action = intent.getAction();
        Log.i(Constants.TAG, action);

        switch (action){

            case Constants.NOTIFIACTION_PREVIOUS:
                transportControls.skipToPrevious();
                break;

            case Constants.NOTIFICATION_NEXT:
                transportControls.skipToNext();
                break;

            case Constants.NOTIFICATION_PLAY:
                transportControls.play();
                break;

            case Constants.NOTIFICATION_PAUSE:
                transportControls.pause();
                break;

            case Constants.NOTIFICATION_CANCEL:

                stopService();

                break;

            default:

        }
    }

    private void stopService(){
        if(auraMusicService != null){
            auraMusicService.closeService();
        }
    }


    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.i(Constants.TAG, "Playback state. Updating Notification");

            if (state.getState() == PlaybackStateCompat.STATE_STOPPED ||
                    state.getState() == PlaybackStateCompat.STATE_NONE) {
                cancelNotification();
            } else {
                Notification notification = createNotification();

                if (notification != null) {
                    notificationManager.notify(NOTIFY_ID, notification);
                    if (!started) {
                        started = true;
                        auraMusicService.startForeground(NOTIFY_ID, notification);
                    }
                }
            }

            super.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadataCompat) {
            Log.i(Constants.TAG, "Metatchanged. Updating Notification");

            if (!(mediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED)) {

                Notification notification = createNotification();

                if (notification != null) {
                    notificationManager.notify(NOTIFY_ID, notification);
                }
            }

            super.onMetadataChanged(metadataCompat);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();

            try {
                updateSession();
            } catch (RemoteException e) { }
        }
    };


    private Notification createNotification(){

        mediaMetadata = mediaControllerCompat.getMetadata();
        playbackState = mediaControllerCompat.getPlaybackState();

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }


        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(R.drawable.exo_controls_previous,
                    context.getString(R.string.label_back), backIntent);
        }

        addPlayPauseAction(notificationBuilder);

        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(R.drawable.exo_controls_next,
                    context.getString(R.string.label_forward), forwardIntent);
        }

        if(mediaMetadata != null) {

            notificationBuilder
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(sessionToken)
                            .setCancelButtonIntent(cancelIntent)
                            .setShowCancelButton(true))
                    .setDeleteIntent(cancelIntent)
                    .setOnlyAlertOnce(true)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.notification_icon)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setUsesChronometer(true)
                    .setContentIntent(createContentIntent())
                    .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                    .setContentText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .setLargeIcon(BitmapFactory.decodeResource(auraMusicService.getResources(),
                            R.drawable.album_art));
        }
        else{
            Log.i(Constants.TAG, "Mediametadata is null in Notification Manager");
        }
        setNotificationPlaybackState(notificationBuilder);
        setNotificationAlbumArt(notificationBuilder);

        return notificationBuilder.build();
    }

    private void setNotificationAlbumArt(final NotificationCompat.Builder notificationBuilder){

        Song song = QueueManager.getCurrentSong();

        GlideApp.with(auraMusicService)
                .asBitmap()
                .load(new AlbumImage(song.getArtist(),
                        song.getAlbum(),
                        (int) song.getAlBumId()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.LARGE_SIZE, Constants.LARGE_SIZE))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(new CustomTarget() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notificationBuilder.setLargeIcon(resource);
                        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
                    }
                }) ;
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {

        String label;
        int icon;
        PendingIntent intent;
        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = context.getString(R.string.label_play_pause);
            icon = R.drawable.exo_controls_pause;
            intent = pauseIntent;
        } else {
            label = context.getString(R.string.label_play_pause);
            icon = R.drawable.exo_controls_play;
            intent = playIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {

        if (playbackState == null || !started) {

            auraMusicService.stopForeground(true);
            return;
        }
        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING
                && playbackState.getPosition() >= 0) {

            builder
                    .setWhen(System.currentTimeMillis() - playbackState.getPosition())
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {

            builder
                    .setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }

        builder.setOngoing(playbackState.getState() == PlaybackStateCompat.STATE_PLAYING);
    }



    private PendingIntent createContentIntent(){
        Intent openUi = new Intent(auraMusicService, MainActivity.class);
        openUi.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(auraMusicService, REQUEST_CODE, openUi, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    auraMusicService.getString(R.string.notification_channel),
                    android.app.NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    auraMusicService.getString(R.string.notification_channel_description));

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
