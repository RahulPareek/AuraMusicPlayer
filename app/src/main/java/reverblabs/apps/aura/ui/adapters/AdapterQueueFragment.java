package reverblabs.apps.aura.ui.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.ui.fragments.QueueFragment;
import reverblabs.apps.aura.interfaces.ItemTouchHelperAdapter;
import reverblabs.apps.aura.interfaces.ItemTouchHelperViewHolder;
import reverblabs.apps.aura.interfaces.OnStartDragListener;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;

public class AdapterQueueFragment extends RecyclerView.Adapter<AdapterQueueFragment.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Song> queueDataset = new ArrayList<>() ;
    private Context context;

    private QueueFragment queueFragment;

    private final OnStartDragListener onStartDragListener;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, ItemTouchHelperViewHolder {

         TextView textView1;
         TextView textView2;
         ImageView imageView;
        ImageView reorder;
        ImageView overflow;


        ViewHolder(View v){
            super(v);
            textView1 = v.findViewById(R.id.firstline_queue);
            textView2 = v.findViewById(R.id.secondline_queue);
            imageView = v.findViewById(R.id.image_queue);
            reorder = v.findViewById(R.id.reorder);
            overflow = v.findViewById(R.id.overflow_queue);
            overflow.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(view == overflow){
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_queue, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }
            else{
                queueFragment.playSong(getAdapterPosition());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item){

            Song song = queueDataset.get(getAdapterPosition());
            int posOfItem = getAdapterPosition();

            switch (item.getItemId()){
                case R.id.play_next_queue:
                    QueueManager.playNext(queueDataset.get(getAdapterPosition()));
                    return true;

                case R.id.go_to_artist_queue:
                    Bundle artistbundle = new Bundle();
                    artistbundle.putString("artist",song.getArtist());
                    artistbundle.putLong("id",song.ArtistId);

                    queueFragment.createArtistFragment(artistbundle);

                    return true;

                case R.id.go_to_album_queue:
                    Bundle bundle = new Bundle();
                    bundle.putLong("albumid",song.getAlBumId());
                    bundle.putString("album",song.getAlbum());
                    bundle.putString("artist",song.getArtist());
                    bundle.putString("year",song.Year);

                    queueFragment.createAlbumFragment(bundle);

                    return true;

                case R.id.remove_queue:
                    songRemoved(posOfItem);
                    return true;

                default:
                    return false;
            }
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

    }

    public AdapterQueueFragment(ContentResolver contentResolver, Activity cx, QueueFragment fragment, OnStartDragListener mOnStartDragListener){
        queueDataset = QueueManager.getPlayingQueue();

        context = cx;
        queueFragment = fragment;

        onStartDragListener = mOnStartDragListener;
    }


    public int queueListSize(){
        return queueDataset.size();
    }


    @Override
    public AdapterQueueFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_queue_fragment, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Song song = queueDataset.get(position);

        if (song != null) {

            holder.textView1.setText(song.getTitle());
            holder.textView2.setText(song.getArtist());

            holder.overflow.setImageResource(R.drawable.overflow);

            GlideApp.with(context)
                    .load(new AlbumImage(queueDataset.get(position).getArtist(),
                            queueDataset.get(position).getAlbum(),
                            (int)  queueDataset.get(position).getAlBumId()))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(new RequestOptions().
                            override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                    .transition(GenericTransitionOptions.with(R.anim.fade_in))
                    .placeholder(R.drawable.album_art)
                    .error(R.drawable.album_art)
                    .into(holder.imageView);
        }

        holder.reorder.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    private void songRemoved(int position){
        if (queueDataset != null) {
            queueDataset.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, queueDataset.size());
            QueueManager.removeSongAt(position);
            queueFragment.setQueueSizeText();
        }
    }

    @Override
    public void onItemDismiss(int position){
        queueDataset.remove(position);
        notifyItemRemoved(position);
        QueueManager.removeSongAt(position);
        queueFragment.setQueueSizeText();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition){
        Collections.swap(queueDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        QueueManager.swapPositions(fromPosition, toPosition);
        queueFragment.setQueueSizeText();
        return true;
    }


    @Override
    public int getItemCount () {
        if (queueDataset != null) {
            return queueDataset.size();
        }else {
            return 0;
        }
    }

}