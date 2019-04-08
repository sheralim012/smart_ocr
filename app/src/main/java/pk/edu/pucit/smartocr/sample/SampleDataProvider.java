package pk.edu.pucit.smartocr.sample;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.entity.DocumentEntity;
import pk.edu.pucit.smartocr.database.entity.FolderEntity;
import pk.edu.pucit.smartocr.database.entity.PictureEntity;
import pk.edu.pucit.smartocr.model.DataItem;
import pk.edu.pucit.smartocr.utilities.Constants;

public class SampleDataProvider {
    public static List<DataItem> dataItems;
    public static Map<String, DataItem> dataItemMap;
    public static Context context;

    static {
        dataItems = new ArrayList<>();
        dataItemMap = new HashMap<>();
        int i;
        for (i = 1; i < 50; i++) {
            int p = 0;
            if (i == 1) {
                p = 0;
            } else if (i < 5) {
                p = 1;
            } else if (i >= 5 && i <= 10) {
                p = 2;
            } else if (i > 10 && i <= 20) {
                p = 3;
            } else if (i > 20 && i < 25) {
                p = 0;
            } else if (i >= 25 && i <= 49)
                p = 4;


            //addItem(new DataItem(i, "Folder" + i, 0, "folder", p));
        }
    }

    private static void addItem(DataItem item) {
        dataItems.add(item);
        dataItemMap.put("" + item.getId(), item);
    }

    public static List<DataItem> getData(long id) {
        dataItems.clear();
        ArrayList<FolderEntity> foldersList = (ArrayList<FolderEntity>) Database.getInstance(context).folderDao().getFoldersByParentId(id);
        dataItems = populateDataByFolders(foldersList, dataItems);
        ArrayList<PictureEntity> imagesList = (ArrayList<PictureEntity>) Database.getInstance(context).pictureDao().getImagesByParentId(id);
        dataItems = populateDataByFiles(imagesList, dataItems);
        ArrayList<DocumentEntity> pdfList = (ArrayList<DocumentEntity>) Database.getInstance(context).documentDao().getPdfsByParentId(id);
        dataItems = populateDataByPdfs(pdfList, dataItems);
        return dataItems;
    }

    public static List<DataItem> getDataItemsByParentId(int id) {
        List<DataItem> itemsByParentId = new ArrayList<DataItem>();
        for (int i = 0; i < dataItems.size(); i++) {
            DataItem item = dataItems.get(i);
            if (item.getParent() == id) {
                itemsByParentId.add(item);
            }
        }
        return itemsByParentId;
    }

    public static long getParentId(long id) {
        return Database.getInstance(context).folderDao().getFolderById(id).getParent();
    }

    public static List<Long> getSubFolders(long id) {


        ArrayList<Long> list = new ArrayList<>();
        list.add(id);
        for (int i = 0; i < list.size(); i++) {
            Long id1 = list.get(i);
            List<Long> l2 = getFirstSubFolder(id1);
            if (!(l2.isEmpty()))
                list.addAll(l2);
        }
        return list;
    }

    public static List<Long> getFirstSubFolder(Long id) {
        List<Long> list = new ArrayList<>();
        List<FolderEntity> allFoldersList = Database.getInstance(context).folderDao().getAllFolders();
        List<DataItem> dataItemList = new ArrayList<DataItem>();
        dataItemList = populateDataByFolders(allFoldersList, dataItemList);
        for (DataItem item :
                dataItemList) {
            if (item.getParent() == id) {
                list.add(item.getId());
            }
        }
        return list;
    }

    public static List<DataItem> populateDataByFolders(List<FolderEntity> foldersList, List<DataItem> itemsList) {
        for (FolderEntity folder :
                foldersList) {
            DataItem item = new DataItem();
            item.setId(folder.getId());
            item.setParent(folder.getParent());
            item.setModifiedOn(folder.getModifiedOn());
            item.setCreatedOn(folder.getCreatedOn());
            item.setIsChecked(false);
            item.setItemName(folder.getFolderName());
            item.setType(0);
            item.setPath("");
            itemsList.add(item);
        }
        return itemsList;
    }

    public static List<DataItem> populateDataByFiles(List<PictureEntity> imagesList, List<DataItem> itemsList) {
        for (PictureEntity image :
                imagesList) {
            DataItem item = new DataItem();
            item.setId(image.getId());
            item.setParent(image.getParent());
            item.setModifiedOn(image.getModifiedOn());
            item.setCreatedOn(image.getCreatedOn());
            item.setIsChecked(false);
            item.setItemName(image.getImageName());
            item.setType(1);
            item.setPath(image.getPath());
            itemsList.add(item);
        }
        return itemsList;
    }

    public static List<DataItem> populateDataByPdfs(List<DocumentEntity> imagesList, List<DataItem> itemsList) {
        for (DocumentEntity pdf : imagesList) {
            DataItem item = new DataItem();
            item.setId(pdf.getId());
            item.setParent(pdf.getParent());
            item.setModifiedOn(pdf.getModifiedOn());
            item.setCreatedOn(pdf.getCreatedOn());
            item.setIsChecked(false);
            item.setItemName(pdf.getPdfName());
            item.setType(2);
            item.setPath(pdf.getPath());
            itemsList.add(item);
        }
        return itemsList;
    }

    public static List<FolderEntity> findSubfolders(long id) {
        return Database.getInstance(context).folderDao().getFoldersByParentId(id);
    }

    public static List<PictureEntity> findPicturesByParent(long id) {
        return Database.getInstance(context).pictureDao().getImagesByParentId(id);
    }

    public static List<DocumentEntity> findDocumentsByParent(long id) {
        return Database.getInstance(context).documentDao().getPdfsByParentId(id);
    }

    public static void deleteFolder(FolderEntity folder) {
        ArrayList<FolderEntity> foldersEntityList = new ArrayList<>();
        foldersEntityList.add(folder);
        int length = foldersEntityList.size();
        for (int i = 0; i < length; i++) {
            FolderEntity deleteFolder = foldersEntityList.get(i);
            List<PictureEntity> picturesList = findPicturesByParent(deleteFolder.getId());
            for (PictureEntity deletePicture :
                    picturesList) {
                File picture=new File(deletePicture.getPath());
                picture.delete();
                Database.getInstance(context).pictureDao().delete(deletePicture);
            }
            List<DocumentEntity> documentsList=findDocumentsByParent(deleteFolder.getId());
            for (DocumentEntity deleteDocument :
                    documentsList) {
                File document=new File(deleteDocument.getPath());
                document.delete();
                Database.getInstance(context).documentDao().delete(deleteDocument);
            }
            List<FolderEntity> foldersList=findSubfolders(deleteFolder.getId());
            Database.getInstance(context).folderDao().delete(deleteFolder);
            if(i+1 == length){
                foldersEntityList.addAll(foldersList);
                length=foldersEntityList.size();
            }

        }
    }
    public static void copyFolder(FolderEntity folder,long id) {
        ArrayList<FolderEntity> foldersEntityList = new ArrayList<>();
        foldersEntityList.add(folder);
        long parent=id;
        int length = foldersEntityList.size();
        for (int i = 0; i < length; i++) {
            FolderEntity copyFolder = foldersEntityList.get(i);
            List<FolderEntity> foldersList=findSubfolders(copyFolder.getId());
            copyFolder.setId(0);
            copyFolder.setParent(parent);
            parent=Database.getInstance(context).folderDao().insertFolder(copyFolder);
            List<PictureEntity> picturesList = findPicturesByParent(copyFolder.getId());
            for (PictureEntity copyPicture :
                    picturesList) {
                copyPicture.setId(0);
                copyPicture.setParent(parent);
                String path = copyPicture.getPath();
                String extension = path.substring(path.lastIndexOf("."));
                extension=extension.replace(".","");
                File copyFile = new File(Constants.PICTURES_DIRECTORY, SampleDataProvider.generateFileName(extension));
                File originalFile = new File(copyPicture.getPath());
                copyFile = SampleDataProvider.copy(originalFile, copyFile);
                try {
                    copyFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                copyPicture.setPath(copyFile.getAbsolutePath());
                Database.getInstance(context).pictureDao().insertImage(copyPicture);
            }
            List<DocumentEntity> documentsList=findDocumentsByParent(copyFolder.getId());
            for (DocumentEntity copyDocument :
                    documentsList) {
                copyDocument.setId(0);
                copyDocument.setParent(parent);
                String path = copyDocument.getPath();
                String extension = path.substring(path.lastIndexOf("."));
                extension=extension.replace(".","");
                File copyFile = new File(Constants.DOCUMENTS_DIRECTORY, SampleDataProvider.generateFileName(extension));
                File originalFile = new File(copyDocument.getPath());
                copyFile = SampleDataProvider.copy(originalFile, copyFile);
                try {
                    copyFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                copyDocument.setPath(copyFile.getAbsolutePath());
                Database.getInstance(context).documentDao().insertPdf(copyDocument);
            }
            if(i+1 == length){
                for (FolderEntity subFolder:
                        foldersList) {
                    subFolder.setParent(parent);
                }
                foldersEntityList.addAll(foldersList);
                length=foldersEntityList.size();
            }

        }
    }

    public static File copy(File src,File dst){
        try(InputStream in =new FileInputStream(src)){
            try(OutputStream out=new FileOutputStream(dst)){
                byte[] buf=new byte[1024];
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dst;
    }
    public static String generateFileName(String type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return type.toUpperCase() + "_" + timeStamp + "." + type.toLowerCase();
    }
}
