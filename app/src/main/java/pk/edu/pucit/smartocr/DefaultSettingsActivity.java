package pk.edu.pucit.smartocr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.SharedPreferencesHelper;

public class DefaultSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewFolderActivityDefaultSettings;
    private TextView textViewDefaultScreenActivityDefaultSettings;
    private CheckBox checkboxActivityDefaultSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_settings);
        initialize();
        int defaultScreen = SharedPreferencesHelper.getInt(this, Constants.DEFAULT_SCREEN_KEY, Constants.DEFAULT_SCREEN_CAMERA);
        if (defaultScreen == Constants.DEFAULT_SCREEN_CAMERA) {
            checkboxActivityDefaultSettings.setChecked(false);
        } else {
            checkboxActivityDefaultSettings.setChecked(true);
        }
    }

    private void initialize() {
        imageViewFolderActivityDefaultSettings = findViewById(R.id.image_view_folder_activity_default_settings);
        textViewDefaultScreenActivityDefaultSettings = findViewById(R.id.text_view_default_screen_activity_default_settings);
        checkboxActivityDefaultSettings = findViewById(R.id.check_box_select_activity_default_settings);
        imageViewFolderActivityDefaultSettings.setOnClickListener(this);
        textViewDefaultScreenActivityDefaultSettings.setOnClickListener(this);
        checkboxActivityDefaultSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (checkboxActivityDefaultSettings.isChecked()) {
            SharedPreferencesHelper.putInt(this, Constants.DEFAULT_SCREEN_KEY, Constants.DEFAULT_SCREEN_FILE_MANAGER);
        } else {
            SharedPreferencesHelper.putInt(this, Constants.DEFAULT_SCREEN_KEY, Constants.DEFAULT_SCREEN_CAMERA);
        }
    }

}
