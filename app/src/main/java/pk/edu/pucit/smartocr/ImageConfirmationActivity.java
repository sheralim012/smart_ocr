package pk.edu.pucit.smartocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.entity.PictureEntity;
import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.DatabaseHelper;
import pk.edu.pucit.smartocr.utilities.DateTimeHelper;

public class ImageConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarTopActivityImageConfirmation;
    private ImageView imageViewCloseActivityImageConfirmation, imageViewCheckActivityImageConfirmation, imageViewImageActivityImageConfirmation;
    private TextView textViewFileNameActivityImageConfirmation;
    private EditText editTextFileNameActivityImageConfirmation;
    private RelativeLayout relativeLayoutImageActivityImageConfirmation;
    private String imageName;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirmation);
        initialize();
        setImage();
    }

    private void initialize() {
        toolbarTopActivityImageConfirmation = findViewById(R.id.toolbar_top_activity_image_confirmation);
        textViewFileNameActivityImageConfirmation = findViewById(R.id.text_view_file_name_activity_image_confirmation);
        editTextFileNameActivityImageConfirmation = findViewById(R.id.edit_text_file_name_activity_image_confirmation);
        imageViewCheckActivityImageConfirmation = findViewById(R.id.image_view_check_activity_image_confirmation);
        imageViewCloseActivityImageConfirmation = findViewById(R.id.image_view_close_activity_image_confirmation);
        imageViewImageActivityImageConfirmation = findViewById(R.id.image_view_image_activity_image_confirmation);
        relativeLayoutImageActivityImageConfirmation = findViewById(R.id.relative_layout_image_activity_image_confirmation);
        imageViewCloseActivityImageConfirmation.setOnClickListener(this);
        imageViewCheckActivityImageConfirmation.setOnClickListener(this);
        relativeLayoutImageActivityImageConfirmation.setOnClickListener(this);
        textViewFileNameActivityImageConfirmation.setOnClickListener(this);
    }

    private void setImage() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    imageName = bundle.getString(Constants.IMAGE_NAME_KEY);
                    imagePath = bundle.getString(Constants.IMAGE_PATH_KEY);
                    setImageName(imageName);
                    Glide.with(this).load(imagePath).into(imageViewImageActivityImageConfirmation);
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.image_does_not_exist);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void setImageName(String imageName) {
        textViewFileNameActivityImageConfirmation.setText(imageName);
        editTextFileNameActivityImageConfirmation.setText(imageName);
        editTextFileNameActivityImageConfirmation.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_check_activity_image_confirmation:
                onClickCheck();
                break;
            case R.id.image_view_close_activity_image_confirmation:
                onClickClose();
                break;
            case R.id.text_view_file_name_activity_image_confirmation:
                onClickTextView();
                break;
            case R.id.relative_layout_image_activity_image_confirmation:
                onClickImage();
                break;
        }
    }

    private void onClickTextView() {
        textViewFileNameActivityImageConfirmation.setText("");
        editTextFileNameActivityImageConfirmation.setVisibility(View.VISIBLE);
    }

    private void onClickImage() {
        imageName = editTextFileNameActivityImageConfirmation.getText().toString();
        editTextFileNameActivityImageConfirmation.setVisibility(View.INVISIBLE);
        textViewFileNameActivityImageConfirmation.setText(imageName);
    }

    private void onClickCheck() {
        PictureEntity pictureEntity = new PictureEntity();
        pictureEntity.setActive(1);
        pictureEntity.setCreatedOn(DateTimeHelper.getCurrentDate());
        pictureEntity.setModifiedOn(DateTimeHelper.getCurrentDate());
        pictureEntity.setPath(imagePath);
        pictureEntity.setParent(DatabaseHelper.getInstance(this).getPicturesFolderId());
        pictureEntity.setImageName(imageName);
        Database.getInstance(this).pictureDao().insertImage(pictureEntity);

        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra(Constants.IMAGE_PATH_KEY, imagePath);
        startActivity(intent);
        finish();
    }

    private void onClickClose() {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
        finish();
    }

}
