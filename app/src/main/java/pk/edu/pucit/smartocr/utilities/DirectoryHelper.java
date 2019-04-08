package pk.edu.pucit.smartocr.utilities;

import java.io.File;

public class DirectoryHelper {

    public static void initializeDirectoryStructure() {
        File rootDirectory = Constants.SMART_OCR_DIRECTORY;
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
        File documentsDirectory = Constants.DOCUMENTS_DIRECTORY;
        if (!documentsDirectory.exists()) {
            documentsDirectory.mkdirs();
        }
        File picturesDirectory = Constants.PICTURES_DIRECTORY;
        if (!picturesDirectory.exists()) {
            picturesDirectory.mkdirs();
        }
    }

    public static String generateFileName(String extension) {
        return extension.toUpperCase() + "_" + DateTimeHelper.getCurrentDateTime() + "." + extension.toLowerCase();
    }

}
