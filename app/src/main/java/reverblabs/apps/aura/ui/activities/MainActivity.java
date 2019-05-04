package reverblabs.apps.aura.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.ui.fragments.album.AlbumDetailFragment;
import reverblabs.apps.aura.ui.fragments.artist.ArtistBioFragment;
import reverblabs.apps.aura.ui.fragments.artist.ArtistDetailFragment;
import reverblabs.apps.aura.ui.fragments.FoldersFragment;
import reverblabs.apps.aura.ui.fragments.genre.GenreDetailFragment;
import reverblabs.apps.aura.ui.fragments.MainFragment;
import reverblabs.apps.aura.ui.fragments.playlist.PlaylistDetailFragment;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class MainActivity extends BaseActivity implements MainActivityCallback,
        NavigationView.OnNavigationItemSelectedListener {

    public Context context;
    public ContentResolver contentResolver;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private boolean isFoldersFragmentOn = false;

    FoldersFragment foldersFragment;

    @Override
    protected int getLayoutId(){
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);

            }
            else {

                init();
            }
        }
        else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(resultCode,permissions,grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            init();
        }
        else {
            finish();
        }
    }

    private void init(){

        isFoldersFragmentOn = false;

        context = getApplicationContext();
        contentResolver = getContentResolver();

        navigationView = findViewById(R.id.navigation_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        createMainFragment();

        final Intent intent = getIntent();

        if(intent == null){

        }
        else {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.LAUNCH_ALBUM_FRAGMENT)) {
                    createAlbumDetailFragment(intent.getExtras());
                } else if (action.equals(Constants.LAUNCH_ARTIST_FRAGMENT)) {
                    createArtistDetailFragment(intent.getExtras());
                }
                else if (action.equals(Constants.BROADCAST_BACK)){
                    issueBroadcast(Constants.BROADCAST_BACK);
                }
                else if (action.equals(Constants.BROADCAST_FORWARD)){
                    issueBroadcast(Constants.BROADCAST_FORWARD);
                }
                else if (action.equals(Constants.BROADCAST_PLAY_OR_PAUSE)){
                    issueBroadcast(Constants.BROADCAST_PLAY_OR_PAUSE);
                }
                else if (action.equals(Intent.ACTION_VIEW)){

                    final Handler handler = new Handler();

                    new Thread(new Runnable() {
                        boolean songInitialized = false;
                        @Override
                        public void run() {

                            if (!songInitialized) {


                                if (action.equals(Intent.ACTION_VIEW)) {

                                    Uri uri = intent.getData();
                                    Log.i(Constants.TAG, uri.toString());

                                    String projection[] = {MediaStore.Audio.Media._ID,
                                            MediaStore.Audio.Media.DATA,
                                            MediaStore.Audio.Media.ARTIST,
                                            MediaStore.Audio.Media.ARTIST_ID,
                                            MediaStore.Audio.Media.ALBUM,
                                            MediaStore.Audio.Media.ALBUM_ID,
                                            MediaStore.Audio.Media.TITLE,
                                            MediaStore.Audio.Media.DURATION,
                                            MediaStore.Audio.Media.YEAR};

                                    Cursor cursor = contentResolver.query(uri, projection, null, null, null);
                                    if (cursor != null) {
                                        cursor.moveToFirst();

                                        final ArrayList<Song> songArrayList = MusicHelper.extractSongsFromCursor(cursor);

                                        cursor.close();
//
//                                        if (HelperClass.auraMusicService == null) {
//
//                                        } else {
//
//                                            HelperClass.auraMusicService.setNowPlaying(0, songArrayList.get(0));
//                                            HelperClass.auraMusicService.setMusicPlayerList(songArrayList);
//                                            HelperClass.auraMusicService.createMediaPlayer();
//                                            HelperClass.auraMusicService.startPlayingSong();
//                                            songInitialized = true;
//                                        }
                                    }

                                    handler.postDelayed(this, 200);

                                }
                            }


                        }
                    }).start();

                }

            }
        }

    }


    private void issueBroadcast(final String value){

        final Handler handler = new Handler();

        new Thread(new Runnable() {

            boolean serviceNull = true;

            @Override
            public void run() {

//                if (serviceNull) {
//
//                    if (HelperClass.auraMusicService == null) {
//
//                    } else {
//                        Intent sendBroadcastIntent = new Intent(Constants.LOCAL_BROADCAST);
//                        Bundle bundle = new Bundle();
//                        bundle.putString(Constants.LOCAL_BROADCAST, value);
//                        sendBroadcastIntent.putExtra(Constants.LOCAL_BROADCAST, bundle);
//                        LocalBroadcastManager.getInstance(context).sendBroadcast(sendBroadcastIntent);
//                        serviceNull = false;
//                    }
//                    handler.postDelayed(this, 200);
//                }
            }
        }).start();
    }


    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.music_library_navigation:
                if (isFoldersFragmentOn) {
                    isFoldersFragmentOn = false;
                    item.setChecked(true);
                    createMainFragment();
                }

                break;

            case R.id.folders_navigation:
                item.setChecked(true);
                createFoldersFragment(SharedPrefsFile.getUserPreferredFolder(context));

                break;

            case R.id.audio_effects_navigation:

                Intent audioEffects = new Intent(context, EqualizerActivity.class);
                startActivity(audioEffects);

                break;

            case R.id.settings_navigation:

                Intent settings = new Intent(context, SettingsActivity.class);
                startActivity(settings);

                break;

            default:
                return true;
        }
        drawerLayout.closeDrawers();

        return false;

    }

    @Override
    public void openNavigationDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void createMainFragment(){

        MainFragment mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_frame_layout, mainFragment)
                .commitAllowingStateLoss();

    }

    @Override
    public void playSong(int position, ArrayList<Song> songsList){
        QueueManager.checkShuffleAndSetQueue(context, songsList, position);
        getMediaController().getTransportControls().play();
    }

    @Override
    public void handleShuffleFromFloatingButtom(ArrayList<Song> songsList){
        QueueManager.handleShuffleFromFloatingButton(songsList);
        getMediaController().getTransportControls().play();
    }

    @Override
    public void createAlbumDetailFragment(Bundle bundle){

        isFoldersFragmentOn = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment();

        if (MusicHelper.isLollipop()){


            getWindow().setEnterTransition(new Fade());

            albumDetailFragment.setEnterTransition(new Fade());
            albumDetailFragment.setExitTransition(new Fade());

            getWindow().setExitTransition(new Fade());

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        albumDetailFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_activity_frame_layout, albumDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void createArtistDetailFragment(Bundle bundle){

        isFoldersFragmentOn = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            artistDetailFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
            artistDetailFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            getWindow().setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        artistDetailFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_activity_frame_layout, artistDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void createGenreDetailFragment(Bundle bundle){

        isFoldersFragmentOn = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        GenreDetailFragment genreDetailFragment = new GenreDetailFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            genreDetailFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
            genreDetailFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            getWindow().setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        genreDetailFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_activity_frame_layout, genreDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void createPlaylistFragment(Bundle bundle){

        isFoldersFragmentOn = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PlaylistDetailFragment playlistDetailFragment = new PlaylistDetailFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            playlistDetailFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
            playlistDetailFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            getWindow().setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        playlistDetailFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.main_activity_frame_layout, playlistDetailFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void createArtistBioFragment(String name){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ArtistBioFragment artistBioFragment = new ArtistBioFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            artistBioFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
            artistBioFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            getWindow().setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        Bundle bundle = new Bundle();
        bundle.putString("artist_name", name);
        artistBioFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_activity_frame_layout, artistBioFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void createFoldersFragment(String path){

        isFoldersFragmentOn = true;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        foldersFragment = new FoldersFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            foldersFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
            foldersFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

            getWindow().setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

        }
        else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.PATH, path);
        foldersFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_activity_frame_layout, foldersFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void launchTagEditorActivity(Song song){
        Intent intent = new Intent(context, TagEditorActivity.class);
        intent.putExtra(Constants.PARCELABLE, song);
        startActivity(intent);
    }

    @Override
    public void executeBackButtonCall(){
        isFoldersFragmentOn = false;
        onBackPressed();
    }

    @Override
    public void onBackPressed(){
        if (isFoldersFragmentOn){
            foldersFragment.backButtonPressed();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:

                onBackPressed();

                return true;

            case R.id.search:

                Intent search = new Intent(context, SearchActivity.class);
                search.putExtra(SearchManager.QUERY, "");
                startActivity(search);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}







