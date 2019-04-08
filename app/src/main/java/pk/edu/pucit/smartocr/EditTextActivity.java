package pk.edu.pucit.smartocr;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.entity.DocumentEntity;
import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.DatabaseHelper;
import pk.edu.pucit.smartocr.utilities.DateTimeHelper;
import pk.edu.pucit.smartocr.utilities.DirectoryHelper;

public class EditTextActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewEditActivityEditText,
            imageViewRedoActivityEditText,
            imageViewUndoActivityEditText,
            imageViewSaveActivityEditText,
            imageViewCameraActivityEditText,
            imageViewPdfActivityEditText;
    private EditText editTextTextActivityEditText;
    private EditTextUndoRedo helper;
    private Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        initialize();
        setText();
    }

    private void setText() {
        editTextTextActivityEditText.setText(getIntent().getExtras().getString("text"));
    }

    private void initialize() {
        imageViewEditActivityEditText = findViewById(R.id.image_view_edit_activity_edit_text);
        imageViewRedoActivityEditText = findViewById(R.id.image_view_redo_activity_edit_text);
        imageViewUndoActivityEditText = findViewById(R.id.image_view_undo_activity_edit_text);
        imageViewSaveActivityEditText = findViewById(R.id.image_view_save_activity_edit_text);
        imageViewCameraActivityEditText = findViewById(R.id.image_view_camera_activity_edit_text);
        imageViewPdfActivityEditText = findViewById(R.id.image_view_pdf_activity_edit_text);
        editTextTextActivityEditText = findViewById(R.id.edit_text_text_activity_edit_text);
        imageViewEditActivityEditText.setOnClickListener(this);
        imageViewRedoActivityEditText.setOnClickListener(this);
        imageViewUndoActivityEditText.setOnClickListener(this);
        imageViewSaveActivityEditText.setOnClickListener(this);
        imageViewCameraActivityEditText.setOnClickListener(this);
        imageViewPdfActivityEditText.setOnClickListener(this);
        helper = new EditTextUndoRedo(editTextTextActivityEditText);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_view_edit_activity_edit_text) {
            if (!editMode) {
                editMode();
            } else {
                viewMode();
            }
        } else if (v.getId() == R.id.image_view_redo_activity_edit_text) {
            helper.redo();
        } else if (v.getId() == R.id.image_view_undo_activity_edit_text) {
            helper.undo();
        } else if (v.getId() == R.id.image_view_save_activity_edit_text) {
            saveTextFile();
        } else if (v.getId() == R.id.image_view_pdf_activity_edit_text) {
            generateAndSavePDF();
        } else if (v.getId() == R.id.image_view_camera_activity_edit_text) {
            openCamera();
        }
    }

    private void openCamera() {
        this.finish();
    }

    public void generateAndSavePDF() {
        try {
            String documentsDirectory = Constants.DOCUMENTS_DIRECTORY.getAbsolutePath();
            String fileName = DirectoryHelper.generateFileName("pdf");
            File pdfFile = new File(documentsDirectory, fileName);
            pdfFile.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
            Document document = new Document();
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            document.add(new Paragraph(editTextTextActivityEditText.getText().toString()));
            document.close();

            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setPdfName(fileName);
            documentEntity.setActive(1);
            documentEntity.setPath(pdfFile.getAbsolutePath());
            documentEntity.setCreatedOn(DateTimeHelper.getCurrentDate());
            documentEntity.setModifiedOn(DateTimeHelper.getCurrentDate());
            documentEntity.setParent(DatabaseHelper.getInstance(this).getDocumentsFolderId());

            long id = Database.getInstance(this).documentDao().insertPdf(documentEntity);
            if (id == 0) {
                showSnackBar("PDF not Saved.");
            } else {
                showSnackBar("PDF Saved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTextFile() {
        try {
            String documentsDirectory = Constants.DOCUMENTS_DIRECTORY.getAbsolutePath();
            String fileName = DirectoryHelper.generateFileName("txt");
            File editableFile = new File(documentsDirectory, fileName);
            editableFile.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(editableFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(editTextTextActivityEditText.getText());
            outputStreamWriter.close();
            fileOutputStream.close();

            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setPdfName(fileName);
            documentEntity.setActive(1);
            documentEntity.setPath(editableFile.getAbsolutePath());
            documentEntity.setCreatedOn(DateTimeHelper.getCurrentDate());
            documentEntity.setModifiedOn(DateTimeHelper.getCurrentDate());
            documentEntity.setParent(DatabaseHelper.getInstance(this).getDocumentsFolderId());
            long id = Database.getInstance(this).documentDao().insertPdf(documentEntity);
            if (id == 0) {
                showSnackBar("Text File not Saved.");
            } else {
                showSnackBar("Text File Saved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewMode() {
        Toast.makeText(getApplicationContext(), "View Mode", Toast.LENGTH_LONG).show();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        editTextTextActivityEditText.setClickable(false);
        editTextTextActivityEditText.setFocusable(false);
        editTextTextActivityEditText.setFocusableInTouchMode(false);
        editMode = false;
    }

    private void editMode() {
        Toast.makeText(getApplicationContext(), "Editable Mode", Toast.LENGTH_LONG).show();
        editTextTextActivityEditText.setClickable(true);
        editTextTextActivityEditText.setFocusable(true);
        editTextTextActivityEditText.setFocusableInTouchMode(true);
        editMode = true;
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(imageViewEditActivityEditText, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
