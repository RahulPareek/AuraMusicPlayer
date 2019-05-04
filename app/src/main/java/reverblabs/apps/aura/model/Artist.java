package reverblabs.apps.aura.model;

public class Artist {

    public long id;
    public String artist;
    public int noOfAlbums;
    public int noOfSongs;


    public Artist(long aid, String aartist, int anoOfAlbums, int aNoOfSongs) {
        id = aid;
        artist = aartist;
        noOfAlbums = anoOfAlbums;
        noOfSongs = aNoOfSongs;
    }

    public Artist(){}

    public long getid(){
        return id;
    }
    public String getArtist(){
        return artist;
    }
    public int getNoOfAlbums(){
        return noOfAlbums;
    }
    public int getNoOfSongs(){
        return noOfSongs;
    }

}
