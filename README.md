# IFolder & File picker

This is a sample app that presents a file and folder picker for the storage.

There are 3 activities as follows:

a) ListFolder class: started from MainActivity, it lists all directories in internal storage starting with root folder. When clicking on an entry the folder name is returned to MainActivity and displayed in an EditText field.

b) ListFiles class: started from MainActivity, it lists all files in internal storage root folder. When clicking on an entry the file name is returned to MainActivity and displayed in an EditText field.

c) BrowseFolder class: started from MainActivity, it lists all directories in internal storage starting with root folder. When clicking on an entry the folder name is forwarded to ListFiles class and the content (files) is imediatly displayed. Now you can select a file. When clicking on an entry the folder and file name is returned to MainActivity and displayed in EditText fields.

d) FilePicker class: started from MainActivity, it is a classic file and folder explorer for the storage

**Note**: as the FilePicker class is an explorer for the external storage and it starts from the root of 
the device the app needs a special permission to work with Android 30+. The usage of this permission will 
result in a not listing in the PlayStore if the app's purpose is not something like a file manager or 
file synchronize.

Add these lines in AndroidManifest.xml:
```plaintext
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
    
    ...
    <application
        android:requestLegacyExternalStorage="true"
        ...
    </application>        
```


Tested and running on SDK33
