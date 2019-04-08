package pk.edu.pucit.smartocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import pk.edu.pucit.smartocr.utilities.Constants;

public class TextViewerActivity extends AppCompatActivity {

    TextView textViewtextActivityTextViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_viewer);
        textViewtextActivityTextViewer = findViewById(R.id.text_view_text_activity_text_viewer);
        Intent intent = getIntent();
        String imagePath = intent.getExtras().getString(Constants.DOCUMENT_PATH);
        File file = null;
        if (imagePath != null) {
            file = new File(imagePath);
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textViewtextActivityTextViewer.setText(text.toString());
        }
    }
}
