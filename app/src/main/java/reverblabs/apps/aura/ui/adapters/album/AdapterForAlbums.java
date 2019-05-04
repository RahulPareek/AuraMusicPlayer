package reverblabs.apps.aura.ui.adapters.album;

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
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.album.AlbumsFragment;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class AdapterForAlbums extends RecyclerView.Adapter<AdapterForAlbums.ViewHolder> implements DeleteInterFace,
         FastScrollRecyclerView.SectionedAdapter{

    private ArrayList<Album> albumsDataset = new ArrayList<>() ;
    private ContentResolver cr;
    private Context context;

    private AdapterToFragmentCallbacks fragmentCallbacks;

    private Cursor cursor;

    //private ImageFetcher imageFetcher;


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

        ImageView album_art;
        TextView album_name;
        TextView artist_name_and_songs;
         ImageView overflow;

         ViewHolder(View v){
            super(v);
            this.album_art = v.findViewById(R.id.card_image);
            this.album_name = v.findViewById(R.id.card_album_name);
            this.artist_name_and_songs = v.findViewById(R.id.card_album_artist_no_of_songs);
             this.overflow = v.findViewById(R.id.overflow_album);
             overflow.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(view == overflow){
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_album_artist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }
            else {

               Album album = albumsDataset.get(getAdapterPosition());

                Bundle bundle = new Bundle();
                bundle.putLong("albumid", album.getid());
                bundle.putString("album", album.album);
                bundle.putString("artist", album.artist);
                bundle.putString("year", album.year);
                bundle.putInt("count", album.getNoOfSongs());

                fragmentCallbacks.createAlbumDetailFragment(bundle);
            }
        }

         @Override
         public boolean onMenuItemClick(MenuItem item) {

             Album album = albumsDataset.get(getAdapterPosition());

             ArrayList<Song> songs = MusicHelper.getAlbumSongs(album.album, cr);

             switch (item.getItemId()) {
                 case R.id.play:
//
//                     HelperClass.auraMusicService.setMusicPlayerList(songs);
//                     HelperClass.auraMusicService.setNowPlaying(0, songs.get(0));
//                     HelperClass.auraMusicService.createMediaPlayer();
//                     HelperClass.auraMusicService.startPlayingSong();
                     fragmentCallbacks.play(songs);
                     return true;
//
                 case R.id.add_to_queue:
//
//                     HelperClass.auraMusicService.addToQueue(songs);
                    fragmentCallbacks.addToQueue(songs);
                    return true;

                 case R.id.add_to_playlist:
                     fragmentCallbacks.createPlaylistDialog(songs);
                     return true;

                 case R.id.delete:

                     ArrayList<Integer> pos = new ArrayList<>();
                     pos.add(albumsDataset.indexOf(album));

                     fragmentCallbacks.createDeleteDialog(album, songs, pos, getDeleteInterface());
                     return true;

                 default:
                     return false;
             }

         }
    }



    public AdapterForAlbums(ContentResolver contentResolver, Activity cx, Cursor dataCursor, AlbumsFragment fragment){
        albumsDataset.clear();
        cr = contentResolver;
        context = cx;
        cursor = dataCursor;
        fragmentCallbacks = fragment;

        //imageFetcher = Utils.getImageFetcher(cx);
       // imageFetcher.setmLoadingBitmap(R.drawable.album_art);

    }


    public DeleteInterFace getDeleteInterface(){
        return this;
    }

    @Override
    public AdapterForAlbums.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_albums, parent,false);
        return new ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor){
        if(cursor == mCursor){
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        albumsDataset.clear();

        albumsDataset = MusicHelper.extractAlbumsFromCursor(cursor);

        if(albumsDataset != null){
            notifyDataSetChanged();
        }


        return oldCursor;
    }


    @NonNull
    public String getSectionName(int pos){
        return albumsDataset.get(pos).getAlbum().substring(0,1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GlideApp.with(context)
                .load(new AlbumImage(albumsDataset.get(position).getArtist(),
                        albumsDataset.get(position).getAlbum(),
                        (int)  albumsDataset.get(position).getid()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().
                        override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .into(holder.album_art);

        //imageFetcher.loadImage(albumsDataset.get(position).id, albumsDataset.get(position).getAlbum(),
        //        albumsDataset.get(position).getArtist(), "Album", false, holder.album_art, Constants.MEDIUM);

        if (albumsDataset.get(position).artist.equals("<unknown>")){
            holder.album_name.setText(" ");
        }else {
            holder.album_name.setText(albumsDataset.get(position).album);
        }

        holder.album_name.setText(albumsDataset.get(position).album);

        if(albumsDataset.get(position).getNoOfSongs() == 1) {
            holder.artist_name_and_songs.setText(String.format(context.getString(R.string.album_artist_and_one_song),
                    albumsDataset.get(position).getArtist(),albumsDataset.get(position).getNoOfSongs()));
        }
        else{
            holder.artist_name_and_songs.setText(String.format(context.getString(R.string.album_artist_and_no_of_songs),
                    albumsDataset.get(position).getArtist(),albumsDataset.get(position).getNoOfSongs()));
        }

        holder.overflow.setImageResource(R.drawable.overflow);

    }


    @Override
    public void afterItemDelete(int i){
        if (albumsDataset != null) {
            albumsDataset.remove(i);
            notifyItemRemoved(i);
        }
    }

    @Override
    public int getItemCount () {
        if (albumsDataset != null) {
            return albumsDataset.size();
        }else {
            return 0;
        }
    }

}
