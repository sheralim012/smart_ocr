package pk.edu.pucit.smartocr.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pk.edu.pucit.smartocr.database.entity.PictureEntity;


@Dao
public interface PictureDao {
    @Insert
    void insertImage(PictureEntity images); // comma separated insertion

    @Insert
    void insertImage(List<PictureEntity> images); // insertion via LIST

    @Query("SELECT COUNT(*) FROM PictureEntity")
        // count of images
    int countImages();

    @Query("SELECT * FROM PictureEntity WHERE active=1 ORDER BY imageName")
    List<PictureEntity> getAllImages();

    @Query("SELECT * FROM PictureEntity WHERE active=1 AND parent=:pid  ORDER BY imageName")
    List<PictureEntity> getImagesByParentId(long pid);

    @Query("SELECT * FROM PictureEntity WHERE imageName = :imageName")
    PictureEntity findImage(String imageName);

    @Query("Delete from PictureEntity where id=:delId")
    void deleteById(long delId);

    @Delete
    void delete(PictureEntity image);

    @Update
    void update(PictureEntity image);


}
