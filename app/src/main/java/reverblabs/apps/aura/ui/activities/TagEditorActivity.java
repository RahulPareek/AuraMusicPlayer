package reverblabs.apps.aura.ui.activities;

import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;

import static reverblabs.apps.aura.R.id.save;

public class TagEditorActivity extends AppCompatActivity {

    EditText songText;
    EditText artistText;
    EditText albumText;
    EditText albumArtist;
    EditText genreText;
    EditText yearText;
    EditText trackNo;

    Song song;
    MediaMetadataRetriever metadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.tageditor));
        }

        song = getIntent().getParcelableExtra(Constants.PARCELABLE);

        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(song.getPath());

        songText = (EditText) findViewById(R.id.song);
        artistText = (EditText) findViewById(R.id.artist);
        albumText = (EditText) findViewById(R.id.album);
        albumArtist = (EditText) findViewById(R.id.album_artist);
        genreText = (EditText) findViewById(R.id.genre);
        yearText = (EditText) findViewById(R.id.year);
        trackNo = (EditText) findViewById(R.id.track_no);

        songText.setText(song.getTitle());
        artistText.setText(song.getArtist());
        albumText.setText(song.getAlbum());
        albumArtist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
        genreText.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        yearText.setText(song.Year);
        trackNo.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));

    }


    public void editTags(){


            boolean tagsChanged = false;

            try{

                TagOptionSingleton.getInstance().setAndroid(true);
                AudioFile audioFile = AudioFileIO.read(new File(song.getPath()));



                Tag tag = audioFile.getTag();

                if (tag == null){
                    audioFile.setTag(new ID3v23Tag());
                    tag = audioFile.getTag();
                }

                if (!songText.getText().toString().equals(song.getTitle())) {

                    tag.setField(FieldKey.TITLE, songText.getText().toString());
                    tagsChanged = true;
                }
                if (!artistText.getText().toString().equals(song.getArtist())) {
                    tag.setField(FieldKey.ARTIST, artistText.getText().toString());
                    tagsChanged = true;
                }
                if (!albumText.getText().toString().equals(song.getAlbum())) {
                    tag.setField(FieldKey.ALBUM, albumText.getText().toString());
                    tagsChanged = true;
                }
                if (!albumArtist.getText().toString().equals(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST))) {

                    tag.setField(FieldKey.ALBUM_ARTIST, albumArtist.getText().toString());
                    tagsChanged = true;


                }
                if (!genreText.getText().toString().equals(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE))) {
                    tag.setField(FieldKey.GENRE, genreText.getText().toString());
                    tagsChanged = true;
                }
                if (!yearText.getText().toString().equals(song.Year)) {
                    tag.setField(FieldKey.YEAR, yearText.getText().toString());
                    tagsChanged = true;
                }
                if (!trackNo.getText().toString().equals("")) {
                    if (!trackNo.getText().toString().equals(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER))) {
                        tag.setField(FieldKey.TRACK, trackNo.getText().toString());
                        tagsChanged = true;
                    }
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.track_no_error),Toast.LENGTH_SHORT);
                }
                audioFile.commit();

                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{song.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                    }
                });

                if (tagsChanged) {
                    Toast.makeText(getApplicationContext(), getString(R.string.tag_edited_message), Toast.LENGTH_SHORT).show();
                }



            }catch (IOException|TagException|InvalidAudioFrameException|
                    ReadOnlyFileException|CannotReadException|CannotWriteException|NumberFormatException|NullPointerException e){
                e.printStackTrace();
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_equalizer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case save:
                editTags();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
