package reverblabs.apps.aura.interfaces;


import android.os.Bundle;

import java.util.ArrayList;

import reverblabs.apps.aura.model.Song;

public interface MainActivityCallback {
    void playSong(int position, ArrayList<Song> songsList);
    void handleShuffleFromFloatingButtom(ArrayList<Song> songsList);
    void createAlbumDetailFragment(Bundle bundle);
    void createArtistDetailFragment(Bundle bundle);
    void createGenreDetailFragment(Bundle bundle);
    void createPlaylistFragment(Bundle bundle);
    void createFoldersFragment(String path);
    void openNavigationDrawer();
    void createArtistBioFragment(String name);
    void executeBackButtonCall();
    void launchTagEditorActivity(Song song);
}
