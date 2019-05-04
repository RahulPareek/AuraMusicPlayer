package reverblabs.apps.aura.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.DetailsDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.ui.activities.MainActivity;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public abstract class BaseFragment extends Fragment implements AdapterToFragmentCallbacks {

    private MainActivityCallback mainActivityCallback;
    public ActionMode actionMode;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            mainActivityCallback =(MainActivityCallback) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setupActionMode() {
        if (getContext() != null) {
            actionMode = ((AppCompatActivity) getContext()).startSupportActionMode(actionModeCallback);
        }
    }

    @Override
    public void setActionModeCount(int count){
        actionMode.setTitle(String.format(getContext().getString(R.string.selected), count));
    }

    @Override
    public void playSong(int position, ArrayList<Song> songsList){
        mainActivityCallback.playSong(position, songsList);
    }

    @Override
    public void play(ArrayList<Song> songs){

    }


    @Override
    public void playNext(Song song){

    }

    @Override
    public void addToQueue(Song song){

    }

    @Override
    public void addToQueue(ArrayList<Song> songs){

    }


    @Override
    public void createAlbumDetailFragment(Bundle bundle){
        mainActivityCallback.createAlbumDetailFragment(bundle);
    }


    @Override
    public void createArtistDetailFragment(Bundle bundle){
        mainActivityCallback.createArtistDetailFragment(bundle);
    }

    @Override
    public void createGenreDetailFragment(Bundle bundle){
        mainActivityCallback.createGenreDetailFragment(bundle);
    }

    @Override
    public void createPlaylistDetailFragment(Bundle bundle){
        mainActivityCallback.createPlaylistFragment(bundle);
    }


    @Override
    public void createDeleteDialogForMultipleFiles(ArrayList<Song> temp,
                                                   ArrayList<Integer> pos, DeleteInterFace deleteInterFace){
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, null, "multiple", getContext(), pos);
        deleteDialog.deleteInterFace = deleteInterFace;
        deleteDialog.show(getFragmentManager(), Constants.TAG);
    }


    @Override
    public void createDeleteDialog(Song song, ArrayList<Song> temp,
                                   ArrayList<Integer> pos, DeleteInterFace deleteInterFace){
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, song.getTitle(), "single" , getContext(),
                pos);
        deleteDialog.deleteInterFace = deleteInterFace;
        deleteDialog.show(getFragmentManager(), Constants.TAG);
    }


    @Override
    public void createDeleteDialog(Album album, ArrayList<Song> temp,
                                   ArrayList<Integer> pos, DeleteInterFace deleteInterFace){
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, album.album, "set" , getContext(), pos);
        deleteDialog.deleteInterFace = deleteInterFace;
        deleteDialog.show(getFragmentManager(), Constants.TAG);
    }

    @Override
    public void createDeleteDialog(Artist artist, ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace){
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, artist.artist, "set" , getContext(), pos);
        deleteDialog.deleteInterFace = deleteInterFace;
        deleteDialog.show(getFragmentManager(), Constants.TAG);
    }

    @Override
    public void createDeleteDialog(String name, ArrayList<Song> temp, ArrayList<Integer> pos, DeleteInterFace deleteInterFace){
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.initDeleteDialog(temp, name, "set" , getContext(), pos);
        deleteDialog.deleteInterFace = deleteInterFace;
        deleteDialog.show(getFragmentManager(), Constants.TAG);
    }

    @Override
    public void createPlaylistDialog(ArrayList<Song> temp){
        ArrayList<Long> songId = new ArrayList<>();

        for (int i=0; i < temp.size() ;i++){
            songId.add(temp.get(i).getID());
        }
        PlaylistDialog playlistDialog = new PlaylistDialog();

        MusicHelper musicHelper = new MusicHelper(getContext(), getContext().getContentResolver());
        musicHelper.songId = songId;
        musicHelper.songList = temp;
        playlistDialog.playlistCallback = musicHelper;
        playlistDialog.show(getFragmentManager(), Constants.TAG);
    }

    @Override
    public void createDetailsDialog(String path){
        DetailsDialog detailsDialog = new DetailsDialog();
        detailsDialog.init(path);
        detailsDialog.show(getFragmentManager(), Constants.TAG);
    }


    @Override
    public void launchEditTagsActivity(Song song){
        mainActivityCallback.launchTagEditorActivity(song);
    }

    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_multi_selection, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            ArrayList<Song> temp = getActionModeSelectedItems();

            switch (item.getItemId()){
                case R.id.add_to_playlist:
                    createPlaylistDialog(temp);

                    actionMode.finish();
                    return true;

                case R.id.add_to_queue:
                    addToQueue(temp);

                    actionMode.finish();
                    return true;

                case R.id.delete:

                    createDeleteDialogForMultipleFiles(temp,getPositionOfSelectedItems(),
                            getDeleteInterface());
                    actionMode.finish();
                    return true;

                default:

                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            clearActionMode();
        }
    };

    public abstract ArrayList<Integer> getPositionOfSelectedItems();
    public abstract ArrayList<Song> getActionModeSelectedItems();
    public abstract DeleteInterFace getDeleteInterface();
    public abstract void clearActionMode();


    protected final MediaBrowserCompat.SubscriptionCallback subscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    super.onChildrenLoaded(parentId, children);
                }

                @Override
                public void onError(@NonNull String parentId) {
                    super.onError(parentId);
                }
            };

}
