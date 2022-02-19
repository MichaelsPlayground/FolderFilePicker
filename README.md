# InternalStorageFileFolderChooser

This is a sample app that presents a file and folder picker for the internal storage.

There are 3 activities as follows:

a) ListFolder class: started from MainActivity, it lists all directories in internal storage starting with root folder. When clicking on an entry the folder name is returned to MainActivity and displayed in an EditText field.

b) ListFiles class: started from MainActivity, it lists all files in internal storage root folder. When clicking on an entry the file name is returned to MainActivity and displayed in an EditText field.

c) BrowseFolder class: started from MainActivity, it lists all directories in internal storage starting with root folder. When clicking on an entry the folder name is forwarded to ListFiles class and the content (files) is imediatly displayed. Now you can select a file. When clicking on an entry the folder and file name is returned to MainActivity and displayed in EditText fields.

Tested and running on SDK30
