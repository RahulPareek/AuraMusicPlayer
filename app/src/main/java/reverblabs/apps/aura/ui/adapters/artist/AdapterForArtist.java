package reverblabs.apps.aura.ui.adapters.artist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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
import reverblabs.apps.aura.glide.artist.ArtistImage;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.artist.ArtistFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class AdapterForArtist extends RecyclerView.Adapter<AdapterForArtist.ViewHolder> implements DeleteInterFace,
         FastScrollRecyclerView.SectionedAdapter{

    private ArrayList<Artist> artistDataset = new ArrayList<>() ;
    private ContentResolver cr;
    private Context context;

    private Cursor cursor;

    private AdapterToFragmentCallbacks fragmentCallbacks;

    //private ImageFetcher imageFetcher;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView album_art;
        TextView artist_name;
        TextView no_of_songs;
        ImageView overflow;

        ViewHolder(View v) {
            super(v);
            album_art =v.findViewById(R.id.artist_card_image);
            artist_name = v.findViewById(R.id.card_artist_name);
            no_of_songs = v.findViewById(R.id.artist_no_of_songs_albums);
            overflow = v.findViewById(R.id.overflow_artist);
            overflow.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == overflow) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_album_artist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            } else {

                Artist artist = artistDataset.get(getAdapterPosition());

                Bundle bundle = new Bundle();
                bundle.putString("artist", artist.artist);
                bundle.putLong("id", artist.id);
                fragmentCallbacks.createArtistDetailFragment(bundle);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Artist artist = artistDataset.get(getAdapterPosition());

            ArrayList<Song> songs = MusicHelper.getArtistSongs(artist.artist, cr);

            int posOfItemDeleted = artistDataset.indexOf(artist);

            switch (item.getItemId()) {
                case R.id.play:
//
//                    HelperClass.auraMusicService.setMusicPlayerList(songs);
//                    HelperClass.auraMusicService.setNowPlaying(0, songs.get(0));
//                    HelperClass.auraMusicService.createMediaPlayer();
//                    HelperClass.auraMusicService.startPlayingSong();
//
                    fragmentCallbacks.play(songs);
                    return true;
//
                case R.id.add_to_queue:
                    fragmentCallbacks.addToQueue(songs);
//                    HelperClass.auraMusicService.addToQueue(songs);
                    return true;

                case R.id.add_to_playlist:
                    fragmentCallbacks.createPlaylistDialog(songs);
                    return true;

                case R.id.delete:

                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(posOfItemDeleted);

                    fragmentCallbacks.createDeleteDialog(artist, songs, pos,
                            getDeleteInterface());
                    return true;

                default:
                    return false;
            }

        }
    }


    public DeleteInterFace getDeleteInterface(){
        return this;
    }


    public AdapterForArtist(ContentResolver contentResolver, Activity cx, Cursor dataCursor, ArtistFragment fragment){
        artistDataset.clear();
        cr = contentResolver;
        context = cx;
        cursor = dataCursor;
        fragmentCallbacks = fragment;

        //imageFetcher = Utils.getImageFetcher(cx);
    }


    @Override
    public AdapterForArtist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_artists, parent,false);
        return new ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor){
        if(cursor == mCursor){
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        artistDataset = MusicHelper.extractArtistFromCursor(cursor);
        if(artistDataset != null){
            notifyDataSetChanged();
        }

        return oldCursor;
    }


    @NonNull
    public String getSectionName(int pos){
        return artistDataset.get(pos).artist.substring(0,1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GlideApp.with(context)
                .load(new ArtistImage(artistDataset.get(position).getArtist(),
                        (int) artistDataset.get(position).getid()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(holder.album_art);

//        imageFetcher.loadImage(artistDataset.get(position).id, null,
//               artistDataset.get(position).getArtist(), "Artist", false, holder.album_art, Constants.MEDIUM);


        if (artistDataset.get(position).artist.equals("<unknown>")){
            holder.artist_name.setText(" ");
        }else {
            holder.artist_name.setText(artistDataset.get(position).artist);
        }

        if(artistDataset.get(position).getNoOfSongs() == 1 && artistDataset.get(position).getNoOfAlbums() == 1) {
            holder.no_of_songs.setText(String.format(context.getString(R.string.one_song_one_album),
                    artistDataset.get(position).getNoOfSongs(), artistDataset.get(position).getNoOfAlbums()));
        }
        else if(artistDataset.get(position).getNoOfSongs() > 1 && artistDataset.get(position).getNoOfAlbums() == 1) {
            holder.no_of_songs.setText(String.format(context.getString(R.string.no_of_song_one_album),
                    artistDataset.get(position).getNoOfSongs(), artistDataset.get(position).getNoOfAlbums()));
        }
        else if(artistDataset.get(position).getNoOfSongs() == 1 && artistDataset.get(position).getNoOfAlbums() > 1) {
            holder.no_of_songs.setText(String.format(context.getString(R.string.one_song_no_of_album),
                    artistDataset.get(position).getNoOfSongs(), artistDataset.get(position).getNoOfAlbums()));
        }
        else if(artistDataset.get(position).getNoOfSongs() > 1 && artistDataset.get(position).getNoOfAlbums() > 1) {
            holder.no_of_songs.setText(String.format(context.getString(R.string.no_of_song_no_of_album),
                    artistDataset.get(position).getNoOfSongs(), artistDataset.get(position).getNoOfAlbums()));
        }


        holder.overflow.setImageResource(R.drawable.overflow);

    }

    @Override
    public void afterItemDelete(int i){
        if (artistDataset != null) {
            artistDataset.remove(i);
            notifyItemRemoved(i);
        }
    }


    @Override
    public int getItemCount () {
        if (artistDataset != null) {
            return artistDataset.size();
        }else {
            return 0;
        }
    }

}
