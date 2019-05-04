package reverblabs.apps.aura.ui.adapters.playlist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.playlist.PlaylistDetailFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Playlist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class AdapterForPlaylistAllSongs extends RecyclerView.Adapter<AdapterForPlaylistAllSongs.ViewHolder> implements DeleteInterFace {

    private ArrayList<Song> dataset = new ArrayList<>();
    private ContentResolver cr;
    private Context context;

    Cursor cursor;

    private AdapterToFragmentCallbacks fragmentCallbacks;

    public boolean actionMode = false;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    //private ImageFetcher imageFetcher;

    private String playlistName = null;

    private long playlistId;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {
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

                    if (playlistName.equals(context.getString(R.string.recently_added)) ||
                            playlistName.equals(context.getString(R.string.recently_played))){

                        inflater.inflate(R.menu.menu_song, popupMenu.getMenu());
                    }
                    else {
                        inflater.inflate(R.menu.menu_user_playlist, popupMenu.getMenu());
                    }

                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.show();
                } else {
                    fragmentCallbacks.playSong(getAdapterPosition(), dataset);
                }
            }else {
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

                    Bundle navigateArtist = new Bundle();
                    navigateArtist.putString("artist",song.getArtist());
                    navigateArtist.putLong("id",song.ArtistId);

                    fragmentCallbacks.createArtistDetailFragment(navigateArtist);

                    return true;

                case R.id.go_to_album:

                    Bundle bundle = new Bundle();
                    bundle.putLong("albumid",song.getAlBumId());
                    bundle.putString("album",song.getAlbum());
                    bundle.putString("artist",song.getArtist());
                    bundle.putString("year",song.Year);

                    fragmentCallbacks.createAlbumDetailFragment(bundle);

                    return true;

                case R.id.delete:
                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(posOfItemDeleted);

                    ArrayList<Song> delete = new ArrayList<>();
                    delete.add(song);

                    fragmentCallbacks.createDeleteDialog(song, delete, pos,
                            getDeleteInterface());

                    return true;

                case R.id.details:
                    fragmentCallbacks.createDetailsDialog(song.getPath());

                    return true;

                case R.id.remove:
                    if (playlistName.equals(context.getString(R.string.favourites)) && playlistId == -3){
                        dataset.remove(getAdapterPosition());
                        SharedPrefsFile.saveFavouritesPlaylist(context, dataset);
                    }
                    else {
                        Playlist playlist = new Playlist();
                        playlist.removeFromPlaylist(cr, playlistId,song.ID );
                    }
                    notifyItemRemoved(dataset.indexOf(song));
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


    public AdapterForPlaylistAllSongs(Activity mcontext, ContentResolver contentResolver,
                                      PlaylistDetailFragment object, Cursor dataCursor, String mName, long mPlaylistId) {
        cr = contentResolver;
        context = mcontext;
        fragmentCallbacks = object;
        cursor = dataCursor;
        playlistName = mName;
        playlistId = mPlaylistId;

        //imageFetcher = Utils.getImageFetcher(mcontext);
    }

    public void setDataSet(ArrayList<Song> temp){
        if(temp!=null) {
            dataset = temp;
        }
    }

    @Override
    @NonNull
    public AdapterForPlaylistAllSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_list, parent, false);
        return new AdapterForPlaylistAllSongs.ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor, String name){
        if(cursor == mCursor){
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        dataset.clear();

        if (name.equals(context.getString(R.string.recently_added))) {

            dataset = MusicHelper.extractSongsFromCursor(cursor);
        }
        else {

            if (cursor != null) {
                cursor.moveToFirst();

                if (cursor.getCount() > 0) {


                    int id = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
                    int artist_id = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID);
                    int album = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION);
                    int year = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.YEAR);

                    do {
                        Song song = new Song(cursor.getLong(id),
                                cursor.getString(data),
                                cursor.getString(artist),
                                cursor.getLong(artist_id),
                                cursor.getString(album),
                                cursor.getLong(album_id),
                                cursor.getString(title),
                                cursor.getInt(duration),
                                cursor.getString(year));
                        dataset.add(song);
                    } while (cursor.moveToNext());
                }
            }
        }

        if (dataset != null) {
            this.notifyDataSetChanged();
        }

        return oldCursor;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForPlaylistAllSongs.ViewHolder holder, int position) {
        holder.title.setText(dataset.get(position).Title);
        holder.subtitle.setText(dataset.get(position).Artist);
        holder.overflow.setImageResource(R.drawable.overflow);

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

        holder.duration.setText(MusicHelper.getDurationInSeconds(context,
                dataset.get(position).getDuration()/1000));



//        imageFetcher.loadImage(dataSet.get(position).getAlBumId(), dataSet.get(position).getAlbum(),
//                dataSet.get(position).getArtist(), "Song", false, holder.imageView, Constants.SMALL);

        holder.itemView.setActivated(selectedItems.get(position, false));
    }


    public void afterItemDelete(int i){
        if (dataset != null) {
            dataset.remove(i);
            notifyItemRemoved(i);;
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
