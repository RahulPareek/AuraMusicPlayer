package reverblabs.apps.aura.interfaces;

import reverblabs.apps.aura.model.Song;

import java.util.ArrayList;

public interface CallbackForDialogs {
    void createDeleteDialog(Song song, ArrayList<Song> temp);
    void createPlaylistDialog(ArrayList<Song> temp);
}
