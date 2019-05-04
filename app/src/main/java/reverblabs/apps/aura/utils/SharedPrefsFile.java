package reverblabs.apps.aura.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.PresetReverb;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import reverblabs.apps.aura.model.Song;


public class SharedPrefsFile {

    public static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    }


    public static boolean getShuffleMode(Context context){
        return getPrefs(context).getBoolean(Constants.SHUFFLE_MODE, false);
    }


    public static void setShuffleMode(Context context, boolean temp){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.SHUFFLE_MODE, temp);
        editor.apply();
    }

    public static int getRepeatMode(Context context){
        return getPrefs(context).getInt(Constants.REPEAT_MODE, 0);
    }

    public static void setRepeatMode(Context context, int temp){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.REPEAT_MODE, temp);
        editor.apply();
    }


   public static ArrayList<Song> getQueue(Context context){
       Gson gson = new Gson();
       String playlist = getPrefs(context).getString(Constants.Queue, "favourites");
       if(playlist.equals("favourites")){
           return null;
       }
       Type type = new TypeToken<List<Song>>() {}.getType();
       return gson.fromJson(playlist, type);
    }

    public static ArrayList<Song> getRecentlyPlayedPlaylist(Context context){
        Gson gson = new Gson();
        String playlist = getPrefs(context).getString(Constants.PLAYLIST_RECENTLY_PLAYED, "recently played");
        if(playlist.equals("recently played")){
            return null;
        }
        else {
                Type type = new TypeToken<List<Song>>() {
                }.getType();
                return gson.fromJson(playlist, type);
        }
    }

    public static ArrayList<Song> getFavouritesPlaylist(Context context){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String playlist = getPrefs(context).getString(Constants.PLAYLIST_FAVOURITE, "favourites");
        if(playlist.equals("favourites")){
            return null;
        }
        else {
            Type type = new TypeToken<List<Song>>() {
            }.getType();
            return gson.fromJson(playlist, type);
        }
    }


    public static int getNowPlayingPosition(Context context){
       return getPrefs(context).getInt(Constants.NOW_PLAYING_POSITION, -1);
    }

    public static short getEqualizerPresetMode(Context context){
        int value = getPrefs(context).getInt(Constants.EQUALIZER_PRESET, 0);
        return (short) value;
    }

    public static ArrayList<Short> getCustomPresetLevels(Context context){
        Gson gson = new Gson();
        String values = getPrefs(context).getString(Constants.EQUALIZER_PRESET_LEVELS, "values");

        if(values.equals("values")){
            return null;
        }

        else {
            Type type = new TypeToken<List<Short>>(){}.getType();

            return gson.fromJson(values, type);
        }
    }

    public static short getBassBoostValue(Context context){
        int temp = getPrefs(context).getInt(Constants.BASS_BOOST, 0);
        return (short) temp;
    }

    public static short getVirtualizerValue(Context context){
        int temp = getPrefs(context).getInt(Constants.VIRTUALIZER, 0);
        return (short) temp;
    }

    public static String getUserPreferredFolder(Context context){
        String temp = getPrefs(context).getString(Constants.LAST_LOADED_FOLDER,
                Environment.getExternalStorageDirectory().getAbsolutePath());

        return temp;
    }

    public static void setUserPreferredFolder(String string, Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();

        editor.putString(Constants.LAST_LOADED_FOLDER, string);
        editor.apply();
    }

   public static void saveQueue(Context context, ArrayList<Song> temp){
       SharedPreferences.Editor editor = getPrefs(context).edit();

        Gson gson = new Gson();
        String playlist = gson.toJson(temp);
        editor.putString(Constants.Queue, playlist);
        editor.apply();
    }

    public static void saveRecentlyPlayedPlaylist(Context context, ArrayList<Song> temp){
        SharedPreferences.Editor editor = getPrefs(context).edit();

        Gson gson = new Gson();
        String playlist = gson.toJson(temp);
        editor.putString(Constants.PLAYLIST_RECENTLY_PLAYED, playlist);
        editor.apply();
    }

    public static void saveSongToRecentlyPlayed(Context context, Song song){
        ArrayList<Song> recentlyPlayed = new ArrayList<>();
        ArrayList<Song> temp = getRecentlyPlayedPlaylist(context);
        if(temp != null) {
            if (temp.size() >= 20) {
                recentlyPlayed.add(song);
                for (int i=0; i<temp.size() - 1; i++){
                    recentlyPlayed.add( i + 1, temp.get(i));
                }
            } else {
                recentlyPlayed.add(song);
                for (int i =0; i< temp.size() ; i++){
                    recentlyPlayed.add( i + 1, temp.get(i));
                }
            }
        }
        else {
            recentlyPlayed.add(song);
        }

        Gson gson = new Gson();
        String playlist = gson.toJson(recentlyPlayed);

        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(Constants.PLAYLIST_RECENTLY_PLAYED, playlist);
        editor.apply();
    }

    public static void saveFavouritesPlaylist(Context context, ArrayList<Song> temp){
        SharedPreferences.Editor editor = getPrefs(context).edit();

        Gson gson = new Gson();
        String playlist = gson.toJson(temp);
        editor.putString(Constants.PLAYLIST_FAVOURITE, playlist);
        editor.apply();
    }

    public static void saveSongToFavouritesPlaylist(Context context, Song song){
        Log.i(Constants.TAG, song.getTitle());
        ArrayList<Song> favourites = new ArrayList<>();
        ArrayList<Song> temp = getFavouritesPlaylist(context);
        if(temp!=null) {
            favourites =temp;
            if (favourites.size() >= 40) {
                favourites.add(39, song);
            } else {
                favourites.add(song);
            }
        }
        else {
            favourites.add(song);
        }
        Gson gson = new Gson();
        String playlist = gson.toJson(favourites);

        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(Constants.PLAYLIST_FAVOURITE, playlist);
        editor.apply();
    }

    public static void setNowPlayingPosition(Context context, int pos){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.NOW_PLAYING_POSITION, pos);
        editor.apply();
    }

    public static void setEqualizerPreset(Context context, int value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.EQUALIZER_PRESET, value);
        editor.apply();
    }

    public static void setEqualizerPresetValues(Context context, int value, int id, int size){
        SharedPreferences.Editor editor = getPrefs(context).edit();

        ArrayList<Short> values = new ArrayList<>();

        if(getCustomPresetLevels(context) != null) {
            values = getCustomPresetLevels(context);
                if (values.size() < size) {

                    values.add(id, (short) value);
                } else {
                    values.set(id, (short) value);
                }

        }
        else {
            values.add(id, (short)value);
        }

        Gson gson = new Gson();
        String temp = gson.toJson(values);
        editor.putString(Constants.EQUALIZER_PRESET_LEVELS, temp);
        editor.apply();
    }

    public static void setBassBoost(Context context, int value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.BASS_BOOST, value);
        editor.apply();
    }

    public static void setVirtualizerValue(Context context, int value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.VIRTUALIZER, value);
        editor.apply();
    }

    public static short getPresetReverb(Context context){
        int temp = getPrefs(context).getInt(Constants.PRESET_REVERB, PresetReverb.PRESET_NONE);
        return (short) temp;
    }

    public static void setPresetReverb(Context context , int value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.PRESET_REVERB, value);
        editor.apply();
    }

    public static void setMediaPlayerPosition(Context context, int temp){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(Constants.LAST_PLAYED_MEDIA_PLAYER_POSITION, temp);
        editor.apply();
    }

    public static int getMediaPlayerPosition(Context context){
        return getPrefs(context).getInt(Constants.LAST_PLAYED_MEDIA_PLAYER_POSITION, 0);
    }
}
