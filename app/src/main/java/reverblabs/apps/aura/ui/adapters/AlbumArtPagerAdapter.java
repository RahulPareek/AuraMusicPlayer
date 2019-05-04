package reverblabs.apps.aura.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.glide.CustomTarget;
import reverblabs.apps.aura.glide.GlideApp;
import reverblabs.apps.aura.glide.album.AlbumImage;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.playback.QueueManager;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;

public class AlbumArtPagerAdapter extends FragmentStatePagerAdapter {


    public AlbumArtPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int pos){
        return AlbumArtFragment.newInstance(pos);
    }

    @Override
    public int getCount(){
        if (QueueManager.getCurrentSong() != null) {
            if (QueueManager.getCurrentQueueIndex() != null) {

                return QueueManager.getPlayingQueue().size();
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public static class AlbumArtFragment extends Fragment {

        View rootView;

        int position;

        private Context context;

        public static AlbumArtFragment newInstance(int pos) {
            AlbumArtFragment albumArtFragment = new AlbumArtFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", pos);
            albumArtFragment.setArguments(bundle);
            return albumArtFragment;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            context = getActivity();

            position = getArguments().getInt("position");
        }

        @Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            rootView = layoutInflater.inflate(R.layout.album_art_fragment, viewGroup, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);


            ArrayList<Song> songArrayList;
            songArrayList = QueueManager.getPlayingQueue();
            Song nowPlaying = new Song();

            if (position < songArrayList.size()) {
                nowPlaying = songArrayList.get(position);
            }


            ImageView album_art = rootView.findViewById(R.id.album_art);
            ImageView background = rootView.findViewById(R.id.music_playing_fragment_background_image);

            TextView songname;
            TextView artistname;

            songname = rootView.findViewById(R.id.song_name);
            artistname = rootView.findViewById(R.id.artist_name);

            if (nowPlaying != null) {

                songname.setText(nowPlaying.getTitle());
                artistname.setText(nowPlaying.getArtist());

                GlideApp.with(context)
                        .load(new AlbumImage(nowPlaying.getArtist(),
                                nowPlaying.getAlbum(),
                                (int) nowPlaying.getAlBumId()))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .apply(new RequestOptions().
                                override(Constants.LARGE_SIZE, Constants.LARGE_SIZE))
                        .transition(GenericTransitionOptions.with(R.anim.fade_in))
                        .placeholder(R.drawable.album_art)
                        .error(R.drawable.album_art)
                        .into(album_art);

                setBlurredImage(nowPlaying, background);


            }
            else{
                album_art.setImageResource(R.drawable.album_art);
                background.setImageResource(R.drawable.album_art);
            }
        }

        private void setBlurredImage(Song nowPlaying, final ImageView background){


            if (MusicHelper.isKitkat()) {

                GlideApp.with(context)
                        .asBitmap()
                        .load(new AlbumImage(nowPlaying.getArtist(),
                                nowPlaying.getAlbum(),
                                (int) nowPlaying.getAlBumId()))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.album_art)
                        .error(R.drawable.album_art)
                        .apply(new RequestOptions().
                                override(Constants.MEDIUM_SIZE, Constants.MEDIUM_SIZE)
                                .placeholder(R.drawable.album_art)
                                .error(R.drawable.album_art))
                        .into(new CustomTarget(){

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                Bitmap blurredImage = blurRenderScript(resource);
                                background.setImageBitmap(blurredImage);
                            }
                        });
            }
            else{
                GlideApp.with(context)
                        .load(new AlbumImage(nowPlaying.getArtist(),
                                nowPlaying.getAlbum(),
                                (int) nowPlaying.getAlBumId()))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .apply(new RequestOptions().
                                override(Constants.SMALL_SIZE, Constants.SMALL_SIZE))
                        .transition(GenericTransitionOptions.with(R.anim.fade_in))
                        .placeholder(R.drawable.album_art)
                        .error(R.drawable.album_art)
                        .into(background);

                RelativeLayout relativeLayout = rootView.findViewById(R.id.blur_relative_layout);
                relativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent_background));

            }
        }

        @SuppressLint("NewApi")
        private Bitmap blurRenderScript(Bitmap orgBitmap){

            if (orgBitmap != null) {

                try {
                    orgBitmap = RGB565toARGB888(orgBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = Bitmap.createBitmap(
                        orgBitmap.getWidth(), orgBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);

                RenderScript renderScript = RenderScript.create(context);

                Allocation blurInput = Allocation.createFromBitmap(renderScript, orgBitmap);
                Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

                ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                        Element.U8_4(renderScript));
                blur.setInput(blurInput);
                blur.setRadius(25);
                blur.forEach(blurOutput);

                blurOutput.copyTo(bitmap);
                renderScript.destroy();

                return bitmap;
            }
            else {
                return null;
            }


        }

        private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
            int numPixels = img.getWidth() * img.getHeight();
            int[] pixels = new int[numPixels];

            img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

            Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

            result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            return result;
        }
    }
}
