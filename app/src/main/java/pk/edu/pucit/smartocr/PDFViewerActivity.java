package pk.edu.pucit.smartocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import pk.edu.pucit.smartocr.utilities.Constants;

public class PDFViewerActivity extends AppCompatActivity {

    PDFView pdfViewViewActivityPDFViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        pdfViewViewActivityPDFViewer = findViewById(R.id.pdf_view_view_activity_pdf_viewer);

        Intent intent = getIntent();
        String imagePath = intent.getExtras().getString(Constants.DOCUMENT_PATH);
        if (imagePath != null) {
            pdfViewViewActivityPDFViewer
                    .fromFile(new File(imagePath))
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .load();
        }

    }
}
