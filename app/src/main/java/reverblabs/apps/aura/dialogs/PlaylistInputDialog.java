package reverblabs.apps.aura.dialogs;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.PlaylistCallback;
import reverblabs.apps.aura.model.Playlist;

public class PlaylistInputDialog extends DialogFragment implements DialogInterface.OnClickListener, TextWatcher {

    private PlaylistCallback playlistCallback;

    private EditText editText;

    private String initialText = "";

    private AlertDialog alertDialog;

    public void initPlaylistInputDialog(PlaylistCallback temp){
        playlistCallback = temp;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.addTextChangedListener(this);

        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.playlist_name)).setView(editText).setPositiveButton(R.string.create, this).
                setNegativeButton(R.string.cancel, this);

        alertDialog = builder.create();
        try {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return alertDialog;
    }

    public void onStart(){
        super.onStart();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        editText.setText(initialText);
        editText.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable s){

    }

    @Override
    public void beforeTextChanged(CharSequence sequence, int start, int count, int after){

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count){
        String string = text.toString();

        if (string.equals(initialText)) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
        else{
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            ContentResolver contentResolver = getContext().getContentResolver();

            Playlist playlist = new Playlist();
            long res = playlist.getPlaylist(contentResolver, string);

            if(res!= -1){
                Toast.makeText(getContext(), getString(R.string.playlist_already_exists), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which){
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialogInterface.dismiss();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                playlistCallback.onCreateNewPlaylist(editText.getText().toString());
        }
    }


}
