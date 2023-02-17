package de.androidcrypto.internalstoragefilefolderchooser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FolderFilePickerActivity extends AppCompatActivity {

    private final String TAG = "FolderFilePicker";

    Button selectItem;
    ListView itemsListView;

    private String[] itemsList, filesList, folderList;
    private final String FOLDER_HEADER = "= = = Folder (total ";
    private final String FILES_HEADER = "= = = Files (total ";

    String selectedFolderForIntent, parentFolderForIntent;
    Intent returnActivityIntent;
    private String returnToActivityFromIntent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_file_picker);

        itemsListView = findViewById(R.id.lvItems);

        parentFolderForIntent = ""; // will be overwritten by recursive calls

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles");
        if (extras != null) {
            System.out.println("extras not null");
            String folder = "";
            String parentFolder = "";
            folder = (String) getIntent().getSerializableExtra("selectedFolder");
            parentFolder = (String) getIntent().getSerializableExtra("parentFolder");
            returnToActivityFromIntent = (String) getIntent().getSerializableExtra("returnToActivity");
            if (returnToActivityFromIntent != null) {
                Log.i(TAG, "returnToActivity: " + returnToActivityFromIntent);
            } else {
                Log.i(TAG, "returnToActivity: is NULL");
            }
            if (parentFolder != null) {
                Log.i(TAG, "parent folder: " + parentFolder);
                parentFolderForIntent = parentFolder;
            } else {
                Log.i(TAG, "parent folder is NULL");
                listFolder(getApplicationContext(), "");
            }

            //if (!folder.equals("")) {
            if (folder != null) {
                Log.i(TAG, "received folder: " + folder);
                selectedFolderForIntent = folder;
                System.out.println("folder not null");
                //folderFromListFolder = folder;
                System.out.println("ListFile folder: " + folder);
                // todo do what has todo when folder is selected
                //listFiles.setVisibility(View.GONE);
                listFolder(getBaseContext(), folder);
                String selectFolderButton = "select folder "
                        + parentFolderForIntent + "/"
                        + selectedFolderForIntent;
                System.out.println("selectFolderButton text: " + selectFolderButton);
                //selectItem.setText(selectFolderButton);
            }
        } else {
            System.out.println("extras are NULL");
            listFolder(getApplicationContext(), "");
        }



    }

    private void listFolder(Context context, String startDirectory) {
        Log.i(TAG, "listFolder startDirectory: " + startDirectory);

        File[] files;
        if (TextUtils.isEmpty(startDirectory)) {
            Log.i(TAG, "startDirectory is empty");
            File externalStorageDir = new File(Environment.getExternalStoragePublicDirectory(""), "");
            files = externalStorageDir.listFiles();
        } else {
            Log.i(TAG, "startDirectory is: " + startDirectory);
            String recursiveFolder = parentFolderForIntent.replaceFirst("root", "");
            File externalStorageDir = new File(Environment.getExternalStoragePublicDirectory("")
                    + recursiveFolder, startDirectory);
            Log.i(TAG, "externalStorageDir: " + externalStorageDir.getAbsolutePath());
            files = externalStorageDir.listFiles();
            Log.i(TAG, "externalStorageDir files size: " + files.length);
        }

        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> folderNames = new ArrayList<>();
        if (files == null) {
            Log.i(TAG, "files is NULL, aborted");
            return;
        }
        Log.i(TAG, "files.length: " + files.length);

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileNames.add(files[i].getName());
            } else {
                folderNames.add((files[i].getName()));
            }
        }
        String folderHeader = FOLDER_HEADER + folderNames.size() + ") = = =";
        String filesHeader = FILES_HEADER + fileNames.size() + ") = = =";

        int folderNamesSize = folderNames.size();
        int fileNamesSize = fileNames.size();
        int itemsListSize = folderNamesSize + fileNamesSize + 2; // + 2 for header
        Log.i(TAG, "folderNamesSize: " + folderNamesSize + " fileNamesSize: " + fileNamesSize + " itemsNamesSize: " + itemsListSize);

        itemsList = new String[itemsListSize];

        itemsList[0] = folderHeader;
        for (int i = 0; i < folderNamesSize; i++) {
            itemsList[ 1 + i] = folderNames.get(i);
        }
        itemsList[folderNamesSize + 1] = filesHeader;
        for (int i = 0; i < fileNamesSize; i++) {
            itemsList[folderNamesSize + 2 + i] = fileNames.get(i);
        }
        Log.i(TAG, "itemsList size: " + itemsList.length);

        //itemsList = folderNames.toArray(new String[0]);
        //System.out.println("fileList size: " + itemsList.length);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
        itemsListView.setAdapter(adapter);
        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "onItemLongClick");

                return false;
            }
        });



        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemShortClick, if it is a folder use this folder as start directory");
                String selectedItem = (String) parent.getItemAtPosition(position);
                // check for header
                if (isSelectedItemHeader(selectedItem)) {
                    Log.i(TAG, "you selected a header, aborted");
                    return;
                } else {
                    Log.i(TAG, "you selected " + selectedItem);
                }
                // check if selectedItem is a folder or a file using its position
                if (position < (folderNamesSize + 1)) {
                    Log.i(TAG, "selectedFolder, recursively new search");
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedFolder", selectedItem);
                    bundle.putString("parentFolder", parentFolderForIntent +  "/" + startDirectory);
                    bundle.putString("returnToActivity", returnToActivityFromIntent);
                    returnActivityIntent = new Intent(FolderFilePickerActivity.this, FolderFilePickerActivity.class);
                    returnActivityIntent.putExtras(bundle);
                    startActivity(returnActivityIntent);
                    finish();
                } else {
                    Log.i(TAG, "selectedFile");
                }




                // we are starting a new ListFolder activity

            }
        });
    }

    private boolean isSelectedItemHeader(String selectedItem) {
        if (selectedItem.startsWith(FOLDER_HEADER)) return true;
        if (selectedItem.startsWith(FILES_HEADER)) return true;
        return false;
    }
}