//package reverblabs.apps.aura;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//
//import reverblabs.apps.aura.interfaces.ServiceStateCallback;
//
//public class HelperClass {
//    public static AuraMusicService auraMusicService = null;
//
//    public static boolean musicBound = false;
//
//    private static Intent serviceIntent = null;
//
//    public static ServiceStateCallback serviceStateCallback;
//
//    public HelperClass(){}
//
//    public static ServiceConnection musicConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            AuraMusicService.MusicBinder binder  = ((AuraMusicService.MusicBinder) iBinder);
//            auraMusicService = binder.getService();
//            musicBound=true;
//            serviceStateCallback.serviceConnectedCallback();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            musicBound = false;
//        }
//    };
//
//    public static void startPlayMusicService(Context context){
//        serviceIntent = new Intent(context, AuraMusicService.class);
//        context.bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
//        context.startService(serviceIntent);
//    }
//
//    public static void stopPlayMusicService(Context context){
//        if(serviceIntent == null){
//            return;
//        }
//        HelperClass.auraMusicService.stopMediaPlayer();
//        HelperClass.auraMusicService.closeService();
//        context.stopService(serviceIntent);
//        context.unbindService(musicConnection);
//        serviceIntent = null;
//        auraMusicService = null;
//    }
//}
