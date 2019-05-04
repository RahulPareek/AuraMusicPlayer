package reverblabs.apps.aura.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    public long ID;
    public String Path;
    public String Artist;
    public long ArtistId;
    public String Album;
    public long AlbumId;
    public String Title;
    public int Duration;
    public String Year;

    @Override

    public int describeContents(){
            return 0;
        }

    @Override
    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeLong(ID);
        parcel.writeString(Path);
        parcel.writeString(Artist);
        parcel.writeLong(ArtistId);
        parcel.writeString(Album);
        parcel.writeLong(AlbumId);
        parcel.writeString(Title);
        parcel.writeInt(Duration);
        parcel.writeString(Year);
    }
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
        public Song createFromParcel(Parcel in){
                return new Song(in);
            }
        public Song[] newArray(int size){
                return new Song[size];
            }
    };

     public Song(Parcel in){
         ID=in.readLong();
         Path=in.readString();
         Artist=in.readString();
         ArtistId=in.readLong();
         Album=in.readString();
         AlbumId=in.readLong();
         Title=in.readString();
         Duration=in.readInt();
         Year=in.readString();

        }

        public Song(long sID, String sPath, String sArtsit, long sartistId, String sAlbum, long sAlbumID, String sTitle, int sDuration, String sYear){
            this.ID=sID;
            this.Path=sPath;
            this.Artist=sArtsit;
            this.ArtistId = sartistId;
            this.Album=sAlbum;
            this.AlbumId=sAlbumID;
            this.Title=sTitle;
            this.Duration=sDuration;
            this.Year=sYear;
        }
        public Song(){
        }

        public String getPath(){
            return Path;
        }

        public Long getID(){
            return ID;
        }

        public String getArtist() {
            return Artist;
        }

        public String getAlbum() {
            return Album;
        }

        public long getAlBumId(){
            return AlbumId;
        }

        public String getTitle() {
            return Title;
        }

        public int getDuration() {return Duration; }


}



