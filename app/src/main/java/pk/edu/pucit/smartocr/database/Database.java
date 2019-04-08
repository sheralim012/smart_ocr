package pk.edu.pucit.smartocr.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import pk.edu.pucit.smartocr.database.dao.DocumentDao;
import pk.edu.pucit.smartocr.database.dao.FolderDao;
import pk.edu.pucit.smartocr.database.dao.PictureDao;
import pk.edu.pucit.smartocr.database.entity.DocumentEntity;
import pk.edu.pucit.smartocr.database.entity.FolderEntity;
import pk.edu.pucit.smartocr.database.entity.PictureEntity;

@android.arch.persistence.room.Database(entities={FolderEntity.class, DocumentEntity.class, PictureEntity.class},version=1)
public abstract class Database extends RoomDatabase
{
    private static Database instance;
	public abstract FolderDao folderDao();
	public abstract PictureDao pictureDao();
	public abstract DocumentDao documentDao();
	
    public static Database getInstance(Context context)
    {
        if(instance==null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(),Database.class,"database").allowMainThreadQueries().build();
        }
        return instance;
    }
    public static void onDestroy()
    {
        instance=null;
    }
}
