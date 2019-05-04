package reverblabs.apps.aura.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.ui.adapters.TabsPagerAdapter;
import reverblabs.apps.aura.interfaces.MainActivityCallback;

public class MainFragment extends Fragment {

    private Context context;

    public MainFragment() {
    }

    MainActivityCallback mainActivityCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            mainActivityCallback =(MainActivityCallback) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.music_library));

        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.drawer_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                mainActivityCallback.openNavigationDrawer();
            }
        });

        PagerAdapter pagerAdapter = new TabsPagerAdapter(getChildFragmentManager(), context);

        ViewPager viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

}
