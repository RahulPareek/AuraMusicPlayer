package reverblabs.apps.aura.model;

public class Album {

    public long id;
    public String album;
    public String artist;
    public int noOfSongs;
    public String year;

    public Album(long aid, String aAlbum, String aArtist, int aNoOfSongs,String ayear) {
        id = aid;
        album = aAlbum;
        artist = aArtist;
        noOfSongs = aNoOfSongs;
        year = ayear;
        }

    public Album(){}

    public long getid(){
        return id;
    }
    public String getAlbum(){
        return album;
    }
    public String getArtist(){
        return artist;
    }
    public int getNoOfSongs(){
        return noOfSongs;
    }

}
