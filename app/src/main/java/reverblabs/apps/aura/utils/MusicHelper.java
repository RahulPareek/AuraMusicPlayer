package reverblabs.apps.aura.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.PlaylistCallback;
import reverblabs.apps.aura.model.Album;
import reverblabs.apps.aura.model.Artist;
import reverblabs.apps.aura.model.Playlist;
import reverblabs.apps.aura.model.Song;

public class MusicHelper implements PlaylistCallback {

    private Context context;
    private ContentResolver contentResolver;

    public PlaylistCallback playlistCallback;
    public ArrayList<Long> songId = new ArrayList<>();
    public ArrayList<Song> songList = new ArrayList<>();

    public MusicHelper(){}

    public MusicHelper(Context cx, ContentResolver cr){
        context = cx;
        contentResolver = cr;
    }


    public static ArrayList<Album> extractAlbumsFromCursor(Cursor cursor){

        ArrayList<Album> temp = new ArrayList<>();

        if(cursor == null){
            return null;
        }

        if (cursor.getCount() ==0){
            return null;
        }

        try{
            cursor.moveToFirst();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }



        do {
            long aid = cursor.getLong(0);
            String aAlbum = cursor.getString(1);
            String aArist = cursor.getString(2);
            int aNoOfSongs = cursor.getInt(3);
            String ayear = cursor.getString(4);


            Album album = new Album(aid,aAlbum, aArist,aNoOfSongs,ayear);
            temp.add(album);
        }while (cursor.moveToNext());

        return temp;
    }

    public static ArrayList<Artist> extractArtistFromCursor(Cursor cursor){
        if(cursor == null){
            return null;
        }

        if (cursor.getCount() ==0){
            return null;
        }

        try{
            cursor.moveToFirst();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        ArrayList<Artist> temp = new ArrayList<>();

        do {

            long aid = cursor.getLong(0);
            String aArist = cursor.getString(1);
            int anoofAlbum = cursor.getInt(2);
            int aNoOfSongs = cursor.getInt(3);


            Artist artist = new Artist(aid, aArist, anoofAlbum, aNoOfSongs);
            temp.add(artist);
        } while (cursor.moveToNext());

        return temp;
    }

    public static ArrayList<Song> extractSongsFromCursor(Cursor cursor){

        if(cursor == null){
            return null;
        }

        if (cursor.getCount() ==0){
            return null;
        }

        try {
            cursor.moveToFirst();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        ArrayList<Song> dataSet = new ArrayList<>();



            int idcolumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistidcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int datacolumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int artistcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumidcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int titlecolumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int yearcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);

            dataSet.clear();

            do {
                Song song = new Song(cursor.getLong(idcolumn),
                        cursor.getString(datacolumn),
                        cursor.getString(artistcolumn),
                        cursor.getLong(artistidcolumn),
                        cursor.getString(albumcolumn),
                        cursor.getLong(albumidcolumn),
                        cursor.getString(titlecolumn),
                        cursor.getInt(durationcolumn),
                        cursor.getString(yearcolumn));
                dataSet.add(song);
            } while (cursor.moveToNext());


        return dataSet;
    }

    public static ArrayList<Song> getArtistSongs(String name, ContentResolver cr){

        ArrayList<Song> songs = new ArrayList<>();

        String projection[] = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR};

        String selection =  MediaStore.Audio.Artists.ARTIST+ "=?";

        String[] selectionArgs = {name};

        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, MediaStore.Audio.Media.TITLE);

        if(cursor!=null){
            try{
                cursor.moveToFirst();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            if(cursor.getCount() > 0){
                songs.clear();

                songs = extractSongsFromCursor(cursor);
            }
        }

        return songs;
    }

    public static ArrayList<Song> getAlbumSongs(String albumName, ContentResolver contentResolver){

        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String projection[] = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR};

        String selection =  MediaStore.Audio.Media.ALBUM + "=?";

        String[] selectionArgs = {albumName};

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if(cursor!=null){
            try{
                cursor.moveToFirst();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            if(cursor.getCount() > 0){
                songs.clear();

                songs = extractSongsFromCursor(cursor);
            }
        }

        return songs;
    }

    private static String[] SUPPORTED_FORMATS = new String[]{
            "mp3", "m4a", "aac", "ogg", "wav", "flac"
    };

    public static ArrayList<File> getAudioFiles(File dir){
        ArrayList<File> list = new ArrayList<>();
        List<File> files = Arrays.asList(dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                    if (file.isFile()) {
                        String name = file.getName();
                        return !".nomedia".equals(name) && checkFileExt(name);
                    } else if (file.isDirectory()) {
                        return checkDir(file);
                    } else
                        return false;
                }

        }));

        Collections.sort(files, new FileNameComparator());
        Collections.sort(files, new DirFirstComparator());
        list.addAll(files);

        return list;
    }


    private static boolean checkDir(File dir){
        return dir.exists() && dir.canRead() && !".".equals(dir.getName()) && dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return !".".equals(name) && file.canRead() && (file.isDirectory() || (file.isFile() && checkFileExt(name)));
            }
        }).length != 0;
    }

    private static boolean checkFileExt(String name){
        if (TextUtils.isEmpty(name)){
            return false;
        }

        int p = name.lastIndexOf(".") + 1;
        if (p < 1){
            return false;
        }

        String ext = name.substring(p).toLowerCase();
        for (String o : SUPPORTED_FORMATS){
            if (o.equals(ext)){
                return true;
            }
        }

        return false;
    }

    private static class FileNameComparator implements Comparator<File>{
        @Override
        public int compare(File f1, File f2){
            return f1.getName().compareTo(f2.getName());
        }
    }

    private static class DirFirstComparator implements Comparator<File>{
        @Override
        public int compare(File f1, File f2){
            if (f1.isDirectory() == f2.isDirectory()){
                return 0;
            }
            else if (f1.isDirectory() && !f2.isDirectory()){
                return -1;
            }
            else
                return 1;
        }
    }

    public static Song getSongFromPath(String path, Context context){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {path};
        String projection[] = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR};

        Cursor cursor = contentResolver.query(uri, projection, selection + "=?", selectionArgs, MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null) {

            if (cursor.getCount() > 0) {

                try {
                    cursor.moveToFirst();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }


                int idcolumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistidcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
                int datacolumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int artistcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumidcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int titlecolumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int yearcolumn = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);

                Song song = new Song(cursor.getLong(idcolumn),
                        cursor.getString(datacolumn),
                        cursor.getString(artistcolumn),
                        cursor.getLong(artistidcolumn),
                        cursor.getString(albumcolumn),
                        cursor.getLong(albumidcolumn),
                        cursor.getString(titlecolumn),
                        cursor.getInt(durationcolumn),
                        cursor.getString(yearcolumn));

                cursor.close();

                return song;
            }
        }

        return null;
    }

    @Override
    public void onCreateNewPlaylist(String name){
        Playlist playlist = new Playlist();
        long pId = playlist.createNewPlaylist(contentResolver, name);
        boolean result = playlist.addToPlaylist(contentResolver, pId, songId);
        if(result){
            if(songId.size() == 1) {
                Toast.makeText(context, String.format(context.getString(R.string.song_added_to_playlist),songId.size(), name), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, String.format(context.getString(R.string.songs_added_to_playlist),songId.size(), name), Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onAddToExistingPlaylist(long playlistId, String name){
        Playlist playlist = new Playlist();
        boolean result;
        if(name.equals(context.getString(R.string.favourites))){
            for(int i = 0; i< songList.size(); i++) {
                SharedPrefsFile.saveSongToFavouritesPlaylist(context, songList.get(i));
            }
            result = true;
            Log.i(Constants.TAG, name);
        }
        else {
            result = playlist.addToPlaylist(contentResolver, playlistId, songId);
        }
        if (result) {
            if (songId.size() == 1) {
                Toast.makeText(context, String.format(context.getString(R.string.song_added_to_playlist), songId.size(), name), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, String.format(context.getString(R.string.songs_added_to_playlist), songId.size(), name), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getDurationInSeconds(final Context context, long secs){

        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources()
                .getString(hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    public static boolean useAuraEqualizer(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("equalizer", true);
    }

    public static boolean pauseAudioPlayback(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("pause_audio", true);
    }

    public static boolean downloadArtwork(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("download_artwork", true);
    }

    public static boolean downloadArtistImages(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("download_artist_images", true);
    }

    public static boolean downloadArtworkOnlyOverWifi(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("only_over_wifi", false);
    }

    public static boolean ignoreMediaStoreCovers(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("ignore_album_art", false);
    }

    public static boolean isLandscape(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isTablet(Context context){
        final int layout = context.getResources().getConfiguration().screenLayout;
        return (layout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isJellyBean(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isKitkat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
