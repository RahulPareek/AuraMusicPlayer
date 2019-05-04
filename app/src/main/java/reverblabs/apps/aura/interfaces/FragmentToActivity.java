package reverblabs.apps.aura.interfaces;

import android.os.Bundle;

import java.util.ArrayList;

import reverblabs.apps.aura.model.Song;

public interface FragmentToActivity{

    void onQueueMethodClicked();
    void issueBroadcast(String value);
    void createPlaylistDialog(ArrayList<Song> temp);
    void navigateToArtist(Bundle bundle);
    void navigateToAlbum(Bundle bundle);
}
