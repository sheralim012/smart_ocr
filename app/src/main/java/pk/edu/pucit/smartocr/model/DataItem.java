package pk.edu.pucit.smartocr.model;

import java.io.Serializable;

public class DataItem implements Serializable {
    private boolean isChecked;
    private long id;
    private String itemName;
    private int type;       //0 for folder 1 for image 2 for pdf
    private String pic;
    private long parent;
    private String createdOn;
    private String modifiedOn;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public long getParent()
    {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public void setIsChecked(boolean flag)
    {
        isChecked=flag;
    }
    public boolean getIsChecked()
    {
        return isChecked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public long getId() {

        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getType() {
        return type;
    }

    public String getPic() {
        return pic;
    }

    public DataItem(int id, String itemName, int type, String pic , int parent) {

        this.id = id;
        this.itemName = itemName;
        this.type = type;
        this.pic = pic;
        isChecked=false;
        this.parent=parent;
    }

    public DataItem() {

    }
}
