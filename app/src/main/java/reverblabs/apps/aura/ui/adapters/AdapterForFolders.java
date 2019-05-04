package reverblabs.apps.aura.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.FoldersFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;


public class AdapterForFolders extends RecyclerView.Adapter<AdapterForFolders.ViewHolder>
        implements DeleteInterFace {

    private ArrayList<File> fileSet = new ArrayList<>();
    private Context context;

    private ArrayList<File> temp = new ArrayList<>();

    private FoldersFragment foldersFragment;
    private AdapterToFragmentCallbacks fragmentCallbacks;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener  {
        TextView file_name;
        TextView other_details;
        ImageView imageView;
        ImageView overflow;

        ViewHolder(View v) {
            super(v);
            file_name =  v.findViewById(R.id.firstline_songs);
            other_details =  v.findViewById(R.id.secondline_songs);
            imageView =  v.findViewById(R.id.image_folders_files);
            overflow =  v.findViewById(R.id.overflow);
            overflow.setOnClickListener(this);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (view == overflow) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater inflater = popupMenu.getMenuInflater();

                File file = fileSet.get(getAdapterPosition());
                if (file.isDirectory()){
                    inflater.inflate(R.menu.menu_directory, popupMenu.getMenu());
                }
                else {
                    inflater.inflate(R.menu.menu_file, popupMenu.getMenu());
                }

                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();

            } else {

                File file = fileSet.get(getAdapterPosition());

                if (file.isDirectory()){
                    foldersFragment.navigateToDirectory(file);
                }
                else {
                    GetSongFromPath getSongFromPath = new GetSongFromPath("play_all_songs", file);
                    getSongFromPath.execute();
                }
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item){

            temp.clear();

            File file = fileSet.get(getAdapterPosition());

            switch (item.getItemId()){
                case R.id.play_next:
                    fragmentCallbacks.playNext(MusicHelper.getSongFromPath(file.getAbsolutePath(), context));

                    return true;

                case R.id.add_to_queue:
                    if (file.isDirectory()) {
                        GetSongFromPath getSongFromPathaddToQueue = new GetSongFromPath("add_to_queue", file);
                        getSongFromPathaddToQueue.execute();
                    }else {
                        ArrayList<Song> add_to_queue = new ArrayList<>();
                        add_to_queue.add(MusicHelper.getSongFromPath(file.getAbsolutePath(), context));
                        fragmentCallbacks.addToQueue(add_to_queue);
                    }

                    return true;

                case R.id.add_to_playlist:
                    if (file.isDirectory()){
                        GetSongFromPath getSongFromPathPlaylist = new GetSongFromPath("add_to_playlist", file);
                        getSongFromPathPlaylist.execute();
                    }else {
                        ArrayList<Song> addToPlaylist = new ArrayList<>();
                        addToPlaylist.add(MusicHelper.getSongFromPath(file.getAbsolutePath(), context));
                        fragmentCallbacks.createPlaylistDialog(addToPlaylist);
                    }

                    return true;

                case R.id.go_to_artist:


                    Song artistSong = MusicHelper.getSongFromPath(file.getAbsolutePath(), context);

                    if (artistSong != null) {

                        Bundle artistbundle = new Bundle();
                        artistbundle.putString("artist", artistSong.getArtist());
                        artistbundle.putLong("id", artistSong.ArtistId);

                        fragmentCallbacks.createArtistDetailFragment(artistbundle);
                    }

                    return true;

                case R.id.go_to_album:

                    Song albumSong = MusicHelper.getSongFromPath(file.getAbsolutePath(), context);
                    if (albumSong != null) {

                        Bundle bundle = new Bundle();
                        bundle.putLong("albumid", albumSong.getAlBumId());
                        bundle.putString("album", albumSong.getAlbum());
                        bundle.putString("artist", albumSong.getArtist());
                        bundle.putString("year", albumSong.Year);

                        fragmentCallbacks.createAlbumDetailFragment(bundle);
                    }

                    return true;

                case R.id.set_as_start_directory:
                    SharedPrefsFile.setUserPreferredFolder(file.getPath(), context);
                    return true;


                case R.id.delete:
                    if (file.isDirectory()){
                        ArrayList<Song> deleteDir = extractSongsFromFilesList(getAudioFiles(file));

                        ArrayList<Integer> pos = new ArrayList<>();
                        pos.add(getAdapterPosition());

                        foldersFragment.createDeleteDialog(file.getName(), deleteDir, pos,
                                getDeleteInterface());
                    }
                    else {
                        Song song = MusicHelper.getSongFromPath(file.getAbsolutePath(), context);

                        if (song != null) {

                            ArrayList<Song> delete = new ArrayList<>();
                            delete.add(song);

                            ArrayList<Integer> pos = new ArrayList<>();
                            pos.add(fileSet.indexOf(file));

                            foldersFragment.createDeleteDialog(song.getTitle(), delete, pos,
                                    getDeleteInterface());
                        }
                    }

                    return true;

                default:
                    return false;
            }
        }
    }


    public AdapterForFolders(Context mcontext, FoldersFragment object, String mPath) {
        context = mcontext;
        foldersFragment = object;
        fragmentCallbacks = object;

        fileSet = MusicHelper.getAudioFiles(new File(mPath));
    }

    public DeleteInterFace getDeleteInterface(){
        return this;
    }


    private ArrayList<Song> extractSongsFromFilesList(final ArrayList<File> fileArrayList){

        final ArrayList<Song> temp = new ArrayList<>();

        for (File file:fileArrayList){
            temp.add(MusicHelper.getSongFromPath(file.getAbsolutePath(), context));
        }

        return temp;
    }

    @Override
    public AdapterForFolders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_folders, parent, false);
        return new AdapterForFolders.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterForFolders.ViewHolder holder, int position) {
        File file = fileSet.get(position);

        holder.file_name.setText(file.getName());

        if (file.isDirectory()){
            holder.imageView.setImageResource(R.drawable.folders);
            holder.other_details.setVisibility(View.GONE);
        }
        else {
            holder.imageView.setImageResource(R.drawable.album_art);
            holder.other_details.setVisibility(View.VISIBLE);
            double size = getFileSize(file)/1024;
            if (size/1024 > 1){
                holder.other_details.setText(String.format(context.getString(R.string.file_size_in_mb), size/1024.0));
            }
            else {
                holder.other_details.setText(String.format(context.getString(R.string.file_size_in_kb), size));
            }
        }

        holder.overflow.setImageResource(R.drawable.overflow);
    }


    private long getFileSize(File file){
        return file.length();
    }


    private ArrayList<File> getAudioFiles(File file){
        temp.addAll(MusicHelper.getAudioFiles(file));

        ArrayList<File> audioFiles = (ArrayList<File>) temp.clone();

        for (File file1:audioFiles){
            if (file1.isDirectory()){
                if (temp.contains(file1)) {
                    temp.remove(file1);

                    getAudioFiles(file1);
                }
            }
        }

        return temp;
    }


    @Override
    public void afterItemDelete(int i){
        if (fileSet != null) {
            fileSet.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, fileSet.size());
        }
    }

    @Override
    public int getItemCount() {
        if (fileSet != null) {
            return fileSet.size();
        }else {
            return 0;
        }
    }

    public class GetSongFromPath extends AsyncTask<Void, Void, ArrayList<Song>>{

        String action;
        File file;

        GetSongFromPath(String mAction, File mFile){
            action = mAction;
            file = mFile;
        }

        @Override
        protected ArrayList<Song> doInBackground(Void... params){

            if (action.equals("play_all_songs")){
                ArrayList<Song> queue = new ArrayList<>();


                for (File file1:fileSet) {
                    if (file1.isDirectory()) {

                    } else {
                        queue.add(MusicHelper.getSongFromPath(file1.getAbsolutePath(), context));

                    }
                }

                return queue;
            }else {

                return extractSongsFromFilesList(getAudioFiles(file));
            }
        }

        @Override
        public void onPostExecute(ArrayList<Song> result){

            if (action.equals("play_all_songs")){
                fragmentCallbacks.play(result);
            }
            else if (action.equals("play")){
                fragmentCallbacks.play(result);
            }
            else if (action.equals("add_to_playlist")){
                foldersFragment.createPlaylistDialog(result);
            }
            else if (action.equals("add_to_queue")){
                fragmentCallbacks.addToQueue(result);
            }

        }
    }
}
