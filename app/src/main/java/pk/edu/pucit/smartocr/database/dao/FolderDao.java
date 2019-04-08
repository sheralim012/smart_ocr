package pk.edu.pucit.smartocr.database.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pk.edu.pucit.smartocr.database.entity.FolderEntity;


@Dao
public interface FolderDao
{	
	@Insert
	long insertFolder(FolderEntity folders); // create a folder
	
	@Query("SELECT COUNT(*) FROM folderentity") // count of folders
	int countFolders();
	
	@Query("SELECT * FROM folderentity WHERE active=1 ORDER BY folderName")
	List<FolderEntity> getAllFolders();
	
	@Query("SELECT * FROM folderentity WHERE folderName = :folderName")
	FolderEntity findFolder(String folderName);

	@Query("SELECT * FROM folderentity WHERE active=1 AND parent=:pid  ORDER BY folderName")
	List<FolderEntity> getFoldersByParentId(long pid);

	@Query("Select * from folderentity WHERE id=:id AND active=1")
	FolderEntity getFolderById(long id);

    @Query("Delete from folderentity where id=:delId")
	void deleteById(long delId);
	@Delete
	void delete(FolderEntity... folder);
	@Update
	void update(FolderEntity folder);
}
