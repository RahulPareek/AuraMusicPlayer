package reverblabs.apps.aura.ui.activities;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import reverblabs.apps.aura.R;
import reverblabs.apps.aura.utils.MusicHelper;
import reverblabs.apps.aura.utils.SharedPrefsFile;

import static reverblabs.apps.aura.R.string.hz;

public class EqualizerActivity extends AppCompatActivity {

    short noOfFrequencyBands;

    short equalizerLowBandLevel;
    short equalizerHighBandLevel;

    LinearLayout linearLayout;

    Equalizer equalizer;
    BassBoost bassBoost;
    PresetReverb presetReverb;
    Virtualizer virtualizer;

    SeekBar seekBar;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        context = getApplicationContext();

        if(getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getString(R.string.audio_effects));

        linearLayout = (LinearLayout) findViewById(R.id.equalier_layout);

//        if (MusicHelper.useAuraEqualizer(context)) {
//
//            equalizer = HelperClass.auraMusicService.equalizer;
//            bassBoost = HelperClass.auraMusicService.bassBoost;
//            presetReverb = HelperClass.auraMusicService.presetReverb;
//            virtualizer = HelperClass.auraMusicService.virtualizer;
//
//        }
//        else {
//            equalizer = new Equalizer(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
//            bassBoost = new BassBoost(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
//            virtualizer = new Virtualizer(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
//            presetReverb = new PresetReverb(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
//        }
//
//        if (equalizer == null){
//            equalizer = new Equalizer(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
//        }

        getFrequencyBands();


        initializeResources();

        setPresetSpinner();
        setBassBoost();
        setVirtualizer();
        setPresetReverb();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home:
                onBackPressed();
                finish();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFrequencyBands(){

        noOfFrequencyBands = equalizer.getNumberOfBands();

        equalizerLowBandLevel = equalizer.getBandLevelRange()[0];
        equalizerHighBandLevel = equalizer.getBandLevelRange()[1];
    }

    private void initializeResources() {

        for (short i = 0; i < noOfFrequencyBands; i++) {

            final short bandIndex = i;

            TextView frequencyHeaderText = new TextView(this);
            frequencyHeaderText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderText.setGravity(Gravity.CENTER_HORIZONTAL);

            frequencyHeaderText.setText(String.format(getString(hz), equalizer.getCenterFreq(bandIndex) / 1000));

            linearLayout.addView(frequencyHeaderText);

            LinearLayout seekBarLayout = new LinearLayout(this);
            seekBarLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView lowerBandLevel = new TextView(this);
            lowerBandLevel.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            lowerBandLevel.setText(getString(R.string.db, equalizerLowBandLevel / 100));

            TextView higherBandLevel = new TextView(this);
            higherBandLevel.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            higherBandLevel.setText(getString(R.string.db, equalizerHighBandLevel / 100));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.weight = 1;

            seekBar = new SeekBar(this);
            seekBar.setId(i);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(equalizerHighBandLevel - equalizerLowBandLevel);

            seekBar.setProgress(equalizer.getBandLevel(i));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                    equalizer.setBandLevel(bandIndex, (short) (progress + equalizerLowBandLevel));


                    if(SharedPrefsFile.getEqualizerPresetMode(context) == equalizer.getNumberOfPresets()){
                        SharedPrefsFile.setEqualizerPresetValues(context, progress , seekBar.getId(), noOfFrequencyBands);
                    }


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seekBarLayout.addView(lowerBandLevel);
            seekBarLayout.addView(seekBar);
            seekBarLayout.addView(higherBandLevel);

            linearLayout.addView(seekBarLayout);

        }
    }

    private void setPresetSpinner(){

        ArrayList<String> presetNames = new ArrayList<>();
        Spinner presetSpinner = (Spinner) findViewById(R.id.equalizer_preset_spinner);
        if (equalizer != null) {

            for (short i = 0; i < equalizer.getNumberOfPresets(); i++) {
                presetNames.add(equalizer.getPresetName(i));
            }
        }

        presetNames.add(getString(R.string.equalizer_custom));

        ArrayAdapter<String> presetAdapter = new ArrayAdapter(this, R.layout.spinner_text, presetNames);
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        presetSpinner.setAdapter(presetAdapter);
        presetSpinner.setSelection(SharedPrefsFile.getEqualizerPresetMode(context));

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


                SharedPrefsFile.setEqualizerPreset(context, position);


                if(position == adapterView.getCount() - 1){

                    if(SharedPrefsFile.getCustomPresetLevels(context) == null) {
                        SharedPrefsFile.setEqualizerPreset(context, position);

                        for (short i = 0; i < noOfFrequencyBands; i++) {

                            SeekBar seekBar = (SeekBar) findViewById(i);
                            seekBar.setProgress((equalizerHighBandLevel - equalizerLowBandLevel) / 2);

                            SharedPrefsFile.setEqualizerPresetValues(context, (equalizerHighBandLevel - equalizerLowBandLevel) / 2, i, noOfFrequencyBands);
                        }
                    }
                    else {
                        ArrayList<Short> values = SharedPrefsFile.getCustomPresetLevels(context);

                        for (short i = 0; i < noOfFrequencyBands; i++){
                            SeekBar seekBar = (SeekBar) findViewById(i);
                            seekBar.setProgress(values.get(i));

                        }
                    }
                }
                else {

                  try {
                      equalizer.usePreset((short) position);
                  }
                  catch (UnsupportedOperationException e){
                      e.printStackTrace();
                  }

                    for (short i = 0; i < noOfFrequencyBands; i++) {

                        SeekBar seekBar = (SeekBar) findViewById(i);
                        seekBar.setProgress(equalizer.getBandLevel(i) - equalizerLowBandLevel);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setBassBoost(){

        if (bassBoost == null) {
            //bassBoost = new BassBoost(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
        }


        if(bassBoost.getStrengthSupported()){

            TextView bassBoostText =  (TextView) findViewById(R.id.bass_boost_text);
            bassBoostText.setText(getString(R.string.bass_boost));

            SeekBar bassBar = (SeekBar) findViewById(R.id.bass_bar);

            bassBar.setMax(1000);
            bassBar.setProgress(SharedPrefsFile.getBassBoostValue(context));

            bassBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    short value = (short) i;
                    try {
                        bassBoost.setStrength(value);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                    SharedPrefsFile.setBassBoost(context, i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
        else {
            LinearLayout bassBoostLayout = (LinearLayout) findViewById(R.id.bass_boost_layout);
            bassBoostLayout.setVisibility(View.GONE);
        }
    }


    private void setVirtualizer(){
        if (virtualizer == null){
            //virtualizer = new Virtualizer(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
        }

        if(!virtualizer.getStrengthSupported())
            return;

        LinearLayout virtualizerLayout = (LinearLayout) findViewById(R.id.virtualizer_layout);
        virtualizerLayout.setVisibility(View.VISIBLE);


        TextView virtualizerText = (TextView) findViewById(R.id.virtualizer_text);
        virtualizerText.setText(getString(R.string.virtualizer));

        final SeekBar virtualizerBar = (SeekBar) findViewById(R.id.virtualizer_seekbar);

        virtualizerBar.setMax(1000);

        virtualizerBar.setProgress(SharedPrefsFile.getVirtualizerValue(context));

        virtualizerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                short value = (short) i;

                try {
                    virtualizer.setStrength(value);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }

                SharedPrefsFile.setVirtualizerValue(context, i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setPresetReverb(){

        if (presetReverb == null){
           // presetReverb = new PresetReverb(0, HelperClass.auraMusicService.mediaPlayer.getAudioSessionId());
        }

        TextView reverbText = (TextView) findViewById(R.id.reverb_text);
        reverbText.setText(R.string.preset_reverb);

        String[] reverbNames={
                            getString(R.string.none),
                            getString(R.string.small_room),
                            getString(R.string.medium_room),
                            getString(R.string.large_room),
                            getString(R.string.medium_hall),
                            getString(R.string.large_hall),
                            getString(R.string.plate)};


        ArrayAdapter<String> reverbAdapter = new ArrayAdapter<>(context, R.layout.spinner_text, reverbNames);

        Spinner reverbSpinner = (Spinner) findViewById(R.id.reverb_spinner);

        reverbSpinner.setAdapter(reverbAdapter);
        reverbSpinner.setSelection(SharedPrefsFile.getPresetReverb(context));

        reverbSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        presetReverb.setPreset(PresetReverb.PRESET_NONE);
                        SharedPrefsFile.setPresetReverb(context, PresetReverb.PRESET_NONE );

                        break;
                    case 1:
                        presetReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
                        SharedPrefsFile.setPresetReverb(context, PresetReverb.PRESET_SMALLROOM );

                        break;
                    case 2:
                        presetReverb.setPreset(PresetReverb.PRESET_MEDIUMROOM);
                        SharedPrefsFile.setPresetReverb(context,PresetReverb.PRESET_MEDIUMROOM );

                        break;
                    case 3:
                        presetReverb.setPreset(PresetReverb.PRESET_LARGEROOM);
                        SharedPrefsFile.setPresetReverb(context, PresetReverb.PRESET_LARGEROOM );

                        break;
                    case 4:
                        presetReverb.setPreset(PresetReverb.PRESET_MEDIUMHALL);
                        SharedPrefsFile.setPresetReverb(context,PresetReverb.PRESET_MEDIUMHALL );

                        break;
                    case 5:
                        presetReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
                        SharedPrefsFile.setPresetReverb(context, PresetReverb.PRESET_LARGEHALL );

                        break;
                    case 6:

                        presetReverb.setPreset(PresetReverb.PRESET_PLATE);
                        SharedPrefsFile.setPresetReverb(context, PresetReverb.PRESET_PLATE );

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
