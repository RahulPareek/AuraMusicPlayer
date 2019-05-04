package reverblabs.apps.aura.interfaces;

import android.os.Bundle;

import java.util.ArrayList;

import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;

public interface AdapterToFragmentCallbacks {

    void playSong(int position, ArrayList<Song> songsList);
    void play(ArrayList<Song> songs);
    void createAlbumDetailFragment(Bundle bundle);
    void createArtistDetailFragment(Bundle bundle);
    void createGenreDetailFragment(Bundle bundle);
    void createPlaylistDetailFragment(Bundle bundle);
    void createDeleteDialogForMultipleFiles(ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace);
    void createDeleteDialog(Song song, ArrayList<Song> temp,
                                   ArrayList<Integer> pos, DeleteInterFace deleteInterFace);
    void createDeleteDialog(Album album, ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace);
    void createDeleteDialog(Artist artist, ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace);
    void createDeleteDialog(String name, ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace);
    void createPlaylistDialog(ArrayList<Song> temp);
    void createDetailsDialog(String path);
    void launchEditTagsActivity(Song song);
    void playNext(Song song);
    void addToQueue(Song song);
    void addToQueue(ArrayList<Song> songs);
    void setupActionMode();
    void setActionModeCount(int count);
}