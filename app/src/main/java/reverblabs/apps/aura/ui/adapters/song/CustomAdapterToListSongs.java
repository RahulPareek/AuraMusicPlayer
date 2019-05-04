package reverblabs.apps.aura.ui.adapters.song;

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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.song.SongsFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class CustomAdapterToListSongs extends RecyclerView.Adapter<CustomAdapterToListSongs.ViewHolder>
        implements DeleteInterFace, FastScrollRecyclerView.SectionedAdapter{

    private ArrayList<Song> dataSet = new ArrayList<>();
    private Context context;

    public AdapterToFragmentCallbacks fragmentCallbacks;

    private Cursor cursor;

    public boolean actionMode = false;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                    PopupMenu.OnMenuItemClickListener,
                                                    View.OnLongClickListener {
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
                    fragmentCallbacks.playSong(getAdapterPosition(), dataSet);
                }
            }

            else {

                toggleSelection(getAdapterPosition());
                fragmentCallbacks.setActionModeCount(getSelectedItemCount());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Song song = dataSet.get(getAdapterPosition());

            switch (item.getItemId()) {
                case R.id.play_next:
                    fragmentCallbacks.playNext(song);
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
                    pos.add(dataSet.indexOf(song));

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
            songs.add(dataSet.get(selectedItems.keyAt(i)));
        }

        return songs;
    }


    public ArrayList<Song> getDataSet(){
        return dataSet;
    }


    public CustomAdapterToListSongs(Activity mcontext, Cursor dataCursor, SongsFragment object) {
        context = mcontext;
        cursor = dataCursor;
        fragmentCallbacks = object;

       // imageFetcher = Utils.getImageFetcher(mcontext);
    }


    @Override
    @NonNull
    public CustomAdapterToListSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_list, parent, false);
        return new ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor){
        if(cursor == mCursor){
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        dataSet = MusicHelper.extractSongsFromCursor(cursor);
        if (dataSet != null){
            notifyDataSetChanged();
        }

        return oldCursor;
    }

    @NonNull
    public String getSectionName(int pos){
        return dataSet.get(pos).getTitle().substring(0,1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GlideApp.with(context)
                .load(new AlbumImage(dataSet.get(position).getArtist(),
                        dataSet.get(position).getAlbum(),
                        (int)  dataSet.get(position).getAlBumId()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(holder.albumArt);

//        imageFetcher.loadImage(dataSet.get(position).getAlBumId(), dataSet.get(position).getAlbum(),
//                dataSet.get(position).getArtist(), "Song", false, holder.imageView, Constants.SMALL);

        holder.title.setText(dataSet.get(position).Title);
        holder.subtitle.setText(dataSet.get(position).Artist);
        holder.overflow.setImageResource(R.drawable.overflow);

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.duration.setText(MusicHelper.getDurationInSeconds(context,
                                            dataSet.get(position).getDuration()/1000));
   }

    @Override
     public void afterItemDelete(int i){
        if (dataSet != null) {
            dataSet.remove(i);
            notifyItemRemoved(i);
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        }else {
            return 0;
        }
    }

}


