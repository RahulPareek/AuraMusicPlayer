package reverblabs.apps.aura.ui.adapters.genre;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import reverblabs.apps.aura.R;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.genre.GenreDetailFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class AdapterForGenreAllSongs extends RecyclerView.Adapter<AdapterForGenreAllSongs.ViewHolder> implements DeleteInterFace {
    private ArrayList<Song> dataset = new ArrayList<>();
    private Context context;

    private Cursor cursor;

    public boolean actionMode = false;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    private AdapterToFragmentCallbacks fragmentCallbacks;

    //private ImageFetcher imageFetcher;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, View.OnLongClickListener  {
        TextView title;
        TextView subtitle;
        TextView duration;
        ImageView albumArt;
        ImageView overflow;

        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            subtitle =v.findViewById(R.id.subtitle);
            albumArt = v.findViewById(R.id.album_art);
            duration = v.findViewById(R.id.duration);
            overflow = v.findViewById(R.id.overflow);
            overflow.setOnClickListener(this);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if(!actionMode) {
                if (view == overflow) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.menu_song, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.show();
                } else {
                    fragmentCallbacks.playSong(getAdapterPosition(), dataset);
//
//                    Song song = dataset.get(getAdapterPosition());
//                    int position = dataset.indexOf(song);
//
//                    boolean value = SharedPrefsFile.getShuffleMode(context);
//
//                    HelperClass.auraMusicService.setMusicPlayerList(dataset);
//                    HelperClass.auraMusicService.setNowPlaying(position, song);
//                    if (value) {
//                        HelperClass.auraMusicService.handleShuffleMode();
//                    }
//                    HelperClass.auraMusicService.createMediaPlayer();
//                    HelperClass.auraMusicService.startPlayingSong();
                }
            }
            else {
                toggleSelection(getAdapterPosition());
                fragmentCallbacks.setActionModeCount(getSelectedItemCount());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item){

            Song song = dataset.get(getAdapterPosition());
            int posOfItemDeleted = dataset.indexOf(song);

            switch (item.getItemId()){
                case R.id.play_next:
//                    HelperClass.auraMusicService.handlePlayNext(song);
                    fragmentCallbacks.addToQueue(song);
                    return true;

                case R.id.add_to_queue:
                    fragmentCallbacks.addToQueue(song);
                    return true;

                case R.id.add_to_playlist:
                    ArrayList<Song> temp = new ArrayList<>();
                    temp.add(song);
                    fragmentCallbacks.createPlaylistDialog(temp);
                    return true;

                case R.id.edit_tags:
                    fragmentCallbacks.launchEditTagsActivity(song);

                    return true;

                case R.id.go_to_artist:

                    Bundle artistbundle = new Bundle();
                    artistbundle.putString("artist",song.getArtist());
                    artistbundle.putLong("id",song.ArtistId);

                    fragmentCallbacks.createArtistDetailFragment(artistbundle);

                    return true;

                case R.id.go_to_album:

                    Bundle bundle = new Bundle();
                    bundle.putLong("albumid",song.getAlBumId());
                    bundle.putString("album",song.getAlbum());
                    bundle.putString("artist",song.getArtist());
                    bundle.putString("year",song.Year);

                    fragmentCallbacks.createAlbumDetailFragment(bundle);

                    return true;

                case R.id.details:
                    fragmentCallbacks.createDetailsDialog(song.getPath());

                    return true;

                case R.id.delete:
                    ArrayList<Song> delete = new ArrayList<>();
                    delete.add(song);

                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(posOfItemDeleted);

                    fragmentCallbacks.createDeleteDialog(song, delete, pos,
                            getDeleteInterface());

                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean onLongClick(View view) {

            actionMode = true;

            fragmentCallbacks.setupActionMode();

            toggleSelection(getAdapterPosition());

            fragmentCallbacks.setActionModeCount(1);

            return true;
        }
    }


    public DeleteInterFace getDeleteInterface(){
        return this;
    }

    public void toggleSelection(int pos){

        if (selectedItems.get(pos, false)){
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }

        notifyItemChanged(pos);
    }

    public void clearSelections(){
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return selectedItems.size();
    }

    public ArrayList<Integer> getPositionOfSelectedItems(){
        ArrayList<Integer> songsid = new ArrayList<>(selectedItems.size());
        for (int i = 0; i <selectedItems.size() ; i++){
            songsid.add(selectedItems.keyAt(i));
            Log.i(Constants.TAG, Integer.toString(selectedItems.keyAt(i)));
        }

        return songsid;
    }

    public ArrayList<Song> getActionModeSelectedItems(){
        ArrayList<Integer> songsid = new ArrayList<>(selectedItems.size());
        ArrayList<Song> songs = new ArrayList<>(selectedItems.size());

        for (int i = 0; i <selectedItems.size() ; i++){
            songsid.add(selectedItems.keyAt(i));
            Log.i(Constants.TAG, Integer.toString(selectedItems.keyAt(i)));
        }

        for (int i = 0; i <selectedItems.size(); i++){
            songs.add(dataset.get(selectedItems.keyAt(i)));
        }

        return songs;
    }


    public ArrayList<Song> getGenreAllSongDataSet(){
        return dataset;
    }


    public AdapterForGenreAllSongs(Activity mcontext, Cursor dataCursor, GenreDetailFragment object) {
        context = mcontext;
        cursor = dataCursor;
        fragmentCallbacks = object;

       // imageFetcher = Utils.getImageFetcher(mcontext);
    }


    @Override
    @NonNull
    public AdapterForGenreAllSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_list, parent, false);
        return new ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor){
        if(cursor == mCursor){
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        try{
            cursor.moveToFirst();
        }
        catch (NullPointerException e){
        }

        if (cursor != null && cursor.getCount() > 0) {
            dataset.clear();

            dataset = MusicHelper.extractSongsFromCursor(cursor);

            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GlideApp.with(context)
                .load(new AlbumImage(dataset.get(position).getArtist(),
                        dataset.get(position).getAlbum(),
                        (int)  dataset.get(position).getAlBumId()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(holder.albumArt);

//        imageFetcher.loadImage(genreAllSongDataSet.get(position).getAlBumId(), genreAllSongDataSet.get(position).getAlbum(),
//                genreAllSongDataSet.get(position).getArtist(), "Song", false, holder.imageView, Constants.SMALL);

        holder.title.setText(dataset.get(position).Title);
        holder.subtitle.setText(dataset.get(position).Artist);

        holder.overflow.setImageResource(R.drawable.overflow);

        holder.duration.setText(MusicHelper.getDurationInSeconds(context,
                dataset.get(position).getDuration()/1000));


        holder.itemView.setActivated(selectedItems.get(position, false));
    }


    public void afterItemDelete(int i){
        if (dataset != null) {
            dataset.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, dataset.size());
        }
    }

    @Override
    public int getItemCount() {
        if (dataset != null) {
            return dataset.size();
        }else {
            return 0;
        }
    }
}


