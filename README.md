# BQNote
_Awaiting for OK_
_All screenshots were taken in spanish version_

Evernote Client for Android.

## Index

- [Base Information](#def-infobase)
- [Workspace](#def-ide)
- [App Deploy](#def-app)
- [Login](#def-login)
- [Note List](#def-list)
- [View Note](#def-detail)
- [Add Note](#def-add)


<a name="def-infobase"></a>

## Base Information

Evernote client for Android. Based on its [Demo App](https://github.com/evernote/evernote-sdk-android#demo-app)

Features included:

+ Login: Automatic login into Evernote account. _It needs user confirmation for accessing an account from the app_
+ Note List: Shows all the notes that belong to the default notebook.
+ View Note: HTML view of a Note (in order to be able to see its resources properly).
+ Add Note: Creates a new note with title and content. The user can choose between normal text or creating a handwriting note, drawing in a canvas that will be saved as a bitmap and attached to the note.

Developed by Rodrigo de Blas


<a name="def-ide"></a>

## Workspace
+ Hardware

	+ Developed mainly on PC: Intel i7 3.1GHz 8GB RAM (Windows 10 64b)
	+ Tested on Jiayu S3+ (Android 5.1 Lollipop)
	+ Tested on UMI X2T (Android 4.3 Jelly Bean)
	
+ Software

	+ Android Studio 2.1
	+ Java JDK 1.8
	+ Android API 22/API 23
	+ [Evernote Android SDK 2.0.0](https://github.com/evernote/evernote-sdk-android)

<a name="def-app"></a>

## App deploy

1. Download/clone repository
2. Import from Android Studio

    ```
    import -> existing Android Studio Project
    ```
    
3. Plug a device in and wait for ADB to start, or create a new AVD (Android Virtual Device)
4. Launch app, choosing the desired device
    

<a name="def-login"></a>

## Login

![login_img][login]

First time the app is launched, it will redirect to Evernote Authentication Screen. Just insert your email and password to link the app, and allow it to access your data.

Any time you want to log out, select the option in the right-top corner dropdown menu. The app will redirect to the Evernote Authentication Screen.

<a name="def-list"></a>

## Note List
![list_img][list]
Main activity of the app. It lists all the notes the user has in his/her default notebook.
By default the notes are sorted by modifications, but they can be sorted by title too. Just select the option in the dropdown menu (as same as logout).

The user can touch a note to see its content, or make a long touch on the note to delete it.

<a name="def-detail"></a>

## View Note


|           Regular Note          |              Handwriting Note             |
|:-------------------------------:|:-----------------------------------------:|
| ![normal_view_img][normal_view] | ![handwriting_view_img][handwriting_view] |


Every time a note is clicked, it will lead to a WebView where the content of the note will be shown.
WebView was chosen because of the better performance about any kind of content.
To go back, press the top-left arrow or touch the _back button_ of the device.


<a name="def-add"></a>

## Add Note
![add_img][add]
In the main activity, there is a floating button which function is to create a new note. When pressed, it will create a dialog where the user can write the title and the content of a note. 
Once the fields are filled, the user can save the note, adding it to the note list and his/her account on Evernote.

The user can choose between plain text input or handwriting input, as shown below:

![add_hand_img][handwriting]

This allows the user to draw whatever is wanted, and save it as a regular note. For achieving this functionality, a bitmap is created then saved as a File on the external storage. Next step is to convert it into a Evernote resource for attaching it to a note that will be stored in the account.


[login]: https://github.com/rodrixan/BQNote/blob/master/screenshots/login.png 
[list]: https://github.com/rodrixan/BQNote/blob/master/screenshots/note_list.png
[normal_view]: https://github.com/rodrixan/BQNote/blob/master/screenshots/regular_note_view.png
[handwriting_view]: https://github.com/rodrixan/BQNote/blob/master/screenshots/handwriting_note_view.png
[add]: https://github.com/rodrixan/BQNote/blob/master/screenshots/new_note_text.png 
[handwriting]: https://github.com/rodrixan/BQNote/blob/master/screenshots/new_note_handwriting.png

