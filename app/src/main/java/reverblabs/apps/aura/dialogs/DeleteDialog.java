package reverblabs.apps.aura.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class DeleteDialog extends DialogFragment implements DialogInterface.OnClickListener {

    Song song;

    Context context;

    public DeleteInterFace deleteInterFace;

    public DeleteDialog(){}

    ArrayList<Song> songList = new ArrayList<>();
    ArrayList<Integer> position = new ArrayList<>();

    String name;

    String action = null;


    public void initDeleteDialog(ArrayList<Song> temp, String value, String act, Context cx, ArrayList<Integer> pos){
        songList.clear();
        position.clear();

        songList = temp;
        name = value;
        action = act;
        context = cx;

        position = pos;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());

        if(action.equals("set")){

        builder.setTitle(context.getString(R.string.delete_message) + " " + name + " ?")
                .setPositiveButton(R.string.yes, this).
                setNegativeButton(R.string.no, this);
        }
        else if(action.equals("single")) {
            song = songList.get(0);
            builder.setTitle(context.getString(R.string.delete_message) + " " + song.getTitle() + " ?")
                    .setPositiveButton(R.string.yes, this).
                    setNegativeButton(R.string.no, this);

        }

        else if(action.equals("multiple")){

            builder.setTitle(context.getString(R.string.delete_selected_songs) + " ?")
                    .setPositiveButton(R.string.yes, this).
                    setNegativeButton(R.string.no, this);
        }
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which){
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialogInterface.dismiss();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
                deleteAsyncTask.execute();
                break;
        }
    }

    public class DeleteAsyncTask extends AsyncTask<Void, Void ,Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {

            boolean result;
            for(int i = 0; i< songList.size(); i++) {

                String selection = MediaStore.Audio.Media.DATA + "=?";
                String[] selectionArgs = new String[]{songList.get(i).getPath()};

                Song currentDeleted = songList.get(i);

                ArrayList<Song> favourites = SharedPrefsFile.getFavouritesPlaylist(context);
                if(favourites!=null) {

                    for (int j = 0; j < favourites.size() ; j++){
                        if (favourites.get(j).getID().equals(currentDeleted.getID())){
                            favourites.remove(j);
                        }
                    }
                    SharedPrefsFile.saveFavouritesPlaylist(context, favourites);
                }

                ArrayList<Song> recentlyPlayed = SharedPrefsFile.getRecentlyPlayedPlaylist(context);
                if(recentlyPlayed != null) {

                    for (int j = 0; j < recentlyPlayed.size() ; j++){
                        if (recentlyPlayed.get(j).getID().equals(currentDeleted.getID())){
                            recentlyPlayed.remove(j);
                        }
                    }

                    SharedPrefsFile.saveRecentlyPlayedPlaylist(context, recentlyPlayed);
                }

       //         ArrayList<Song> queue = HelperClass.auraMusicService.getSongArrayList();
//                if(queue != null){
//
//                    for (int j = 0; j < queue.size() ; j++){
//                        if (queue.get(j).getID().equals(currentDeleted.getID())){
//
//                            if (currentDeleted.getID().equals(HelperClass.auraMusicService.getNowPlaying().getID())){
//                                int currentPos = HelperClass.auraMusicService.getNowPlayingPosition();
//                                if (queue.size() > 1){
//
//                                    if ((currentPos + 1)!= queue.size()){
//                                        Song newSong = queue.get(currentPos + 1);
//                                        HelperClass.auraMusicService.setNowPlaying(currentPos + 1, newSong);
//                                    }
//                                    else if (!((currentPos - 1) < 0)){
//                                        Song newSong = queue.get(currentPos - 1);
//                                        HelperClass.auraMusicService.setNowPlaying(currentPos - 1, newSong);
//                                    }
//
//                                }
//                            }
//
//                            queue.remove(j);
//                        }
//                    }
//
//                    HelperClass.auraMusicService.setMusicPlayerList(queue);
//
//                }



                context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);



                File file = new File(songList.get(i).getPath());
                try{
                    result = file.delete();
                    if(!result){

                    }
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result) {
                for (int i = 0; i < position.size(); i++) {
                    deleteInterFace.afterItemDelete(position.get(i));
                }
            }
        }
    }

}
