package reverblabs.apps.aura.model;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class Playlist {

    public long playlistId;
    public String name;
    public int noOfSongs;

    public Playlist(){}

    public Playlist(long mPlaylist, String mName, int count){
        playlistId = mPlaylist;
        name = mName;
        noOfSongs = count;
    }

    public long getId(){
        return playlistId;
    }

    public String getName(){
        return name;
    }

    public int getNoOfSongs(){return noOfSongs;}

    public long createNewPlaylist(ContentResolver contentResolver, String name){

        long id = getPlaylist(contentResolver, name);
        if(id == -1) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Playlists.NAME, name);

            Uri uri = contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues);

            if(uri != null){
                id = Long.parseLong(uri.getLastPathSegment());
            }
        }
        else {
        }
        return id;

    }

    public ArrayList<Song> getAllSongsOfPlaylist(ContentResolver contentResolver, long id) {

        ArrayList<Song> songs = new ArrayList<>();

        String projection[] = {MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.ARTIST_ID,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.YEAR};

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        try {
            if (cursor != null)
                cursor.moveToFirst();
        } catch (NullPointerException e) {
        }

        if (cursor != null && cursor.getCount() > 0) {
            int idcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            int datacolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA);
            int artistcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
            int artistidcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID);
            int albumcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
            int albumidcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID);
            int titlecolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
            int durationcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION);
            int yearcolumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.YEAR);

            songs.clear();

            do {

                Song song = new Song(cursor.getInt(idcolumn),
                        cursor.getString(datacolumn),
                        cursor.getString(artistcolumn),
                        cursor.getLong(artistidcolumn),
                        cursor.getString(albumcolumn),
                        cursor.getLong(albumidcolumn),
                        cursor.getString(titlecolumn),
                        cursor.getInt(durationcolumn),
                        cursor.getString(yearcolumn));
                songs.add(song);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    public long getPlaylist(ContentResolver contentResolver, String name){
        long id = -1;

        Cursor cursor = contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name},null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                id = cursor.getLong(0);
            }
            cursor.close();
        }
        return id;
    }

    public static int getNoOfSongsInPlaylist(ContentResolver contentResolver, long id){
        int count = 0;
        Cursor cursor = contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", id),
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Playlists.Members.IS_MUSIC+  "=1", null, null);

        if(cursor != null) {

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                count = cursor.getCount();
                cursor.close();
            }
        }

        return count;
    }

    public static int getNoOfSongsInRecentlyAdded(ContentResolver contentResolver){

        int count =0;

        String projection[] = {MediaStore.Audio.Media._ID,};

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(MediaStore.Audio.Media.IS_MUSIC + "=1");
        stringBuilder.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + " > " + ((System.currentTimeMillis() / 1000) - 60 * 60 * 24 * 7));

        Cursor cursor = contentResolver.query(uri, projection, stringBuilder.toString(),null ,null);

        if(cursor != null){
            if(cursor.getCount() > 0){
                count = cursor.getCount();
            }
            cursor.close();
        }

        return count;
    }

    public static int getSongsInRecentlyPlayed(Context context){
        int recentlyPlayedCount = 0;

        ArrayList<Song> recentlyPlayedPlaylist = SharedPrefsFile.getRecentlyPlayedPlaylist(context);
        if(recentlyPlayedPlaylist != null){
            recentlyPlayedCount = recentlyPlayedPlaylist.size();
        }

        return recentlyPlayedCount;
    }

    public static int getSongsInFavourites(Context context){
        int count = 0;

        ArrayList<Song> temp = SharedPrefsFile.getFavouritesPlaylist(context);
        if(temp != null){
            count = temp.size();
        }

        return count;
    }

    public ArrayList<Playlist> getAllPlaylists(ContentResolver contentResolver){

        ArrayList<Playlist> playlistsList = new ArrayList<>();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID,MediaStore.Audio.Playlists.NAME},
                null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                do {
                    Playlist playlist = new Playlist(cursor.getLong(0), cursor.getString(1), getNoOfSongsInPlaylist(contentResolver, cursor.getLong(0)));

                    playlistsList.add(playlist);
                }while (cursor.moveToNext());

            }
        }
        try {
            cursor.close();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return playlistsList;
    }

    public boolean addToPlaylist(ContentResolver contentResolver, long pId, ArrayList<Long> songId){

        boolean result = false;

        if(pId!= -3) {

            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pId);
            String[] projection = new String[]{MediaStore.Audio.Playlists.Members.PLAY_ORDER};

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            int count = 0;
            if (cursor!=null) {
                count = cursor.getCount() + 1;
                Log.i(Constants.TAG, Integer.toString(count));
                cursor.close();
            }


            if (count > 0) {
                ContentValues[] contentValues = new ContentValues[songId.size()];

                for (int i = 0; i < songId.size(); i++) {
                    ContentValues values = new ContentValues(2);
                    values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, count + i);
                    values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId.get(i));
                    contentValues[i] = values;
                }
                contentResolver.bulkInsert(uri, contentValues);

                result = true;
            }
        }
        return result;
    }

    public void removeFromPlaylist(ContentResolver contentResolver, long pId,long songId){

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pId);
        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=" +songId ;
        contentResolver.delete(uri, where,null);
        }

    public void deletePlaylist(ContentResolver contentResolver, long id){
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        contentResolver.delete(uri, null, null);
    }

}
