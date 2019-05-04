package reverblabs.apps.aura.playback;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.SharedPrefsFile;

public class QueueManager {

    private static ArrayList<Song> playingQueue = new ArrayList<>();
    private static Integer currentQueueIndex;

    public static void setPlayingQueue(ArrayList<Song> songsList) {
        QueueManager.playingQueue.clear();

        try {
            playingQueue = (ArrayList<Song>) songsList.clone();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public static void checkShuffleAndSetQueue(Context context, ArrayList<Song> songsList,
                                               int position){
        if(SharedPrefsFile.getShuffleMode(context)){

            playingQueue.clear();

            playingQueue = (ArrayList<Song>) songsList.clone();

            Song song = playingQueue.get(position);
            playingQueue.remove(position);

            Collections.shuffle(playingQueue);
            playingQueue.add(0, song);

            setCurrentQueueIndex(0);
        }
        else{
            setPlayingQueue(songsList);
            setCurrentQueueIndex(position);
        }
    }


    public static void handleShuffleFromFloatingButton(ArrayList<Song> songsList){
        QueueManager.playingQueue.clear();

        try {
            playingQueue = (ArrayList<Song>) songsList.clone();

            Collections.shuffle(playingQueue);
            setCurrentQueueIndex(0);
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Song> getPlayingQueue() {
        return playingQueue;
    }

    public static void setCurrentQueueIndex(int position){
        currentQueueIndex = position;
    }

    public static void playNext(Song song){
        playingQueue.add(currentQueueIndex + 1, song);
    }

    public static void addToQueue(Song song){
        playingQueue.add(song);
    }

    public static void addToQueue(ArrayList<Song> songs){
        playingQueue.addAll(songs);
    }

    public static void removeSongAt(int position){
        playingQueue.remove(position);
    }

    public static void swapPositions(int from, int to){
        Collections.swap(playingQueue, from, to);
    }

    public static Integer getCurrentQueueIndex(){
        return currentQueueIndex;
    }

    public static Song getCurrentSong(){
        return playingQueue.get(currentQueueIndex);
    }

    public static Boolean hasNext(){
        return (playingQueue.size() - 1) != currentQueueIndex;
    }

    public static Boolean hasPrevious(){
        return currentQueueIndex != 0;
    }

    public static void updateToNextSong(){
        currentQueueIndex = currentQueueIndex + 1;
    }

    public static void updateToPreviousSong(){
        currentQueueIndex = currentQueueIndex - 1;
    }

    public static void handleShuffleClick(Context context){

        if(SharedPrefsFile.getShuffleMode(context)){

            Song song = getCurrentSong();
            Collections.sort(playingQueue, new Comparator<Song>() {
                @Override
                public int compare(Song song, Song t1) {
                    return song.getTitle().compareTo(t1.getTitle());
                }
            });

            currentQueueIndex = playingQueue.indexOf(song);

            SharedPrefsFile.setShuffleMode(context, false);
        }
        else{
            Song song = getCurrentSong();
            playingQueue.remove(currentQueueIndex);

            Collections.shuffle(playingQueue);

            playingQueue.add(0, song);

            currentQueueIndex = 0;

            SharedPrefsFile.setShuffleMode(context, true);
        }
    }

}



