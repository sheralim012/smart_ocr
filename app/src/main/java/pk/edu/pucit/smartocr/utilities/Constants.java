package pk.edu.pucit.smartocr.utilities;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final String SHARED_PREFERENCES_NAME = "sharedPreferences";

    public static final String FIRST_TIME_KEY = "firstTime";
    public static final String DEFAULT_SCREEN_KEY = "defaultScreen";
    public static final String DOCUMENTS_FOLDER_ID_KEY = "documentsFolderId";
    public static final String PICTURES_FOLDER_ID_KEY = "picturesFolderId";

    public static final String FIRST_TIME_DEFAULT_VALUE = "Yes";
    public static final String NOT_FIRST_TIME = "No";

    public static final int DOCUMENTS_FOLDER_ID_DEFAULT_VALUE = 1;
    public static final int PICTURES_FOLDER_ID_DEFAULT_VALUE = 2;

    public static final int DEFAULT_SCREEN_CAMERA = 0;
    public static final int DEFAULT_SCREEN_FILE_MANAGER = 1;

    public static final String IMAGE_NAME_KEY = "imageName";
    public static final String IMAGE_PATH_KEY = "imagePath";

    public static final int PERMISSION_ALL_REQUEST_CODE = 0;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 3;

    public static final File SMART_OCR_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "SmartOCR");
    public static final File PICTURES_DIRECTORY = new File(SMART_OCR_DIRECTORY, "Pictures");
    public static final File DOCUMENTS_DIRECTORY = new File(SMART_OCR_DIRECTORY, "Documents");

    public static final String DOCUMENT_PATH = "documentPath";

    public static final String ACTIVITY_NAME = "activityName";
    public static final String FILE_MANAGER_ACTIVITY = "FileManagerActivity";

}
