package pk.edu.pucit.smartocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pk.edu.pucit.smartocr.database.Database;
import pk.edu.pucit.smartocr.database.entity.DocumentEntity;
import pk.edu.pucit.smartocr.database.entity.FolderEntity;
import pk.edu.pucit.smartocr.database.entity.PictureEntity;
import pk.edu.pucit.smartocr.model.DataItem;
import pk.edu.pucit.smartocr.sample.SampleDataProvider;
import pk.edu.pucit.smartocr.utilities.DatabaseHelper;
import pk.edu.pucit.smartocr.utilities.DateTimeHelper;
import pk.edu.pucit.smartocr.utilities.SharedPreferencesHelper;

public class FileManagerActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private List<DataItem> dataItemsList;

    public DataItemAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbarBottomActivityFileManager;
    private ImageView imageViewSearchActivityFileManager, imageViewMenuActivityFileManager,
            imageViewRenameActivityFileManager, imageViewShareActivityFileManager,
            imageViewDeleteActivityFileManager, imageViewMoveActivityFileManager,
            imageViewCopyActivityFileManager, imageViewBackActivityFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        setTitle("File Manager");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SampleDataProvider.context = getApplicationContext();
        if (SharedPreferencesHelper.checkForFirstTime(this)) {
            DatabaseHelper.getInstance(this).initializeDatabase(this);
        }
        dataItemsList = SampleDataProvider.getData(0);
        Collections.sort(dataItemsList, new Comparator<DataItem>() {
            @Override
            public int compare(DataItem dataItem, DataItem t1) {
                return dataItem.getItemName().compareTo(t1.getItemName());
            }
        });
        toolbarBottomActivityFileManager = findViewById(R.id.toolbar_bottom_activity_file_manager);
        adapter = new DataItemAdapter(this, dataItemsList, toolbarBottomActivityFileManager);
        recyclerView = findViewById(R.id.recycler_view_folders_activity_file_manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {
        imageViewShareActivityFileManager = findViewById(R.id.image_view_share_activity_file_manager);
        imageViewDeleteActivityFileManager = findViewById(R.id.image_view_delete_activity_file_manager);
        imageViewMoveActivityFileManager = findViewById(R.id.image_view_move_activity_file_manager);
        imageViewCopyActivityFileManager = findViewById(R.id.image_view_copy_activity_file_manager);
    }

    @Override
    public void onBackPressed() {
        if (adapter.isLongPressed()) {//to hide manager bar
            adapter.setLongPressed(false);
            adapter.clearSelectedList();
            adapter.notifyDataSetChanged();
            Toolbar tl = findViewById(R.id.toolbar_bottom_activity_file_manager);
            tl.setVisibility(View.GONE);
            adapter.setCheckedNumber(0);
            adapter.setFolderSelected(0);
        } else {
            if (adapter.getCurrentFolder() != 0) {//to go to parent folder
                adapter.setCurrentFolder(SampleDataProvider.getParentId(adapter.getCurrentFolder()));
                adapter.notifyDataSetChanged();
            } else {
                if (adapter.isMoveItem()) {//to hide manager bar in case of copy/cut
                    adapter.setLongPressed(false);
                    adapter.clearSelectedList();
                    Toolbar tl = findViewById(R.id.toolbar_bottom_activity_file_manager);
                    tl.setVisibility(View.GONE);
                    adapter.setCheckedNumber(0);
                    adapter.setFolderSelected(0);
                    adapter.setCurrentFolder(0);
                    adapter.hideManagerBar();
                    adapter.setMoveItem(false);
                    adapter.notifyDataSetChanged();
                } else {
                    super.onBackPressed();
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.image_view_search_activity_file_manager:
//                Toast.makeText(getApplicationContext(), "Search file/folder", Toast.LENGTH_LONG).show();
//                break;
            case R.id.image_view_rename_activity_file_manager:
                // Toast.makeText(getApplicationContext(), "Rename file/folder", Toast.LENGTH_LONG).show();
                break;
            case R.id.image_view_share_activity_file_manager:
                Toast.makeText(getApplicationContext(), "Share file/folder", Toast.LENGTH_LONG).show();
                break;
            case R.id.image_view_delete_activity_file_manager:
                Toast.makeText(getApplicationContext(), "Delete file/folder", Toast.LENGTH_LONG).show();
                break;
            case R.id.image_view_copy_activity_file_manager:
                Toast.makeText(getApplicationContext(), "Copy file/folder", Toast.LENGTH_LONG).show();
                break;
            case R.id.image_view_move_activity_file_manager:
                Toast.makeText(getApplicationContext(), "Move file/folder", Toast.LENGTH_LONG).show();
                break;


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        adapter.setCheckedNumber(0);
        adapter.setFolderSelected(0);
        adapter.setLongPressed(false);
        adapter.setCurrentFolder(adapter.getCurrentFolder());
        toolbarBottomActivityFileManager.setVisibility(View.GONE);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_layout_file_manager, menu);
        getMenuInflater().inflate(R.menu.menu_layout_file_manager, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search_file_manager);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setSearch(false);
                adapter.setCurrentFolder(adapter.getCurrentFolder());
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.setSearch(false);
                adapter.setCurrentFolder(adapter.getCurrentFolder());
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_camera_menu_layout_file_manager:
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.new_folder_menu_layout_file_manager:
                long id = adapter.getCurrentFolder();
                if (id != 0) {
                    final EditText editText = new EditText(this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Create Folder");
                    builder.setMessage("Enter Folder Name");
                    builder.setCancelable(false);
                    builder.setView(editText);
                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!(editText.getText().toString().equals(""))) {
                                String newFolder = editText.getText().toString();
                                FolderEntity newFolderObj = new FolderEntity();
                                newFolderObj.setFolderName(newFolder);
                                newFolderObj.setParent(adapter.getCurrentFolder());
                                newFolderObj.setCreatedOn(DateTimeHelper.getCurrentDate());
                                newFolderObj.setModifiedOn(DateTimeHelper.getCurrentDate());
                                newFolderObj.setActive(1);
                                Database.getInstance(FileManagerActivity.this).folderDao().insertFolder(newFolderObj);
                                Snackbar snackbar = Snackbar
                                        .make(recyclerView, "Folder Created as " + newFolder, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                adapter.refresh();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(recyclerView, "Please Enter Folder Name", Snackbar.LENGTH_LONG);
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
                } else {
                    Snackbar snackbar = Snackbar
                            .make(recyclerView, "Can not add Folders in root folder", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                adapter.refresh();
                return true;
            case R.id.settings_menu_layout_file_manager:
                Intent settingsIntent = new Intent(this, DefaultSettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.setSearch(true);
        adapter.setLongPressed(false);
        adapter.clearSelectedList();
        toolbarBottomActivityFileManager.setVisibility(View.GONE);
        String userInput = s.toLowerCase();
        userInput = userInput.trim();
        if (!userInput.isEmpty()) {
            List<FolderEntity> folders = new ArrayList<>();
            List<PictureEntity> images = new ArrayList<>();
            List<DocumentEntity> pdfs = new ArrayList<>();

            List<DataItem> dataItemsList = new ArrayList<>();
            List<DataItem> dataItemsSearchList = new ArrayList<>();
            folders = Database.getInstance(this).folderDao().getAllFolders();
            images = Database.getInstance(this).pictureDao().getAllImages();
            pdfs = Database.getInstance(this).documentDao().getAllPdfs();
            dataItemsList = SampleDataProvider.populateDataByFolders(folders, dataItemsList);
            dataItemsList = SampleDataProvider.populateDataByFiles(images, dataItemsList);
            dataItemsList = SampleDataProvider.populateDataByPdfs(pdfs, dataItemsList);

            for (DataItem item :
                    dataItemsList) {
                if (item.getItemName().toLowerCase().contains(userInput)) {
                    dataItemsSearchList.add(item);
                }
            }
            adapter.updateData(dataItemsSearchList);
            adapter.notifyDataSetChanged();
        }
        return true;
    }
}
