package reverblabs.apps.aura.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import reverblabs.apps.aura.ui.activities.MainActivity;
import reverblabs.apps.aura.utils.Constants;

public class PlaybackReceivers {

    public PlaybackReceivers(){}

    public static class PlayPauseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context cx, Intent intent){
            issueBroadcast(Constants.BROADCAST_PLAY_OR_PAUSE, cx);
        }
    }

    public static class BackReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context cx, Intent intent){
            issueBroadcast(Constants.BROADCAST_BACK, cx);
        }
    }

    public static class ForwardReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context cx, Intent intent){
            issueBroadcast(Constants.BROADCAST_FORWARD, cx);
        }
    }

    private static void issueBroadcast(String value, Context context){
//      if(HelperClass.auraMusicService == null) {
//
//          Intent intent = new Intent(context, MainActivity.class);
//          Bundle bundle = new Bundle();
//          bundle.putString(Constants.ACTION_FROM_WIDGETS, value);
//          intent.setAction(value);
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          context.startActivity(intent);
//        }
//        else {
//
//          Intent sendBroadcastIntent = new Intent(Constants.LOCAL_BROADCAST);
//          Bundle bundle = new Bundle();
//          bundle.putString(Constants.LOCAL_BROADCAST, value);
//          sendBroadcastIntent.putExtra(Constants.LOCAL_BROADCAST, bundle);
//          LocalBroadcastManager.getInstance(context).sendBroadcast(sendBroadcastIntent);
//      }
    }
}
