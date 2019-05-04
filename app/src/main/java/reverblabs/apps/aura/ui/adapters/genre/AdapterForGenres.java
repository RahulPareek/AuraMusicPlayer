package reverblabs.apps.aura.ui.adapters.genre;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.AdapterToFragmentCallbacks;
import reverblabs.apps.aura.ui.fragments.genre.GenresFragment;
import reverblabs.apps.aura.model.Genre;
import reverblabs.apps.aura.utils.Constants;

    public class AdapterForGenres extends RecyclerView.Adapter<AdapterForGenres.ViewHolder> implements
             FastScrollRecyclerView.SectionedAdapter{

        private ArrayList<Genre> genreDataset = new ArrayList<>() ;
        Context context;

        private Cursor cursor;

        private AdapterToFragmentCallbacks fragmentCallbacks;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            ImageView album_art;
            TextView genre_name;

            ViewHolder(View v){
                super(v);
                album_art = v.findViewById(R.id.genre_image);
                genre_name = v.findViewById(R.id.firstline_genre);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View view){

                Genre genre;
                genre = genreDataset.get(getAdapterPosition());
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.GENRE_ID,genre.genreId);
                bundle.putString(Constants.GENRE_NAME,genre.genre);

                fragmentCallbacks.createGenreDetailFragment(bundle);
            }
        }

        public AdapterForGenres(Context cx, Cursor dataCursor, GenresFragment fragment){
            genreDataset.clear();
            context = cx;
            cursor = dataCursor;

            fragmentCallbacks = fragment;
        }


        @Override
        public AdapterForGenres.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_genres, parent,false);
            return new ViewHolder(v);
        }

        public Cursor swapCursor(Cursor mCursor){
            if(cursor == mCursor){
                return null;
            }
            Cursor oldCursor = cursor;
            this.cursor = mCursor;

            try{
                cursor.moveToFirst();
            }
            catch (NullPointerException e){
            }

            if(cursor!=null) {
                if (cursor.getCount() > 0) {
                    genreDataset.clear();
                    do {
                        long aid = cursor.getLong(0);
                        String aName = cursor.getString(1);

                        Genre genre = new Genre(aid, aName);
                        genreDataset.add(genre);
                    } while (cursor.moveToNext());

                    this.notifyDataSetChanged();
                }
            }
            return oldCursor;
        }


        @NonNull
        public String getSectionName(int pos){
            return genreDataset.get(pos).genre.substring(0,1);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.genre_name.setText(genreDataset.get(position).genre);
            holder.album_art.setImageResource(R.drawable.genre_default);

        }

        @Override
        public int getItemCount () {
            if (genreDataset != null) {
                return genreDataset.size();
            }else {
                return 0;
            }
        }

    }