package reverblabs.apps.aura.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.PlaylistCallback;
import reverblabs.apps.aura.model.Playlist;
import reverblabs.apps.aura.utils.Constants;

public class PlaylistDialog extends DialogFragment implements DialogInterface.OnClickListener {

    Playlist playlist;

    ArrayList<Playlist> playlistList =new ArrayList<>();

    String[] playlistNames;
    long[] playlistId;

    public PlaylistCallback playlistCallback;

    public PlaylistDialog(){

    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);

        playlist = new Playlist();

        playlistList = playlist.getAllPlaylists(getActivity().getContentResolver());
        playlistNames = new String[playlistList.size() + 2];
        playlistId = new long[playlistList.size() + 2];

        playlistNames[0] = getString(R.string.create_new_playlist);
        playlistNames[1] = getString(R.string.favourites);

        playlistId[0] = -99;
        playlistId[1] = -3;


        for(int i=0; i<playlistList.size(); i++){
            playlistNames[i + 2] = playlistList.get(i).name;
            playlistId[i + 2] = playlistList.get(i).getId();

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_to_playlist)).setItems(playlistNames, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which){
        switch (which){
            case 0:
                PlaylistInputDialog playlistInputDialog = new PlaylistInputDialog();
                playlistInputDialog.initPlaylistInputDialog(playlistCallback);
                playlistInputDialog.show(getFragmentManager(), Constants.TAG);
                break;

            default:
                long id = playlistId[which];
                String name = playlistNames[which];
                playlistCallback.onAddToExistingPlaylist(id, name);

        }
    }
}
