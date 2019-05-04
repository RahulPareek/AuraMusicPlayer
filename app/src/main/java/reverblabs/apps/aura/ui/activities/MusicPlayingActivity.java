package reverblabs.apps.aura.ui.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Queue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.playback.AuraMusicService;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.ui.fragments.MusicPlayingFragment;
import reverblabs.apps.aura.ui.fragments.QueueFragment;
import reverblabs.apps.aura.interfaces.FragmentToActivity;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class MusicPlayingActivity extends AppCompatActivity implements FragmentToActivity {

    static boolean isViewOn = false;

    QueueFragment queueFragment;
    MusicPlayingFragment musicPlayingFragment;

    private MediaBrowserCompat mediaBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_playing);


        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, AuraMusicService.class), connectionCallback,
                null);


        if(getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        musicPlayingFragment = new MusicPlayingFragment();

        fragmentTransaction.replace(R.id.fragment_container, musicPlayingFragment);
        fragmentTransaction.commit();

        isViewOn = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.search_btn, menu);
        getMenuInflater().inflate(R.menu.menu_music_activity, menu);

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


            case R.id.add_to_playlist:
                Song addToPlaylist = QueueManager.getCurrentSong();
                ArrayList<Song> temp = new ArrayList<>();
                temp.add(addToPlaylist);
                createPlaylistDialog(temp);

                return true;


            case R.id.go_to_artist:
                Song goToArtist = QueueManager.getCurrentSong();

                Bundle artistbundle = new Bundle();
                artistbundle.putString("artist",goToArtist.getArtist());
                artistbundle.putLong("id",goToArtist.ArtistId);

                navigateToArtist(artistbundle);

                return true;

            case R.id.go_to_album:
                Song goToAlbum = QueueManager.getCurrentSong();
                Bundle albumBundle = new Bundle();
                albumBundle.putLong("albumid",goToAlbum.getAlBumId());
                albumBundle.putString("album",goToAlbum.getAlbum());
                albumBundle.putString("artist",goToAlbum.getArtist());
                albumBundle.putString("year",goToAlbum.Year);

                navigateToAlbum(albumBundle);

                return true;

            case R.id.details:

                DetailsDialog detailsDialog = new DetailsDialog();
                detailsDialog.init(QueueManager.getCurrentSong().getPath());
                detailsDialog.show(getSupportFragmentManager(), Constants.TAG);

                return true;

            case android.R.id.home:
                finish();
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
        musicPlayingFragment.registerMediaController();
        musicPlayingFragment.setImagesAndTitle(mediaController.getMetadata());
        musicPlayingFragment.setPlayOrPause(mediaController.getPlaybackState());
    }


    @Override
    protected void onStart(){
        super.onStart();
        mediaBrowser.connect();
        isViewOn = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        isViewOn = true;
    }

    public void onQueueMethodClicked(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        queueFragment =new QueueFragment();

        fragmentTransaction.setCustomAnimations(R.anim.slide_fragment_up, R.anim.slide_fragment_down);

        fragmentTransaction.replace(R.id.fragment_container,queueFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void createPlaylistDialog(ArrayList<Song> temp){
        ArrayList<Long> songId = new ArrayList<>();

        for (int i=0; i < temp.size() ;i++){
            songId.add(temp.get(i).getID());
        }

        PlaylistDialog playlistDialog = new PlaylistDialog();

        MusicHelper musicHelper = new MusicHelper(getApplicationContext(), getContentResolver());
        musicHelper.songId = songId;
        musicHelper.songList = temp;
        playlistDialog.playlistCallback = musicHelper;
        playlistDialog.show(getSupportFragmentManager(), Constants.TAG);
    }


    public void issueBroadcast(String value){
        Intent sendBroadcastIntent = new Intent(Constants.LOCAL_BROADCAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LOCAL_BROADCAST,value);
        sendBroadcastIntent.putExtra(Constants.LOCAL_BROADCAST, bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadcastIntent);
    }

    @Override
    public void navigateToAlbum(Bundle bundle){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Constants.LAUNCH_ALBUM_FRAGMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void navigateToArtist(Bundle bundle){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Constants.LAUNCH_ARTIST_FRAGMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        isViewOn = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isViewOn = false;

        mediaBrowser.disconnect();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}


