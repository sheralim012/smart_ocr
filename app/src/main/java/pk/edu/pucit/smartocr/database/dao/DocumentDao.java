package pk.edu.pucit.smartocr.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pk.edu.pucit.smartocr.database.entity.DocumentEntity;

@Dao
public interface DocumentDao {
    @Insert
    long insertPdf(DocumentEntity pdfs); // comma separated insertion

    @Insert
    void insertPdf(List<DocumentEntity> pdfs); // insertion via LIST

    @Query("SELECT COUNT(*) FROM DocumentEntity")
        // count of pdfs
    int countPdfs();

    @Query("SELECT * FROM DocumentEntity where active=1 ORDER BY pdfName")
    List<DocumentEntity> getAllPdfs();

    @Query("SELECT * FROM DocumentEntity where active=1 AND parent=:id ORDER BY pdfName")
    List<DocumentEntity> getPdfsByParentId(long id);
    @Query("SELECT * FROM DocumentEntity WHERE active=1 AND pdfName = :pdfName")
    DocumentEntity findPdf(String pdfName);

    @Query("Delete from DocumentEntity where id=:delId")
    void deleteById(long delId);

    @Delete
    void delete(DocumentEntity pdf);

    @Update
    void update(DocumentEntity pdf);
}
