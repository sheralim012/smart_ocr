package pk.edu.pucit.smartocr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;
import pk.edu.pucit.smartocr.utilities.Constants;
import uk.co.senab.photoview.PhotoViewAttacher;

public class EditImageActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnPhotoEditorListener,
        PropertiesBSFragment.Properties {

    private ProgressBar progressBarLoadingActivityEditImage;
    private RelativeLayout relativeLayoutRootActivityEditImage;
    private Toolbar toolbarTopActivityEditImage, toolbarBottomActivityEditImage;
    private ImageView imageViewCloseActivityEditImage, imageViewUndoActivityEditImage, imageViewRedoActivityEditImage, imageViewCropRotateActivityEditImage, imageViewEditActivityEditImage, imageViewTitleActivityEditImage, imageViewEraserActivityEditImage, imageViewShareActivityEditImage, imageViewCheckActivityEditImage;
    private PhotoEditorView imageViewImageActivityEditImage;
    private PhotoEditor photoEditor;
    private PropertiesBSFragment propertiesBSFragment;
    private File imageFile;
    private String imagePath;
    private String activityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        initialize();
        setImage();
        setPhotoViewAttacher();
        propertiesBSFragment = new PropertiesBSFragment();
        propertiesBSFragment.setPropertiesChangeListener(this);
        photoEditor = new PhotoEditor.Builder(this, imageViewImageActivityEditImage).build();
        photoEditor.setOnPhotoEditorListener(this);
    }

    private void setImage() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    imagePath = bundle.getString(Constants.IMAGE_PATH_KEY);
                    activityName = bundle.getString(Constants.ACTIVITY_NAME);
                    if (imagePath != null) {
                        imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            Glide.with(this)
                                    .load(imagePath)
                                    .apply(RequestOptions.skipMemoryCacheOf(true))
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .into(imageViewImageActivityEditImage.getSource());
                        } else {
                            throw new Exception();
                        }
                    } else {
                        throw new Exception();
                    }
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

    private void initialize() {
        progressBarLoadingActivityEditImage = findViewById(R.id.progress_bar_loading_activity_edit_image);
        relativeLayoutRootActivityEditImage = findViewById(R.id.relative_layout_root_view_activity_edit_image);
        toolbarTopActivityEditImage = findViewById(R.id.toolbar_top_activity_edit_image);
        toolbarBottomActivityEditImage = findViewById(R.id.toolbar_bottom_activity_edit_image);
        imageViewCloseActivityEditImage = findViewById(R.id.image_view_close_activity_edit_image);
        imageViewUndoActivityEditImage = findViewById(R.id.image_view_undo_activity_edit_image);
        imageViewRedoActivityEditImage = findViewById(R.id.image_view_redo_activity_edit_image);
        imageViewCropRotateActivityEditImage = findViewById(R.id.image_view_crop_rotate_activity_edit_image);
        imageViewTitleActivityEditImage = findViewById(R.id.image_view_title_activity_edit_image);
        imageViewEditActivityEditImage = findViewById(R.id.image_view_edit_activity_edit_image);
        imageViewImageActivityEditImage = findViewById(R.id.image_view_image_activity_edit_image);
        imageViewEraserActivityEditImage = findViewById(R.id.image_view_eraser_activity_edit_image);
        imageViewShareActivityEditImage = findViewById(R.id.image_view_share_activity_edit_image);
        imageViewCheckActivityEditImage = findViewById(R.id.image_view_check_activity_edit_image);
        imageViewCloseActivityEditImage.setOnClickListener(this);
        imageViewUndoActivityEditImage.setOnClickListener(this);
        imageViewRedoActivityEditImage.setOnClickListener(this);
        imageViewCropRotateActivityEditImage.setOnClickListener(this);
        imageViewTitleActivityEditImage.setOnClickListener(this);
        imageViewEditActivityEditImage.setOnClickListener(this);
        imageViewEraserActivityEditImage.setOnClickListener(this);
        imageViewShareActivityEditImage.setOnClickListener(this);
        imageViewCheckActivityEditImage.setOnClickListener(this);
    }

    private void setPhotoViewAttacher() {
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageViewImageActivityEditImage.getSource());
        photoViewAttacher.update();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_close_activity_edit_image:
                onClickClose();
                break;
            case R.id.image_view_undo_activity_edit_image:
                onClickUndo();
                break;
            case R.id.image_view_redo_activity_edit_image:
                onClickRedo();
                break;
            case R.id.image_view_crop_rotate_activity_edit_image:
                onClickCropRotate();
                break;
            case R.id.image_view_title_activity_edit_image:
                onClickTitle();
                break;
            case R.id.image_view_eraser_activity_edit_image:
                onClickEraser();
                break;
            case R.id.image_view_edit_activity_edit_image:
                onClickEdit();
                break;
            case R.id.image_view_share_activity_edit_image:
                onClickShare();
                break;
            case R.id.image_view_check_activity_edit_image:
                onClickCheck();
                break;
        }
    }

    private void onClickEraser() {
        photoEditor.brushEraser();
    }

    private void onClickRedo() {
        photoEditor.redo();
    }

    private void onClickUndo() {
        photoEditor.undo();
    }

    private void onClickEdit() {
        photoEditor.setBrushDrawingMode(true);
        propertiesBSFragment.show(getSupportFragmentManager(), propertiesBSFragment.getTag());
    }

    private void onClickTitle() {
        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                photoEditor.addText(inputText, colorCode);
            }
        });
    }

    private void onClickCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();

            photoEditor.saveAsFile(imagePath, saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagepath) {
                    if (activityName != null && activityName.equals(Constants.FILE_MANAGER_ACTIVITY)) {
                        finish();
                    } else {
                        (new OCRAsyncTask()).execute();
                    }
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Snackbar.make(relativeLayoutRootActivityEditImage, "Failed to Save Image", Snackbar.LENGTH_INDEFINITE);
                }
            });
        }
    }

    public class OCRAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLoadingActivityEditImage.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseVisionImage firebaseVisionImage = null;
            try {
                Uri imageUri = FileProvider.getUriForFile(EditImageActivity.this, "pk.edu.pucit.smartocr.fileprovider", imageFile);
                firebaseVisionImage = FirebaseVisionImage.fromFilePath(EditImageActivity.this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            progressBarLoadingActivityEditImage.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(EditImageActivity.this, EditTextActivity.class);
                            intent.putExtra("text", result.getText());
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Can't convert to text", Toast.LENGTH_SHORT).show();
                                }
                            });
            return null;
        }

    }

    private void onClickCropRotate() {
        Uri sourceUri = Uri.fromFile(imageFile);
        Uri destinationUri = Uri.fromFile(imageFile);
        UCrop.Options uCrpOptions = new UCrop.Options();
        uCrpOptions.setToolbarTitle("Crop|Rotate");
        uCrpOptions.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        uCrpOptions.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        uCrpOptions.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        uCrpOptions.setFreeStyleCropEnabled(true);
        UCrop.of(sourceUri, destinationUri)
                .withOptions(uCrpOptions)
                .useSourceImageAspectRatio()
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Glide.with(this)
                    .load(imagePath)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(imageViewImageActivityEditImage.getSource());
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            Glide.with(this)
                    .load(imagePath)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(imageViewImageActivityEditImage.getSource());
        }
    }

    private void onClickClose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_image_eiting);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    private void onClickShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri imageUri = FileProvider.getUriForFile(this, "pk.edu.pucit.smartocr.fileprovider", imageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_via)));
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onColorChanged(int colorCode) {
        photoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        photoEditor.setBrushSize(brushSize);
    }

}
