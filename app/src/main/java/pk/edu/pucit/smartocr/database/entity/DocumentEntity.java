package pk.edu.pucit.smartocr.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class DocumentEntity
{
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
	    //@ForeignKey()
    /*ForeignKeys = arrayOf(ForeignKey(entity = FolderEntity::class,
                          parentColumns = arrayOf("folderId"),
				childColumns = arrayOf("foreignKeyPdfEntity")));*/
    @ColumnInfo
    private String pdfName;

    @ColumnInfo
    private String path;
    @ColumnInfo
    private String createdOn;
    @ColumnInfo
    private String modifiedOn;
    @ColumnInfo
    private int active;

    @ColumnInfo
    private long parent;

    public DocumentEntity()
    {

    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
