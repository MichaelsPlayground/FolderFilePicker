package de.androidcrypto.internalstoragefilefolderchooser;

import static android.os.Build.VERSION.SDK_INT;

import static de.androidcrypto.internalstoragefilefolderchooser.ViewUtils.showSnackbarGreen;
import static de.androidcrypto.internalstoragefilefolderchooser.ViewUtils.showSnackbarRed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Serializable {

    private final String TAG = "FolderFilePicker";

    Button generateSampleFiles, listFolder, listFiles, browseFolder;

    Button grantStoragePermissions, checkStoragePermissions, startFilePicker;
    com.google.android.material.button.MaterialButton storagePermissionsGranted;
    private View view;

    EditText selectedFolder, selectedFile, browsedFile;

    Intent listFolderIntent, listFilesIntent, browseFolderIntent;

    private static final int REQUEST_CODE_PERMISSION_BELOW_SDK30 = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.viewMainLayout);

        generateSampleFiles = findViewById(R.id.btnGenerateSampleFiles);
        listFolder = findViewById(R.id.btnListFolder);
        listFiles = findViewById(R.id.btnListFiles);
        browseFolder = findViewById(R.id.btnBrowseFolder);
        selectedFolder = findViewById(R.id.etSelectedFolder);
        selectedFile = findViewById(R.id.etSelectedFile);
        browsedFile = findViewById(R.id.etBrowsedFile);

        grantStoragePermissions = findViewById(R.id.btnGrantStoragePermissions);
        checkStoragePermissions = findViewById(R.id.btnCheckStoragePermissions);
        storagePermissionsGranted = findViewById(R.id.btnStoragePermissionsGranted);
        startFilePicker = findViewById(R.id.btnFilePicker);

        listFolderIntent = new Intent(MainActivity.this, ListFolder.class);
        listFilesIntent = new Intent(MainActivity.this, ListFiles.class);
        browseFolderIntent = new Intent(MainActivity.this, BrowseFolder.class);

        checkForStoragePermissions();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String folder = "";
            String file = "";
            folder = (String) getIntent().getSerializableExtra("selectedFolder"); //Obtaining data
            if (folder != null) {
                selectedFolder.setText(folder);
                System.out.println("MainActivity folder: " + folder);
                // todo do what has todo when folder is selected
            }
            file = (String) getIntent().getSerializableExtra("selectedFile"); //Obtaining data
            if (file != null) {
                selectedFile.setText(file);
                System.out.println("MainActivity file: " + file);
                // todo do what has todo when file is selected
            }
        }

        generateSampleFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generates 50 files in 50 sub folders plus 50 files in root folder
                String basisFilename = "ABC_2021_";
                String basisFolderFilename = "XYZ_2021_";
                String basisExtension = ".csv";
                String completeFilename;
                // start with root folder
                System.out.println("generate 50 sample files in root folder");
                int counter = 0;
                for (int i = 1; i < 51; i++) {
                    completeFilename = basisFilename +
                            String.format(Locale.GERMANY, "%02d", i) +
                            basisExtension;
                    writeFileToInternalStorage("", completeFilename);
                }
                System.out.println("generate 50 sample files in 50 subfolder");
                String subDirectory;
                for (int j = 1; j < 51; j++) {
                    subDirectory = "2021_" + String.format(Locale.GERMANY, "%02d", j);
                    for (int i = 1; i < 51; i++) {
                        completeFilename = basisFolderFilename +
                                String.format(Locale.GERMANY, "%02d", i) +
                                basisExtension;
                        writeFileToInternalStorage(subDirectory, completeFilename);
                    }
                }
                System.out.println("done, all files and folders created");
            }
        });

        listFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(listFolderIntent);
            }
        });

        listFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(listFilesIntent);
            }
        });

        browseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(browseFolderIntent);
            }
        });

        /**
         * section for file picker
         */

        grantStoragePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "grantStoragePermission");
                // R = SDK 30
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    } else { //request for the permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                } else {
                    //below android 11=======
                    verifyPermissionsBelowSdk30();
                    //startActivity(new Intent(view.getContext(), MainActivity.class));
                    //ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                }
            }
        });

        checkStoragePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "checkStoragePermissions");
                checkForStoragePermissions();
            }
        });

        startFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "startFilePicker");
                Intent intent = new Intent(MainActivity.this, FolderFilePickerActivity.class);
                // define the starting folder
                Bundle bundle = new Bundle();
                bundle.putString("returnToActivity", "MainActivity");

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    /**
     * section permission granting
     */

    private void checkForStoragePermissions() {
        // R = SDK 30
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                showSnackbarGreen(view, "Storage permissions granted");
                storagePermissionsGranted.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green));
            } else {
                // storage permission not granted
                showSnackbarRed(view, "Please grant storage permissions");
                storagePermissionsGranted.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            }
        }
    }

    private void verifyPermissionsBelowSdk30() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            showSnackbarGreen(view, "Storage permissions granted");
            storagePermissionsGranted.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green));
            Log.i(TAG, "permissions were granted on device below SDK30");
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_CODE_PERMISSION_BELOW_SDK30);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_BELOW_SDK30) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackbarGreen(view, "Storage permissions granted");
                storagePermissionsGranted.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                Log.i(TAG, "permissions were granted on device below SDK30");
            } else {
                Toast.makeText(this, "Grant Storage Permission is Required to use this function.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * section write data to internal storage
     * @param subDir
     * @param filename
     * @return
     */


    private boolean writeFileToInternalStorage(String subDir, String filename) {
        byte[] data = "test".getBytes(StandardCharsets.UTF_8);
        try {
            File dir = new File(getFilesDir(), subDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            System.out.println("** dir: " + dir.toString());
            File newFile = new File(dir, filename);
            System.out.println("newFile: " + newFile.toString());
            FileOutputStream output = new FileOutputStream(new File(dir, filename));
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            int DEFAULT_BUFFER_SIZE = 1024;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n = 0;
            n = input.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            while (n >= 0) {
                output.write(buffer, 0, n);
                n = input.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            }
            output.close();
            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}