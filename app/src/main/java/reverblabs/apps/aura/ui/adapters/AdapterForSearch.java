package reverblabs.apps.aura.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.glide.artist.ArtistImage;
import reverblabs.apps.aura.ui.activities.SearchActivity;
import reverblabs.apps.aura.ui.activities.TagEditorActivity;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class AdapterForSearch extends RecyclerView.Adapter<AdapterForSearch.ViewHolder>
        implements DeleteInterFace {

    private Context context;
    private List searchResults = Collections.emptyList();
    private SearchActivity searchActivity;

    //private ImageFetcher imageFetcher;

    public AdapterForSearch(Context cx, SearchActivity activity ){
        context = cx;
        searchActivity = activity;

        //imageFetcher = Utils.getImageFetcher(activity);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView section_header;

        ImageView album_art_artist;
        TextView artist_name_artist;
        TextView no_of_songs_artist;
        ImageView overflow_artist;

        ImageView album_art_album;
        TextView album_name_album;
        TextView album_artist_name;
        ImageView overflow_album;

        TextView textView1;
        TextView textView2;
        ImageView imageView;
        ImageView overflow_song;

        ViewHolder(View v) {
            super(v);

            section_header = (TextView) v.findViewById(R.id.section_header);

            album_art_artist = (ImageView) v.findViewById(R.id.album_art_artist_search);
            artist_name_artist = (TextView) v.findViewById(R.id.firstline_artist_search);
            no_of_songs_artist = (TextView) v.findViewById(R.id.secondline_artist_search);
            overflow_artist = (ImageView) v.findViewById(R.id.overflow_artist_search);

            album_art_album = (ImageView) v.findViewById(R.id.album_art_album_search);
            album_name_album = (TextView) v.findViewById(R.id.firstline_album_search);
            album_artist_name = (TextView) v.findViewById(R.id.secondline_album_search);
            overflow_album = (ImageView) v.findViewById(R.id.overflow_album_search);

            textView1 = (TextView) v.findViewById(R.id.firstline_songs_search);
            textView2 = (TextView) v.findViewById(R.id.secondline_songs_search);
            imageView = (ImageView) v.findViewById(R.id.album_art_songs_search);
            overflow_song = (ImageView) v.findViewById(R.id.overflow_songs_search);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (getItemViewType()) {
                case 0:

                    Album album = (Album) searchResults.get(getAdapterPosition());

                    Bundle bundle = new Bundle();
                    bundle.putLong("albumid", album.getid());
                    bundle.putString("album", album.album);
                    bundle.putString("artist", album.artist);
                    bundle.putString("year", album.year);

                    searchActivity.navigateToAlbum(bundle);


                    break;

                case 1:

                    Artist artist = (Artist) searchResults.get(getAdapterPosition());
                     Bundle artistBundle = new Bundle();
                    artistBundle.putString("artist", artist.artist);
                    artistBundle.putLong("id", artist.id);
                     searchActivity.navigateToArtist(artistBundle);


                    break;

                case 2:
//
//                        Song song = (Song) searchResults.get(getAdapterPosition());
//                        ArrayList<Song> songArrayList = new ArrayList<>();
//                        songArrayList.add(song);
//                        boolean value = SharedPrefsFile.getShuffleMode(context);
//                        HelperClass.auraMusicService.setMusicPlayerList(songArrayList);
//                        HelperClass.auraMusicService.setNowPlaying(0, song);
//                        if (value) {
//                            HelperClass.auraMusicService.handleShuffleMode();
//                        }
//                        HelperClass.auraMusicService.createMediaPlayer();
//                        HelperClass.auraMusicService.startPlayingSong();


                    break;
            }
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        switch (viewType){

            case 0:

                View albumsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums_search_adapter, parent, false);
                return new ViewHolder(albumsView);

            case 1:
                View artistsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_search_adapter, parent, false);
                return new ViewHolder(artistsView);

            case 2:

                View songsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
                return new ViewHolder(songsView);

            case 3:
                View sectionHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_section_header, parent, false);
                return new ViewHolder(sectionHeader);

            default:
                View songsViewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
                return new ViewHolder(songsViewDefault);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder itemHolder) {

    }

        @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        switch (getItemViewType(position)){

            case 0:

                Album album = (Album) searchResults.get(position);

                holder.album_name_album.setText(album.album);
                holder.album_artist_name.setText(album.artist);
                holder.overflow_album.setImageResource(R.drawable.overflow);

                setAlbumPopupMenuListener(holder, position);
                if (holder.album_art_album != null) {
                    GlideApp.with(context)
                            .load(new AlbumImage(album.getArtist(), album.getAlbum(),
                                    (int) album.getid()))
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .apply(new RequestOptions().
                                    override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                            .transition(GenericTransitionOptions.with(R.anim.fade_in))
                            .placeholder(R.drawable.album_art)
                            .error(R.drawable.album_art)
                            .into(holder.album_art_album);

                   // imageFetcher.loadImage(album.getid(), album.getAlbum(),
                     //       album.getArtist(), "Album", false, holder.album_art_album, Constants.SMALL);

                }

                break;

            case 1:

                Artist artist = (Artist) searchResults.get(position);

                setArtistPopupMenuListener(holder, position);

                holder.artist_name_artist.setText(artist.artist);
                if(artist.getNoOfSongs() == 1) {
                    holder.no_of_songs_artist.setText(String.format(context.getString(R.string.one_song),artist.getNoOfSongs()));
                }
                else{
                    holder.no_of_songs_artist.setText(String.format(context.getString(R.string.no_of_songs), artist.getNoOfSongs()));
                }

                holder.overflow_artist.setImageResource(R.drawable.overflow);

                if (holder.album_art_artist != null) {

                    GlideApp.with(context)
                            .load(new ArtistImage(artist.getArtist(),
                                    (int) artist.getid()))
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .apply(new RequestOptions().
                                    override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE))
                            .transition(GenericTransitionOptions.with(R.anim.fade_in))
                            .placeholder(R.drawable.album_art)
                            .error(R.drawable.album_art)
                            .into(holder.album_art_artist);

//                    imageFetcher.loadImage(artist.getid(), null,
//                            artist.getArtist(), "Artist", false, holder.album_art_artist, Constants.SMALL);
                }

                break;

            case 2:

                Song song = (Song) searchResults.get(position);

                holder.textView1.setText(song.Title);
                holder.textView2.setText(song.Artist);
                holder.overflow_song.setImageResource(R.drawable.overflow);

                setSongPopupMenuListener(holder, position);
                if (holder.imageView != null) {

                    GlideApp.with(context)
                            .load(new AlbumImage(song.getArtist(), song.getAlbum(),
                                    (int) song.getAlBumId()))
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .apply(new RequestOptions().
                                    override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                            .transition(GenericTransitionOptions.with(R.anim.fade_in))
                            .placeholder(R.drawable.album_art)
                            .error(R.drawable.album_art)
                            .into(holder.imageView);

//                    imageFetcher.loadImage(song.getAlBumId(), song.getAlbum(),
//                            song.getArtist(), "Song", false, holder.imageView, Constants.SMALL);
                }

                break;

            case 3:
                holder.section_header.setText((String) searchResults.get(position));

            case 4:
                break;

        }

    }



    @Override
    public int getItemViewType(int position){
        if(searchResults.get(position) instanceof Album)
            return 0;

        if(searchResults.get(position) instanceof Artist)
            return 1;

        if (searchResults.get(position) instanceof Song)
            return 2;

        if(searchResults.get(position) instanceof String)
            return 3;

        return 4;
    }

    public void updateSearchResults(List list){
        searchResults = list;
    }

    private void setAlbumPopupMenuListener(ViewHolder holder, final int position){
        holder.overflow_album.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                final PopupMenu menu = new PopupMenu(context, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Album album = (Album) searchResults.get(position);

                        ArrayList<Song> songs = MusicHelper.getAlbumSongs(album.album, context.getContentResolver());

                       int posOfItemDeleted = searchResults.indexOf(album);

                        switch (menuItem.getItemId()) {
                            case R.id.play:
//
//                                HelperClass.auraMusicService.setMusicPlayerList(songs);
//                                HelperClass.auraMusicService.setNowPlaying(0, songs.get(0));
//                                HelperClass.auraMusicService.createMediaPlayer();
//                                HelperClass.auraMusicService.startPlayingSong();
//
//                                return true;
//
//                            case R.id.add_to_queue:
//
//                                HelperClass.auraMusicService.addToQueue(songs);
//                                return true;

                            case R.id.add_to_playlist:
                               searchActivity.createPlaylistDialog(songs);
                                return true;

                            case R.id.delete:
                              ArrayList<Integer> pos = new ArrayList<>();
                                pos.add(posOfItemDeleted);

                                searchActivity.createDeleteDialog(album.getAlbum(), songs, pos);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                menu.inflate(R.menu.menu_album_artist);
                menu.show();
            }
        });
    }

    private void setArtistPopupMenuListener(ViewHolder holder, final int position){

        holder.overflow_artist.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                PopupMenu menu = new PopupMenu(context, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Artist artist = (Artist) searchResults.get(position);

                        ArrayList<Song> songs = MusicHelper.getArtistSongs(artist.artist, context.getContentResolver());

                       int posOfItemDeleted = searchResults.indexOf(artist);

                        switch (menuItem.getItemId()) {
                            case R.id.play:
//
//                                HelperClass.auraMusicService.setMusicPlayerList(songs);
//                                HelperClass.auraMusicService.setNowPlaying(0, songs.get(0));
//                                HelperClass.auraMusicService.createMediaPlayer();
//                                HelperClass.auraMusicService.startPlayingSong();
//
//                                return true;
//
//                            case R.id.add_to_queue:
//
//                                HelperClass.auraMusicService.addToQueue(songs);
//                                return true;

                            case R.id.add_to_playlist:
                             searchActivity.createPlaylistDialog(songs);
                                return true;

                            case R.id.delete:
                                ArrayList<Integer> pos = new ArrayList<>();
                                pos.add(posOfItemDeleted);

                                searchActivity.createDeleteDialog(artist.artist, songs, pos);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                menu.inflate(R.menu.menu_album_artist);
                menu.show();
            }
        });

    }

    private void setSongPopupMenuListener(ViewHolder holder, final int position){

        holder.overflow_song.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                PopupMenu menu = new PopupMenu(context, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Song song = (Song) searchResults.get(position);
                        int posOfItemDeleted = searchResults.indexOf(song);

                        switch (menuItem.getItemId()) {
//                            case R.id.play_next:
//                                HelperClass.auraMusicService.handlePlayNext(song);
//                                return true;
//
//                            case R.id.add_to_queue:
//                                HelperClass.auraMusicService.handleAddToQueue(song);
//                                return true;

                            case R.id.add_to_playlist:
                                ArrayList<Song> addToPlaylist = new ArrayList<>();
                                addToPlaylist.add(song);
                                searchActivity.createPlaylistDialog(addToPlaylist);

                                return true;

                            case R.id.edit_tags:
                                Intent intent = new Intent(context, TagEditorActivity.class);
                                intent.putExtra(Constants.PARCELABLE, song);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                                return true;

                            case R.id.go_to_artist:
                                Bundle artistBundle = new Bundle();
                                artistBundle.putString("artist", song.getArtist());
                                artistBundle.putLong("id", song.ArtistId);
                                searchActivity.navigateToArtist(artistBundle);

                                return true;

                            case R.id.go_to_album:

                                Bundle bundle = new Bundle();
                                bundle.putLong("albumid",song.getAlBumId());
                                bundle.putString("album",song.getAlbum());
                                bundle.putString("artist",song.getArtist());
                                bundle.putString("year",song.Year);

                                searchActivity.navigateToAlbum(bundle);

                                return true;

                            case R.id.details:
                                searchActivity.createDetailsDialog(song.getPath());

                                return true;

                            case R.id.delete:
                                   ArrayList<Integer> delete = new ArrayList<>();
                                  delete.add(posOfItemDeleted);

                                ArrayList<Song> songArrayList = new ArrayList<>();
                                songArrayList.add(song);

                                 searchActivity.createDeleteDialog(song.getTitle(), songArrayList, delete);

                                return true;

                            default:
                                return false;
                        }
                    }
                });

                menu.inflate(R.menu.menu_song);
                menu.show();
            }
        });

    }

    @Override
    public int getItemCount(){
        if (searchResults != null) {
            return searchResults.size();
        }else {
            return 0;
        }
    }

    @Override
    public void afterItemDelete(int i){
        if (searchResults != null) {
            searchResults.remove(i);
            notifyItemRemoved(i);
        }
    }
}
