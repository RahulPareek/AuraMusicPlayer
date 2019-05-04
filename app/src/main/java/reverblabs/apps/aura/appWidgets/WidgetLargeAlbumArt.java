package reverblabs.apps.aura.appWidgets;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.RemoteViews;

import reverblabs.apps.aura.playback.PlaybackReceivers;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.ui.activities.MainActivity;
import reverblabs.apps.aura.model.Song;

public class WidgetLargeAlbumArt extends AppWidgetProvider {

    public static boolean widgetEnabled ;

    @Override
    public void onEnabled(Context context){
        widgetEnabled = true;
    }

    @Override
    public void onDisabled(Context context){
        widgetEnabled = false;
    }


    public static void checkEnabled(Context context, AppWidgetManager manager){
        widgetEnabled = manager.getAppWidgetIds(new ComponentName(context, WidgetLargeAlbumArt.class)).length != 0;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){


//        if(HelperClass.auraMusicService !=null){
//
//            Song nowPlaying;
//
//            nowPlaying = HelperClass.auraMusicService.getNowPlaying();
//
//            widgetEnabled = true;
//
//            updateWidget(nowPlaying, context, appWidgetManager, HelperClass.auraMusicService.mediaSessionCompat.getController().getMetadata());
//
//        }
//
//        else {
//            Song nowPlaying = new Song();
//            ArrayList<Song> songArrayList =  new ArrayList<>();
//
//            int nowPlayingPosition = SharedPrefsFile.getNowPlayingPosition(context);
//            if (nowPlayingPosition != -1) {
//                songArrayList = SharedPrefsFile.getQueue(context);
//
//                if (songArrayList != null) {
//                    try {
//                        Log.i(Constants.TAG, Integer.toString(nowPlayingPosition));
//                        nowPlaying = songArrayList.get(nowPlayingPosition);
//                    }catch (ArrayIndexOutOfBoundsException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                    updateWidget(nowPlaying, context, appWidgetManager, null);
//                }
//
//            }
//
//        }
    }

    public static void updateWidget(Song song, Context context, AppWidgetManager appWidgetManager, MediaMetadataCompat metadataCompat){

        if(widgetEnabled) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_large_album_art);

            remoteViews.setViewVisibility(R.id.widget_large_album_art_song_name, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_large_album_art_artist_name, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_large_album_art_album_name, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_large_album_art_song_name, song.getTitle());
            remoteViews.setTextViewText(R.id.widget_large_album_art_artist_name, song.getArtist());
            remoteViews.setTextViewText(R.id.widget_large_album_art_album_name, song.getAlbum());

            if (metadataCompat != null) {
                remoteViews.setImageViewBitmap(R.id.widget_large_album_art_image, metadataCompat.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
            }
            else {
                remoteViews.setImageViewResource(R.id.widget_large_album_art_image, R.drawable.album_art);
            }

            remoteViews.setImageViewResource(R.id.widget_forward, R.drawable.notification_forward);
            remoteViews.setImageViewResource(R.id.widget_back, R.drawable.notification_back);
//
//            if (HelperClass.auraMusicService != null) {
//
//
//                if (HelperClass.auraMusicService.playbackState.getState() == PlaybackStateCompat.STATE_PAUSED ||
//                        HelperClass.auraMusicService.playbackState.getState() == PlaybackStateCompat.STATE_STOPPED) {
//                    remoteViews.setImageViewResource(R.id.widget_play_or_pause, R.drawable.notification_play);
//
//                } else {
//                    remoteViews.setImageViewResource(R.id.widget_play_or_pause, R.drawable.notification_pause);
//
//                }
//            } else {
//                remoteViews.setImageViewResource(R.id.widget_play_or_pause, R.drawable.notification_play);
//            }


            Intent play_or_pause_intent = new Intent(context, PlaybackReceivers.PlayPauseReceiver.class);
            PendingIntent play_or_pause_pending_intent = PendingIntent.getBroadcast(context, 0, play_or_pause_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play_or_pause, play_or_pause_pending_intent);

            Intent back_intent = new Intent(context, PlaybackReceivers.BackReceiver.class);
            PendingIntent back_pending_intent = PendingIntent.getBroadcast(context, 0, back_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_back, back_pending_intent);

            Intent forward_intent = new Intent(context, PlaybackReceivers.ForwardReceiver.class);
            PendingIntent forward_pending_intent = PendingIntent.getBroadcast(context, 0, forward_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_forward, forward_pending_intent);


            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.app_widget_text_container, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.widget_large_album_art_image, pendingIntent);

            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetLargeAlbumArt.class), remoteViews);

        }
    }

}
