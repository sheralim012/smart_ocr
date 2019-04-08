package pk.edu.pucit.smartocr;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.entity.DocumentEntity;
import pk.edu.pucit.smartocr.database.entity.FolderEntity;
import pk.edu.pucit.smartocr.database.entity.PictureEntity;
import pk.edu.pucit.smartocr.model.DataItem;
import pk.edu.pucit.smartocr.sample.SampleDataProvider;
import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.DateTimeHelper;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ViewHolder> {

    private ImageView imageViewSearchActivityFileManager, imageViewMenuActivityFileManager,
            imageViewRenameActivityFileManager, imageViewShareActivityFileManager,
            imageViewDeleteActivityFileManager, imageViewMoveActivityFileManager,
            imageViewCopyActivityFileManager, imageViewRenameBlurredActivityFileManager,
            imageViewCopyPasteActivityFileManager, imageViewCutPasteActivityFileManager,
            imageViewShareBlurredActivityFileManager, imageViewCancelActivityFileManager;
    private List<DataItem> dataItemsList;
    private List<DataItem> selectedItemsList;
    private Context context;
    private View toolbarBottom;
    private boolean longPressed;
    private long currentFolder;
    private int checkedNumber;
    private boolean search;
    private int folderSelected;
    private boolean moveItem;

    public DataItemAdapter(Context context, List<DataItem> items, View v) {
        this.context = context;
        this.dataItemsList = items;
        this.toolbarBottom = v;
        longPressed = false;
        currentFolder = 0;
        checkedNumber = 0;
        folderSelected = 0;
        moveItem = false;
        selectedItemsList = new ArrayList<DataItem>();
    }

    public boolean isMoveItem() {
        return moveItem;
    }

    public void setMoveItem(boolean moveItem) {
        this.moveItem = moveItem;
    }

    public int getFolderSelected() {
        return folderSelected;
    }

    public void setFolderSelected(int folderSelected) {
        this.folderSelected = folderSelected;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public void clearSelectedList() {
        selectedItemsList.clear();
        folderSelected = 0;
        checkedNumber = 0;
    }

    public int getCheckedNumber() {
        return checkedNumber;
    }

    public void setCheckedNumber(int checkedNumber) {
        this.checkedNumber = checkedNumber;
    }

    public long getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(long currentFolder) {
        this.currentFolder = currentFolder;
        dataItemsList.clear();
        dataItemsList = SampleDataProvider.getData(currentFolder);
        notifyDataSetChanged();
    }

    public boolean isLongPressed() {
        return longPressed;
    }

    public void setLongPressed(boolean longPressed) {
        this.longPressed = longPressed;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DataItem item = dataItemsList.get(position);
        holder.checkBox.setVisibility(View.GONE);
        holder.tvName.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.GONE);
        if (item.getParent() == currentFolder || search) {
            try {
                if (item.getParent() != 0) {
                    holder.checkBox.setVisibility(View.VISIBLE);
                }
                holder.tvName.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(false);
                holder.tvName.setText(item.getItemName());

                if (longPressed && item.getParent() != 0) {
                    holder.checkBox.setVisibility(View.VISIBLE);
                } else {
                    holder.checkBox.setVisibility(View.GONE);
                    item.setIsChecked(false);
                }
                if (item.getIsChecked())
                    holder.checkBox.setChecked(true);
                if (item.getType() == 0) {
                    holder.imageView.setImageResource(R.drawable.ic_folder_activity_file_manager);
                } else if (item.getType() == 1) {
                    holder.imageView.setImageResource(R.drawable.ic_image);
                } else if (item.getType() == 2) {
                    String path1 = item.getPath();
                    String extension1 = path1.substring(path1.lastIndexOf("."));
                    if(extension1.equals(".pdf")) {
                        holder.imageView.setImageResource(R.drawable.ic_pdf_activiy_file_manager);
                    }else if(extension1.equals(".txt")){
                        holder.imageView.setImageResource(R.drawable.ic_text);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Single Click on a folder or file
            holder.listItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSingleClick(holder, item);
                }
            });

            //Long press on a folder or file
            holder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (item.getParent() != 0) {
                        clearSelectedList();
                        final android.support.v7.widget.Toolbar toolbar = toolbarBottom.findViewById(R.id.toolbar_bottom_activity_file_manager);
                        toolbar.setVisibility(View.VISIBLE);

                        imageViewRenameActivityFileManager = toolbar.findViewById(R.id.image_view_rename_activity_file_manager);
                        imageViewDeleteActivityFileManager = toolbar.findViewById(R.id.image_view_delete_activity_file_manager);
                        imageViewMoveActivityFileManager = toolbar.findViewById(R.id.image_view_move_activity_file_manager);
                        imageViewCopyActivityFileManager = toolbar.findViewById(R.id.image_view_copy_activity_file_manager);
                        imageViewShareActivityFileManager = toolbar.findViewById(R.id.image_view_share_activity_file_manager);
                        imageViewShareBlurredActivityFileManager = toolbar.findViewById(R.id.image_view_share_blurrd_activity_file_manager);
                        imageViewCutPasteActivityFileManager = toolbar.findViewById(R.id.image_view_cut_paste_activity_file_manager);
                        imageViewRenameBlurredActivityFileManager = toolbar.findViewById(R.id.image_view_rename_blurred_activity_file_manager);
                        imageViewCopyPasteActivityFileManager = toolbar.findViewById(R.id.image_view_copy_paste_activity_file_manager);
                        imageViewCancelActivityFileManager = toolbar.findViewById(R.id.image_view_cancel_activity_file_manager);
                        imageViewCancelActivityFileManager.setVisibility(View.GONE);
                        imageViewRenameBlurredActivityFileManager.setVisibility(View.GONE);
                        imageViewShareBlurredActivityFileManager.setVisibility(View.GONE);
                        imageViewShareActivityFileManager.setVisibility(View.VISIBLE);
                        imageViewRenameActivityFileManager.setVisibility(View.VISIBLE);
                        imageViewCutPasteActivityFileManager.setVisibility(View.GONE);
                        imageViewMoveActivityFileManager.setVisibility(View.VISIBLE);
                        imageViewCopyActivityFileManager.setVisibility(View.VISIBLE);
                        imageViewCopyPasteActivityFileManager.setVisibility(View.GONE);


                        imageViewShareBlurredActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar snackbar = Snackbar
                                        .make(v, "Can not share folders", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                // Toast.makeText(context,"Can not share Folders",Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (item.getType() == 0) {
                            imageViewShareBlurredActivityFileManager.setVisibility(View.VISIBLE);
                            imageViewShareActivityFileManager.setVisibility(View.GONE);
                            folderSelected++;
                        }
                        if (folderSelected == 0) {
                            imageViewShareBlurredActivityFileManager.setVisibility(View.GONE);
                            imageViewShareActivityFileManager.setVisibility(View.VISIBLE);
                        }
                        imageViewRenameActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (longPressed && item.getIsChecked()) {
                                    final EditText editText = new EditText(context);
                                    editText.setText(item.getItemName());
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Rename ");
                                    switch (item.getType()) {
                                        case 0:
                                            builder.setMessage("Enter Folder Name");
                                            break;
                                        case 1:
                                            builder.setMessage("Enter Picture Name");
                                            break;
                                        case 2:
                                            builder.setMessage("Enter Document Name");
                                            break;
                                    }
                                    builder.setCancelable(false);
                                    builder.setView(editText);
                                    builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!(editText.getText().toString().equals(""))) {
                                                String renameString = editText.getText().toString();

                                                switch (item.getType()) {
                                                    case 0:
                                                        FolderEntity renameFolder = new FolderEntity();
                                                        renameFolder.setId(item.getId());
                                                        renameFolder.setFolderName(renameString);
                                                        renameFolder.setParent(item.getParent());
                                                        renameFolder.setCreatedOn(item.getCreatedOn());
                                                        renameFolder.setModifiedOn(DateTimeHelper.getCurrentDate());
                                                        renameFolder.setActive(1);
                                                        Database.getInstance(context).folderDao().update(renameFolder);
                                                        Snackbar snackbar = Snackbar
                                                                .make(holder.imageView, "Folder Renamed as " + renameString, Snackbar.LENGTH_LONG);
                                                        snackbar.show();
                                                        refresh();
                                                        break;
                                                    case 1:
                                                        PictureEntity renameImage = new PictureEntity();
                                                        renameImage = fillImage(item);
                                                        renameImage.setModifiedOn(DateTimeHelper.getCurrentDate());
                                                        renameImage.setImageName(renameString);
                                                        renameImage.setId(item.getId());
                                                        Database.getInstance(context).pictureDao().update(renameImage);
                                                        Snackbar snackbar1 = Snackbar
                                                                .make(holder.imageView, "Picture Renamed as " + renameString, Snackbar.LENGTH_LONG);
                                                        snackbar1.show();
                                                        refresh();
                                                        break;
                                                    case 2:
                                                        DocumentEntity renameDocument = new DocumentEntity();
                                                        renameDocument = fillPdf(item);
                                                        renameDocument.setModifiedOn(DateTimeHelper.getCurrentDate());
                                                        renameDocument.setId(item.getId());
                                                        renameDocument.setPdfName(renameString);
                                                        Database.getInstance(context).documentDao().update(renameDocument);
                                                        Snackbar snackbar2 = Snackbar
                                                                .make(holder.imageView, "Document Renamed as " + renameString, Snackbar.LENGTH_LONG);
                                                        snackbar2.show();
                                                        refresh();
                                                        break;
                                                }
                                            } else {
                                                Snackbar snackbar = Snackbar
                                                        .make(holder.imageView, "Please Enter Folder Name", Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                            }
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    builder.show();
                                    longPressed = false;
                                    imageViewRenameActivityFileManager.setVisibility(View.VISIBLE);
                                    toolbar.setVisibility(View.GONE);
                                    clearSelectedList();
                                    notifyDataSetChanged();
                                    //Toast.makeText(context.getApplicationContext(), "Rename file/folder", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        imageViewDeleteActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Alert!!!");
                                builder.setMessage("Are you sure to delete ?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (DataItem deleteItem : selectedItemsList) {
                                            if (deleteItem.getType() == 0) {
                                                SampleDataProvider.deleteFolder(fillFolder(deleteItem));
                                                //Database.getInstance(context).folderDao().deleteById(deleteItem.getId());
                                            } else if (deleteItem.getType() == 1) {
                                                File picture = new File(deleteItem.getPath());
                                                picture.delete();
                                                Database.getInstance(context).pictureDao().deleteById(deleteItem.getId());
                                            } else if (deleteItem.getType() == 2) {
                                                File document = new File(deleteItem.getPath());
                                                document.delete();
                                                Database.getInstance(context).documentDao().deleteById(deleteItem.getId());
                                                //dataItemsList.remove(deleteItem);
                                            }
                                        }
                                        checkedNumber = 0;
                                        clearSelectedList();
                                        longPressed = false;
                                        imageViewRenameActivityFileManager.setVisibility(View.VISIBLE);
                                        toolbar.setVisibility(View.GONE);
                                        setCurrentFolder(currentFolder);
                                        notifyDataSetChanged();
                                        Snackbar snackbar = Snackbar
                                                .make(v, "Item/s deleted", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        //Toast.makeText(context.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkedNumber = 0;
                                        clearSelectedList();
                                        longPressed = false;
                                        imageViewRenameActivityFileManager.setVisibility(View.VISIBLE);
                                        toolbar.setVisibility(View.GONE);
                                        setCurrentFolder(currentFolder);
                                        notifyDataSetChanged();
                                        //Toast.makeText(context.getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.show();
                            }
                        });
                        imageViewMoveActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                longPressed = false;
                                imageViewCutPasteActivityFileManager.setVisibility(View.VISIBLE);
                                imageViewMoveActivityFileManager.setVisibility(View.GONE);
                                moveItem = true;
                                imageViewCancelActivityFileManager.setVisibility(View.VISIBLE);
                                imageViewCancelActivityFileManager.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        moveItem = false;
                                        imageViewCopyPasteActivityFileManager.setVisibility(View.GONE);
                                        imageViewCopyActivityFileManager.setVisibility(View.VISIBLE);
                                        imageViewCutPasteActivityFileManager.setVisibility(View.GONE);
                                        imageViewMoveActivityFileManager.setVisibility(View.VISIBLE);
                                        imageViewCancelActivityFileManager.setVisibility(View.GONE);
                                        toolbarBottom.setVisibility(View.GONE);
                                        longPressed = false;
                                        clearSelectedList();
                                        folderSelected = 0;
                                        notifyDataSetChanged();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        });
                        imageViewCopyActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                longPressed = false;
                                moveItem = true;
                                imageViewCopyPasteActivityFileManager.setVisibility(View.VISIBLE);
                                imageViewCopyActivityFileManager.setVisibility(View.GONE);
                                imageViewCancelActivityFileManager.setVisibility(View.VISIBLE);
                                imageViewCancelActivityFileManager.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        moveItem = false;
                                        imageViewCopyPasteActivityFileManager.setVisibility(View.GONE);
                                        imageViewCopyActivityFileManager.setVisibility(View.VISIBLE);
                                        imageViewCutPasteActivityFileManager.setVisibility(View.GONE);
                                        imageViewMoveActivityFileManager.setVisibility(View.VISIBLE);
                                        imageViewCancelActivityFileManager.setVisibility(View.GONE);
                                        toolbarBottom.setVisibility(View.GONE);
                                        longPressed = false;
                                        clearSelectedList();
                                        folderSelected = 0;
                                        notifyDataSetChanged();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        });
                        imageViewCutPasteActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentFolder != 0) {
                                    boolean error = false;
                                    for (DataItem pasteItem : selectedItemsList) {
                                        if (currentFolder == pasteItem.getId() && pasteItem.getType() == 0) {
                                            error = true;
                                            Snackbar snackbar = Snackbar
                                                    .make(v, "Unable to paste a folder to itself or its subfolder", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            //Toast.makeText(context.getApplicationContext(), "Unable to paste a folder to itself or its subfolder", Toast.LENGTH_LONG).show();
                                            //break;
                                        } else {
                                            List<Long> list = SampleDataProvider.getSubFolders(pasteItem.getId());
                                            for (Long item :
                                                    list) {
                                                if (item == currentFolder && pasteItem.getType() != 1 && pasteItem.getType() != 2) {
                                                    error = true;
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Unable to paste a folder to itself or its subfolder", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                    //Toast.makeText(context.getApplicationContext(), "Unable to paste a folder to itself or its subfolder", Toast.LENGTH_LONG).show();
                                                    break;
                                                }
                                            }
                                        }
                                        if (!error) {
                                            for (DataItem cutItem :
                                                    selectedItemsList) {
                                                if (cutItem.getType() == 0) {
                                                    FolderEntity cutFolder = fillFolder(cutItem);
                                                    Database.getInstance(context).folderDao().update(cutFolder);
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Folder pasted", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                } else if (cutItem.getType() == 1) {
                                                    PictureEntity cutFile = fillImage(cutItem);
                                                    Database.getInstance(context).pictureDao().update(cutFile);
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Picture pasted", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                } else if (cutItem.getType() == 2) {
                                                    DocumentEntity cutPdf = fillPdf(cutItem);
                                                    Database.getInstance(context).documentDao().update(cutPdf);
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Document pasted", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                }
                                            }
                                            //Toast.makeText(context.getApplicationContext(), "Folder pasted", Toast.LENGTH_LONG).show();
                                            //Query to cut folder/file
                                        }

                                    }
                                } else {
                                    Snackbar snackbarDocuments = Snackbar
                                            .make(v, "Can not paste in root folder", Snackbar.LENGTH_LONG);
                                    snackbarDocuments.show();
                                }
                                imageViewCutPasteActivityFileManager.setVisibility(View.GONE);
                                imageViewMoveActivityFileManager.setVisibility(View.VISIBLE);
                                toolbar.setVisibility(View.GONE);
                                clearSelectedList();
                                setCurrentFolder(currentFolder);
                            }
                        });
                        imageViewCopyPasteActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentFolder != 0) {
                                    boolean error = false;
                                    for (DataItem pasteItem : selectedItemsList) {
                                        if (currentFolder == pasteItem.getId() && pasteItem.getType() == 0) {
                                            error = true;
                                            Snackbar snackbar = Snackbar
                                                    .make(v, "Unable to paste a folder to itself or its subfolder", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            //Toast.makeText(context.getApplicationContext(), "Unable to paste a folder to itself or its subfolder", Toast.LENGTH_LONG).show();
                                            break;
                                        } else {
                                            List<Long> list = SampleDataProvider.getSubFolders(pasteItem.getId());
                                            for (Long item :
                                                    list) {
                                                if (item == currentFolder && pasteItem.getType() == 0) {
                                                    error = true;
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Unable to paste a folder to itself or its subfolder", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                    //Toast.makeText(context.getApplicationContext(), "Unable to paste a folder to itself or its subfolder", Toast.LENGTH_LONG).show();
                                                    break;
                                                }
                                            }
                                        }
                                        if (!error) {
                                            switch (pasteItem.getType()) {
                                                case 0:
                                                    SampleDataProvider.copyFolder(fillFolder(pasteItem), currentFolder);
                                                    Snackbar snackbar = Snackbar
                                                            .make(v, "Folders pasted", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                    break;
                                                case 1:
                                                    String path = pasteItem.getPath();
                                                    String extension = path.substring(path.lastIndexOf("."));
                                                    extension=extension.replace(".","");
                                                    File copyFile = new File(Constants.PICTURES_DIRECTORY, SampleDataProvider.generateFileName(extension));
                                                    File orginalFile = new File(pasteItem.getPath());
                                                    copyFile = SampleDataProvider.copy(orginalFile, copyFile);
                                                    try {
                                                        copyFile.createNewFile();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    PictureEntity picturesEntity = fillImage(pasteItem);
                                                    picturesEntity.setId(0);
                                                    picturesEntity.setParent(currentFolder);
                                                    picturesEntity.setPath(copyFile.getAbsolutePath());
                                                    picturesEntity.setModifiedOn(DateTimeHelper.getCurrentDate());
                                                    Database.getInstance(context).pictureDao().insertImage(picturesEntity);
                                                    Snackbar snackbarPictures = Snackbar
                                                            .make(v, "Pictures pasted", Snackbar.LENGTH_LONG);
                                                    snackbarPictures.show();
                                                    break;
                                                case 2:
                                                    String path1 = pasteItem.getPath();
                                                    String extension1 = path1.substring(path1.lastIndexOf("."));
                                                    extension1=extension1.replace(".","");
                                                    File copyFileD = new File(Constants.DOCUMENTS_DIRECTORY, SampleDataProvider.generateFileName(extension1));
                                                    File orginalFileD = new File(pasteItem.getPath());
                                                    copyFileD = SampleDataProvider.copy(orginalFileD, copyFileD);
                                                    try {
                                                        copyFileD.createNewFile();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    DocumentEntity documentEntity = fillPdf(pasteItem);
                                                    documentEntity.setId(0);
                                                    documentEntity.setParent(currentFolder);
                                                    documentEntity.setPath(copyFileD.getAbsolutePath());
                                                    documentEntity.setModifiedOn(DateTimeHelper.getCurrentDate());
                                                    Database.getInstance(context).documentDao().insertPdf(documentEntity);
                                                    Snackbar snackbarDocuments = Snackbar
                                                            .make(v, "Documents pasted", Snackbar.LENGTH_LONG);
                                                    snackbarDocuments.show();
                                                    break;
                                            }
                                        }

                                    }
                                } else {
                                    Snackbar snackbarDocuments = Snackbar
                                            .make(v, "Can not paste in root folder", Snackbar.LENGTH_LONG);
                                    snackbarDocuments.show();
                                }
                                imageViewCopyPasteActivityFileManager.setVisibility(View.GONE);
                                imageViewCopyActivityFileManager.setVisibility(View.VISIBLE);
                                imageViewCancelActivityFileManager.setVisibility(View.GONE);
                                toolbar.setVisibility(View.GONE);
                                clearSelectedList();
                                setCurrentFolder(currentFolder);
                            }
                        });

                        imageViewShareActivityFileManager.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!(selectedItemsList.isEmpty())) {
                                    ArrayList<Uri> uriList = new ArrayList<>();
                                    for (DataItem shareItem : selectedItemsList) {
                                        File file = new File(shareItem.getPath());
                                        Uri uri = FileProvider.getUriForFile((Activity) context, "pk.edu.pucit.smartocr.fileprovider", file);
                                        uriList.add(uri);
                                    }
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                    intent.setType("*/*");
                                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)));
                                    longPressed = false;
                                    clearSelectedList();
                                    notifyDataSetChanged();
                                } else {
                                    Snackbar snackbarDocuments = Snackbar
                                            .make(v, "Please select something to share", Snackbar.LENGTH_LONG);
                                    snackbarDocuments.show();
                                }
                            }
                        });

                        CheckBox checkBox = holder.checkBox;
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                        longPressed = true;
                        checkedNumber++;
                        item.setIsChecked(true);
                        selectedItemsList.add(item);
                        notifyDataSetChanged();
                    }
                    return true;

                }
            });

        }


        //CheckBox Listener
        holder.checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked())
                    holder.checkBox.setChecked(false);
                else
                    holder.checkBox.setChecked(true);
                onSingleClick(holder, item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataItemsList.size();
    }

    public void refresh() {
        setCurrentFolder(currentFolder);
        notifyDataSetChanged();
    }

    public void onSingleClick(ViewHolder holder, DataItem item) {
        if (longPressed && item.getParent() != 0) {
            if (holder.checkBox.isChecked()) {
                holder.checkBox.setChecked(false);
                checkedNumber--;

                item.setIsChecked(false);
                selectedItemsList.remove(item);
            } else {
                holder.checkBox.setChecked(true);
                checkedNumber++;
                if (item.getType() == 0) {
                    imageViewShareBlurredActivityFileManager.setVisibility(View.VISIBLE);
                    imageViewShareActivityFileManager.setVisibility(View.GONE);
                    folderSelected++;
                }
                item.setIsChecked(true);
                selectedItemsList.add(item);

            }
            if (folderSelected == 0) {
                imageViewShareBlurredActivityFileManager.setVisibility(View.GONE);
                imageViewShareActivityFileManager.setVisibility(View.VISIBLE);
            }
            if (checkedNumber <= 1) {
                //imageViewRenameActivityFileManager.setImageDrawable(context.getDrawable(R.drawable.ic_edit));
                imageViewRenameActivityFileManager.setVisibility(View.VISIBLE);
                imageViewRenameBlurredActivityFileManager.setVisibility(View.GONE);
            } else {
                //imageViewRenameActivityFileManager.setImageDrawable(context.getDrawable(R.drawable.ic_edit_blurred));
                imageViewRenameActivityFileManager.setVisibility(View.GONE);
                imageViewRenameBlurredActivityFileManager.setVisibility(View.VISIBLE);
                imageViewRenameBlurredActivityFileManager.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar = Snackbar
                                .make(v, "Can not rename multiple Folders/Files", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(context,"Can not rename multiple Folders/Files",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (checkedNumber == 0) {
                folderSelected = 0;
                toolbarBottom.setVisibility(View.GONE);
                longPressed = false;
                notifyDataSetChanged();
            }
        } else {

            switch (item.getType()) {
                case 0:
//                    clearSelectedList();
                    currentFolder = item.getId();
                    dataItemsList = SampleDataProvider.getData(currentFolder);
                    notifyDataSetChanged();
                    break;
                case 1:
                    Intent intent = new Intent(context, EditImageActivity.class);
                    intent.putExtra(Constants.ACTIVITY_NAME, Constants.FILE_MANAGER_ACTIVITY);
                    intent.putExtra(Constants.IMAGE_PATH_KEY, item.getPath());
                    context.startActivity(intent);
                    break;
                case 2:
                    String path = item.getPath();
                    String extension = path.substring(path.lastIndexOf("."));
                    Intent openDocument;
                    if (extension.equals(".pdf")) {
                        openDocument = new Intent(context, PDFViewerActivity.class);
                    } else {
                        openDocument = new Intent(context, TextViewerActivity.class);
                    }
                    openDocument.putExtra(Constants.DOCUMENT_PATH, item.getPath());
                    context.startActivity(openDocument);

//                    File shareDocument = new File(item.getPath());
//                    Intent documentIntent = new Intent();
//                    documentIntent.setAction(Intent.ACTION_VIEW);
//                    documentIntent.setData(Uri.fromFile(shareDocument));
//                    documentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    documentIntent.setType("*/*");
//                    documentIntent.setDataAndType(Uri.fromFile(shareDocument), "application/pdf");
//                    context.startActivity(Intent.createChooser(documentIntent, "Open With"));
                    break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public CheckBox checkBox;
        public View listItem;

        public ViewHolder(View itemView) {
            super(itemView);
            listItem = itemView;
            tvName = (TextView) itemView.findViewById(R.id.text_view_folder_name_list_item);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_folder_list_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box_select_list_item);
        }
    }

    public FolderEntity fillFolder(DataItem item) {
        FolderEntity cutFolder = new FolderEntity();
        cutFolder.setId(item.getId());
        cutFolder.setFolderName(item.getItemName());
        cutFolder.setActive(1);
        cutFolder.setParent(currentFolder);
        cutFolder.setCreatedOn(item.getCreatedOn());
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String modifiedOn = format.format(date);
        cutFolder.setModifiedOn(modifiedOn);
        return cutFolder;
    }

    public PictureEntity fillImage(DataItem item) {
        PictureEntity cutFile = new PictureEntity();
        cutFile.setId(item.getId());
        cutFile.setImageName(item.getItemName());
        cutFile.setActive(1);
        cutFile.setParent(currentFolder);
        cutFile.setCreatedOn(item.getCreatedOn());
        cutFile.setPath(item.getPath());
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String modifiedOn = format.format(date);
        cutFile.setModifiedOn(modifiedOn);
        return cutFile;
    }

    public DocumentEntity fillPdf(DataItem item) {
        DocumentEntity pdf = new DocumentEntity();
        pdf.setId(item.getId());
        pdf.setPdfName(item.getItemName());
        pdf.setActive(1);
        pdf.setParent(currentFolder);
        pdf.setPath(item.getPath());
        pdf.setCreatedOn(item.getCreatedOn());
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String modifiedOn = format.format(date);
        pdf.setModifiedOn(modifiedOn);
        return pdf;
    }

    public void updateData(List<DataItem> list) {
        dataItemsList.clear();
        clearSelectedList();
        dataItemsList.addAll(list);
        notifyDataSetChanged();
    }

    public void hideManagerBar() {
        imageViewCopyPasteActivityFileManager.setVisibility(View.GONE);
        imageViewCopyActivityFileManager.setVisibility(View.VISIBLE);
        imageViewCutPasteActivityFileManager.setVisibility(View.GONE);
        imageViewMoveActivityFileManager.setVisibility(View.VISIBLE);
        imageViewCancelActivityFileManager.setVisibility(View.GONE);
    }
}
