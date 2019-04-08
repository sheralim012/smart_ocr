package pk.edu.pucit.smartocr.utilities;

import android.content.Context;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.dao.FolderDao;
import pk.edu.pucit.smartocr.database.entity.FolderEntity;

public class DatabaseHelper {

    private long documentsFolderId = 1;
    private long picturesFolderId = 2;

    private static DatabaseHelper INSTANCE = null;

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHelper();
        }
        INSTANCE.documentsFolderId = SharedPreferencesHelper.getLong(context, Constants.DOCUMENTS_FOLDER_ID_KEY, Constants.DOCUMENTS_FOLDER_ID_DEFAULT_VALUE);
        INSTANCE.picturesFolderId = SharedPreferencesHelper.getLong(context, Constants.PICTURES_FOLDER_ID_KEY, Constants.PICTURES_FOLDER_ID_DEFAULT_VALUE);
        return INSTANCE;
    }

    public long getPicturesFolderId() {
        return picturesFolderId;
    }

    public void setPicturesFolderId(long picturesFolderId) {
        this.picturesFolderId = picturesFolderId;
    }

    public long getDocumentsFolderId() {
        return documentsFolderId;
    }

    public void setDocumentsFolderId(long documentsFolderId) {
        this.documentsFolderId = documentsFolderId;
    }

    public void initializeDatabase(Context context) {
        String currentDate = DateTimeHelper.getCurrentDate();

        FolderEntity documentsFolderEntity = new FolderEntity();
        documentsFolderEntity.setFolderName("Documents");
        documentsFolderEntity.setActive(1);
        documentsFolderEntity.setParent(0);
        documentsFolderEntity.setCreatedOn(currentDate);
        documentsFolderEntity.setModifiedOn(currentDate);

        FolderEntity picturesFolderEntity = new FolderEntity();
        picturesFolderEntity.setFolderName("Pictures");
        picturesFolderEntity.setActive(1);
        picturesFolderEntity.setParent(0);
        picturesFolderEntity.setCreatedOn(currentDate);
        picturesFolderEntity.setModifiedOn(currentDate);

        FolderDao folderDao = Database.getInstance(context.getApplicationContext()).folderDao();
        documentsFolderId = folderDao.insertFolder(documentsFolderEntity);
        picturesFolderId = folderDao.insertFolder(picturesFolderEntity);

        SharedPreferencesHelper.putLong(context, Constants.DOCUMENTS_FOLDER_ID_KEY, documentsFolderId);
        SharedPreferencesHelper.putLong(context, Constants.PICTURES_FOLDER_ID_KEY, picturesFolderId);
    }

}
