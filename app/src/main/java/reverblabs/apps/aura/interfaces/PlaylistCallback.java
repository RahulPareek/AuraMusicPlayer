package reverblabs.apps.aura.interfaces;


public interface PlaylistCallback {
    void onCreateNewPlaylist(String input);
    void onAddToExistingPlaylist(long playlistId, String name);
}
