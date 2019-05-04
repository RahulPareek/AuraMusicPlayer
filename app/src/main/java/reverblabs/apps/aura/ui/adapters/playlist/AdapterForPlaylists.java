package reverblabs.apps.aura.ui.adapters.playlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.playlist.PlaylistsFragment;
import reverblabs.apps.aura.model.Playlist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;

import static reverblabs.apps.aura.R.drawable.playlist;


public class AdapterForPlaylists extends RecyclerView.Adapter<AdapterForPlaylists.ViewHolder> {


    private ArrayList<Playlist> playlistDataset = new ArrayList<>();
    private Context context;

    private Cursor cursor;

    private AdapterToFragmentCallbacks fragmentCallbacks;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView name;
        TextView count;
        ImageView imageView;
        ImageView overflow;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.playlist_name);
            count = v.findViewById(R.id.playlist_count);
            imageView = v.findViewById(R.id.image_playlists);
            overflow = v.findViewById(R.id.overflow_playlists);
            overflow.setOnClickListener(this);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(view==overflow){
                if(!(playlistDataset.get(getAdapterPosition()).name).equals(context.getString(R.string.recently_added))
                        && !(playlistDataset.get(getAdapterPosition()).name).equals(context.getString(R.string.recently_played))
                        && !(playlistDataset.get(getAdapterPosition()).name).equals(context.getString(R.string.favourites))) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.menu_playlist, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.show();
                }
            }
            else {
                if(playlistDataset.get(getAdapterPosition()).name != null) {
                    String clickedPlaylist = playlistDataset.get(getAdapterPosition()).name;
                    long id = playlistDataset.get(getAdapterPosition()).playlistId;
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PLAYLIST_NAME, clickedPlaylist);
                    bundle.putLong(Constants.PLAYLIST_ID, id);

                    fragmentCallbacks.createPlaylistDetailFragment(bundle);
                }
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item){
            int posOfItemDeleted;
            Playlist playlist = playlistDataset.get(getAdapterPosition());
            posOfItemDeleted = playlistDataset.indexOf(playlist);
            switch (item.getItemId()){

                case R.id.add_to_queue_playlist:
                   ArrayList<Song> temp = playlist.getAllSongsOfPlaylist(context.getContentResolver(), playlist.getId());
                    fragmentCallbacks.addToQueue(temp);
                    return true;

                case R.id.delete_playlist:
                    Log.i(Constants.TAG, Long.toString(playlist.getId()));
                    playlist.deletePlaylist(context.getContentResolver(), playlist.getId());
                    notifyItemRemoved(posOfItemDeleted);

                default:
                    return false;
            }
        }
    }

    public AdapterForPlaylists( Context cx, Cursor dataCusrsor, PlaylistsFragment fragment ){
        context = cx;
        cursor = dataCusrsor;
        fragmentCallbacks = fragment;
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

        if(cursor!=null){
            int idcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
            int namecolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);

            playlistDataset.clear();


            Playlist recentlyAdded = new Playlist(-1, context.getString(R.string.recently_added), Playlist.getNoOfSongsInRecentlyAdded(context.getContentResolver()));
            playlistDataset.add(recentlyAdded);


            Playlist recentlyPlayed = new Playlist(-2, context.getString(R.string.recently_played), Playlist.getSongsInRecentlyPlayed(context));
            playlistDataset.add(recentlyPlayed);


            Playlist favourites = new Playlist(-3,context.getString(R.string.favourites), Playlist.getSongsInFavourites(context));
            playlistDataset.add(favourites);
            if (cursor.getCount() > 0) {

                do {

                    int count = Playlist.getNoOfSongsInPlaylist(context.getContentResolver(), cursor.getLong(0));

                    Playlist playlist = new Playlist(cursor.getLong(idcolumn),
                            cursor.getString(namecolumn), count);
                    playlistDataset.add(playlist);


                } while (cursor.moveToNext());
            }

            this.notifyDataSetChanged();
        }
        return oldCursor;
    }


    @Override
    public AdapterForPlaylists.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_playlists, parent, false);
        return new AdapterForPlaylists.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterForPlaylists.ViewHolder holder, int position) {



        if(playlistDataset.get(position).name != null) {
            holder.name.setText(playlistDataset.get(position).name);
            int noOfSongs = playlistDataset.get(position).noOfSongs;
            if (noOfSongs == 1) {
                holder.count.setText(Integer.toString(noOfSongs) + " " + context.getString(R.string.song));
            } else {
                holder.count.setText(Integer.toString(noOfSongs) + " " + context.getString(R.string.songs));
            }

            Log.i(Constants.TAG, "Playlist in bindView holder" + playlistDataset.get(position).name);

            holder.imageView.setImageResource(playlist);

            if ((playlistDataset.get(position).name).equals(context.getString(R.string.recently_added))
                    || (playlistDataset.get(position).name).equals(context.getString(R.string.recently_played))
                    || (playlistDataset.get(position).name).equals(context.getString(R.string.favourites))) {
                holder.overflow.setVisibility(View.GONE);

            } else {
                holder.overflow.setImageResource(R.drawable.overflow);
            }
        }
    }

    public void notifyNewPlaylistCreated(long id, String name){
        Playlist playlist = new Playlist(id, name, 0);
        playlistDataset.add(playlist);

        notifyItemRangeInserted(playlistDataset.size() - 1, 1);
    }

    @Override
    public int getItemCount() {
        if (playlistDataset != null) {
            return playlistDataset.size();
        }else {
            return 0;
        }
    }

}
