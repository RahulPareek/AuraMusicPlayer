package reverblabs.apps.aura.ui.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import reverblabs.apps.aura.playback.AuraMusicService;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.ui.fragments.BottomPlaybackControls;
import reverblabs.apps.aura.utils.Constants;

public abstract class BaseActivity extends AppCompatActivity{


    private MediaBrowserCompat mediaBrowser;

    private BottomPlaybackControls bottomPlaybackControls;

    //private ProgressBar progressBar;

    //Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, AuraMusicService.class), connectionCallback,
                null);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    private final MediaControllerCompat.Callback mediaControllerCallback =
            new MediaControllerCompat.Callback() {

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);

                    if(shouldShowControls()){
                        showPlaybackControls();
                    }
                    else{
                        hidePlaybackControls();
                    }
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);

                    if(shouldShowControls()){
                        showPlaybackControls();
                    }
                    else{
                        hidePlaybackControls();
                    }
                }
    };


    @Override
    protected void onStart(){
        super.onStart();

        mediaBrowser.connect();
        Log.i(Constants.TAG, "Connecting to service");

        bottomPlaybackControls = (BottomPlaybackControls) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        hidePlaybackControls();

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);

        if(mediaController != null){
            mediaController.unregisterCallback(mediaControllerCallback);
        }

        mediaBrowser.disconnect();
    }



    private void hidePlaybackControls(){

        getSupportFragmentManager().beginTransaction().hide(bottomPlaybackControls).commit();
    }

    private void showPlaybackControls(){

        getSupportFragmentManager().beginTransaction().show(bottomPlaybackControls).commit();
    }



    public boolean shouldShowControls(){
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);

        if(mediaController == null || mediaController.getMetadata() == null || mediaController.getPlaybackState() == null){
            return false;
        }

        switch (mediaController.getPlaybackState().getState()){
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                return false;

            default:
                return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.search_btn, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        switch(menuItem.getItemId()){

            case R.id.search:

                Intent search = new Intent(this, SearchActivity.class);
                search.putExtra(SearchManager.QUERY, "");
                startActivity(search);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback(){

        @Override
        public void onConnected () {
            Log.i(Constants.TAG, "Connected to service");

            try{
                setUpMediaController();
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed(){
            Log.i(Constants.TAG, "Connection failed");
        }
    };


    private void setUpMediaController() throws RemoteException{
        MediaControllerCompat mediaController = new MediaControllerCompat(this,
                mediaBrowser.getSessionToken());

        MediaControllerCompat.setMediaController(this, mediaController);
        Log.i(Constants.TAG, "Media Controller set");


        if(shouldShowControls()){
            showPlaybackControls();
        }else{
            hidePlaybackControls();
        }

        if(bottomPlaybackControls != null){
            bottomPlaybackControls.onConnected();
        }

        mediaController.registerCallback(mediaControllerCallback);
    }


    private void metadataChanged(MediaMetadataCompat mediaMetadata){

        if (mediaMetadata != null) {

            initializeProgressBar();

//            if (HelperClass.auraMusicService.getNowPlaying() != null) {
//
//                long id = HelperClass.auraMusicService.getNowPlaying().getAlBumId();
//
////                imageFetcher.loadImage(id, mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
////                        mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
////                        "Song", false, albumArt, Constants.SMALL);
//
//                loadImage(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
//                        mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM), id);
//
//                songName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//                artistName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
//            }
//        }else {
//
//            initializeProgressBar();
//
//            Song nowPlaying = HelperClass.auraMusicService.getNowPlaying();
//
//            if (nowPlaying != null) {
//
//                loadImage(nowPlaying.getArtist(), nowPlaying.getAlbum(), nowPlaying.getAlBumId());
//
//                //imageFetcher.loadImage(id, nowPlaying.getAlbum(), nowPlaying.getArtist(), "Song", false, albumArt, Constants.SMALL);
//
//                songName.setText(nowPlaying.getTitle());
//                artistName.setText(nowPlaying.getArtist());
//            }
        }

    }



    private void initializeProgressBar(){


//        handler = new Handler();
//
//        if (HelperClass.auraMusicService != null) {
//
//            if (HelperClass.auraMusicService.getNowPlaying() != null) {
//
//                progressBar.setMax(HelperClass.auraMusicService.getNowPlaying().Duration);
//
//                this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (HelperClass.auraMusicService != null) {
//                            if (HelperClass.auraMusicService.mediaPlayer != null) {
//                                int currentPosition = HelperClass.auraMusicService.currentPosition();
//
//                                if (playbackState != null) {
//                                    if (currentPosition != -1 && (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING
//                                            || playbackState.getState() == PlaybackStateCompat.STATE_PAUSED)) {
//                                        progressBar.setProgress(currentPosition);
//                                    } else {
//                                        progressBar.setProgress(SharedPrefsFile.getMediaPlayerPosition(getApplicationContext()));
//                                    }
//                                }
//                                handler.postDelayed(this, 500);
//
//                            }
//                        }
//                    }
//                });
//            }
//        }

    }


    protected abstract int getLayoutId();


}
