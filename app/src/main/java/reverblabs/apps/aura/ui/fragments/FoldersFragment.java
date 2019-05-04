package reverblabs.apps.aura.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.interfaces.DeleteInterFace;
import reverblabs.apps.aura.ui.adapters.AdapterForFolders;
import reverblabs.apps.aura.dialogs.DeleteDialog;
import reverblabs.apps.aura.dialogs.PlaylistDialog;
import reverblabs.apps.aura.interfaces.MainActivityCallback;
import reverblabs.apps.aura.model.Song;
import reverblabs.apps.aura.utils.Constants;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;


public class FoldersFragment extends BaseFragment {

    MainActivityCallback mainActivityCallback;

    String currentPath;

    AdapterForFolders adapterForFolders;


    public FoldersFragment() {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_folders, container, false);

        Context context = getActivity();

        currentPath = getArguments().getString(Constants.PATH);

        if (currentPath == null){
            currentPath = SharedPrefsFile.getUserPreferredFolder(context);
        }

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.drawer_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityCallback.openNavigationDrawer();
            }
        });

        File temp = new File(currentPath);

        String tempPath = temp.getName();
        toolbar.setTitle(tempPath);


        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapterForFolders = new AdapterForFolders(context, this, currentPath);

        recyclerView.setAdapter(adapterForFolders);

        return rootView;
    }

    public void navigateToDirectory(File file){
        mainActivityCallback.createFoldersFragment(file.getPath());
    }

    public void backButtonPressed(){
        File file = new File(currentPath);

        File parent = file.getParentFile();


        if (parent != null && parent.canRead()) {
            navigateToDirectory(parent);
        }
        else {
            mainActivityCallback.executeBackButtonCall();
        }

        Log.i(Constants.TAG, file.getPath());
    }


    @Override
    public ArrayList<Integer> getPositionOfSelectedItems(){
        return null;
    }

    @Override
    public ArrayList<Song> getActionModeSelectedItems(){
        return null;
    }

    @Override
    public DeleteInterFace getDeleteInterface(){
        return null;
    }

    @Override
    public void clearActionMode(){}

}
