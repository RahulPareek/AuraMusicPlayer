package reverblabs.apps.aura.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import reverblabs.apps.aura.R;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST;

public class DetailsDialog extends DialogFragment {

    String path;

    public void init(String mPath){
        path = mPath;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);


        View rootView = inflater.inflate(R.layout.details_layout, null);


        TextView value_name = (TextView) rootView.findViewById(R.id.name);
        value_name.setText(getString(R.string.name_details, metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)));


        TextView value_artist = (TextView) rootView.findViewById(R.id.artist);
        value_artist.setText(getString(R.string.artist_details,metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)));


        TextView valuealbum = (TextView) rootView.findViewById(R.id.album);
        valuealbum.setText(getString(R.string.album_details,metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)));

        TextView value_album_artist = (TextView) rootView.findViewById(R.id.album_artist);
        value_album_artist.setText(getString(R.string.album_artist_details,metadataRetriever.extractMetadata(METADATA_KEY_ALBUMARTIST)));


        TextView value_track_no = (TextView) rootView.findViewById(R.id.track_no);
        value_track_no.setText(getString(R.string.cd_track_no_details,metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)));


        TextView valuegenre= (TextView) rootView.findViewById(R.id.genre);
        valuegenre.setText( getString(R.string.genre_details,metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)));


        TextView valuePath = (TextView) rootView.findViewById(R.id.path);
        valuePath.setText(getString(R.string.path_details,path));


        TextView value_date = (TextView) rootView.findViewById(R.id.date);
        value_date.setText(getString(R.string.date_details,metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)));


        int rate =  Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))/1000;


        TextView value_bitrate = (TextView) rootView.findViewById(R.id.bitrate);
        value_bitrate.setText(String.format(getString(R.string.bitrate_details), rate));



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.details));
        builder.setView(rootView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }


}
