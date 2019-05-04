package reverblabs.apps.aura.ui.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.ui.fragments.album.AlbumsFragment;
import reverblabs.apps.aura.ui.fragments.artist.ArtistFragment;
import reverblabs.apps.aura.ui.fragments.genre.GenresFragment;
import reverblabs.apps.aura.ui.fragments.playlist.PlaylistsFragment;
import reverblabs.apps.aura.ui.fragments.song.SongsFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    Context context;

    public TabsPagerAdapter(FragmentManager fragmentManager, Context cx){
        super(fragmentManager);
        context = cx;
    }

    @Override
    public Fragment getItem(int i){

        switch (i){
            case 0:
                return new AlbumsFragment();

            case 1:
                return new ArtistFragment();

            case 2:
                return new SongsFragment();

            case 3:
                return new GenresFragment();

            case 4:
                return new PlaylistsFragment();

        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
            return context.getString(R.string.albums_tab);
            case 1:
                return context.getString(R.string.artists_tab);
            case 2:
                return context.getString(R.string.songs_tab);
            case 3:
                return context.getString(R.string.genres_tab);
            case 4:
                return context.getString(R.string.playlists_tab);
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return 5;
    }
}
