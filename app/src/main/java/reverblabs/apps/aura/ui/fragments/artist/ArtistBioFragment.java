package reverblabs.apps.aura.ui.fragments.artist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.utils.GetArtistBio;

public class ArtistBioFragment extends Fragment {


    public ArtistBioFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist_bio, container, false);

        TextView bio;
        TextView header;

        String artist;

        artist = getArguments().getString("artist_name");

        bio = rootView.findViewById(R.id.artist_bio);
        header = rootView.findViewById(R.id.artist_bio_header);
        ProgressBar progressBar = rootView.findViewById(R.id.bio_progress_bar);

        header.setText(String.format(getString(R.string.artist_biography), artist));

        GetArtistBio getArtistBio = new GetArtistBio(artist, bio, progressBar);
        getArtistBio.execute();

        return rootView;
    }

}
