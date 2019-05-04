package reverblabs.apps.aura.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AlertDialog;
import reverblabs.apps.aura.R;


public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);

        deleteCache();

    }

    private void deleteCache(){
        final Preference deleteCache = findPreference("delete_cache");

        deleteCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.delete_message_preference))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int which){
//                                ImageFetcher imageFetcher = Utils.getImageFetcher(getActivity());
//                                imageFetcher.clearCache();

                                new AsyncTask<Void, Void,Void>(){
                                    @Override
                                    protected Void doInBackground(Void ...params){
                                        Glide.get(getActivity()).clearDiskCache();
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void value){
                                        Toast.makeText(getActivity(), "Cache cleared", Toast.LENGTH_SHORT).show();
                                    }
                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);

                            }
                        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

                return true;
            }
        });
    }
}
