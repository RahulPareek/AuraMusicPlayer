package reverblabs.apps.aura.ui.adapters.album;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.fragments.artist.ArtistDetailFragment;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class AdapterForAllAlbumsOfArtist extends
        RecyclerView.Adapter<AdapterForAllAlbumsOfArtist.ViewHolder> {

    private ArrayList<Album> dataset = new ArrayList<>();
    private Context context;

    private AdapterToFragmentCallbacks fragmentCallbacks;

    Cursor cursor;

    private Album album = new Album();

    //private ImageFetcher imageFetcher;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView album_art;
        TextView album_name;
        TextView noOfSongs;

        ViewHolder(View v){
            super(v);
            album_art = v.findViewById(R.id.card_image);
            album_name = v.findViewById(R.id.card_album_name);
            noOfSongs = v.findViewById(R.id.card_album_no_of_songs);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if (dataset != null) {

                album = dataset.get(getAdapterPosition());

                Bundle bundle = new Bundle();
                bundle.putLong("id", album.id);
                bundle.putString("album", album.album);
                bundle.putString("artist", album.artist);
                bundle.putInt("count", album.noOfSongs);
                bundle.putString("year", album.year);

                fragmentCallbacks.createAlbumDetailFragment(bundle);
            }

        }


    }

    public AdapterForAllAlbumsOfArtist(Activity cx, Cursor dataCursor, ArtistDetailFragment fragment){
        context = cx;
        cursor = dataCursor;
        fragmentCallbacks = fragment;

        //imageFetcher = Utils.getImageFetcher(cx);
    }


    @Override
    public AdapterForAllAlbumsOfArtist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_all_albums_of_artist, parent,false);
        return new ViewHolder(v);
    }

    public Cursor swapCursor(Cursor mCursor) {
        if (cursor == mCursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        this.cursor = mCursor;

        if (dataset != null) {
            dataset.clear();
        }

        dataset = MusicHelper.extractAlbumsFromCursor(cursor);

        if (dataset != null){
            notifyDataSetChanged();
        }

        return oldCursor;
    }


        @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (dataset != null) {

            GlideApp.with(context)
                    .load(new AlbumImage(dataset.get(position).getArtist(),
                            dataset.get(position).getAlbum(),
                            (int)  dataset.get(position).getid()))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(new RequestOptions().
                            override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                    .transition(GenericTransitionOptions.with(R.anim.fade_in))
                    .placeholder(R.drawable.album_art)
                    .error(R.drawable.album_art)
                    .into(holder.album_art);

//           imageFetcher.loadImage(dataset.get(position).id, dataset.get(position).getAlbum(),
//                       dataset.get(position).getArtist(), "Album", false, holder.album_art, Constants.MEDIUM);

            holder.album_name.setText(dataset.get(position).album);

            if (dataset.get(position).getNoOfSongs() == 1) {
                holder.noOfSongs.setText(String.format(context.getString(R.string.one_song), dataset.get(position).getNoOfSongs()));
            }
            else {
                holder.noOfSongs.setText(String.format(context.getString(R.string.no_of_songs), dataset.get(position).getNoOfSongs()));
                }
            }
    }


    @Override
    public int getItemCount () {
        if (dataset != null) {
            return dataset.size();
        }else {
            return 0;
        }
    }

}
