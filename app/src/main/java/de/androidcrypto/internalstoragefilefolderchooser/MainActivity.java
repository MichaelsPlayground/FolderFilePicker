package de.androidcrypto.internalstoragefilefolderchooser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button generateSampleFiles, listFolder, listFiles, browseFolder;
    EditText selectedFolder, selectedFile, browsedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateSampleFiles = findViewById(R.id.btnGenerateSampleFiles);
        listFolder = findViewById(R.id.btnListFolder);
        listFiles = findViewById(R.id.btnListFiles);
        browseFolder = findViewById(R.id.btnBrowseFolder);
        selectedFolder = findViewById(R.id.etSelectedFolder);
        selectedFile = findViewById(R.id.etSelectedFile);
        browsedFile = findViewById(R.id.etBrowsedFile);


        generateSampleFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generates 50 files in 50 sub folders plus 50 files in root folder
                String basisFilename = "ABC_2021_";
                String basisExtension = ".csv";
                String completeFilename;
                // let start with root folder
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
                        completeFilename = basisFilename +
                                String.format(Locale.GERMANY, "%02d", i) +
                                basisExtension;
                        writeFileToInternalStorage(subDirectory, completeFilename);
                    }
                }
                System.out.println("done, all files and folders created");
            }
        });
    }

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