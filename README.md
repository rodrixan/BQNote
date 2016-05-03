# BQNote
_Currently in development_

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

Evernote client for Android. Based on its [Demo App](https://github.com/evernote/evernote-sdk-android)

Features included:

+ Login: Automatic login into Evernote account. _It needs user confirmation for accessing his/her account from the app_
+ Note List: Shows all the notes that belong to the default notebook.
+ View Note: HTML view of a Note (in order to be able to see its resources properly).
+ Add Note: Creates a new note with title and content. **At this moment it only allows plain text**.

Creada por Rodrigo de Blas

<a name="def-ide"></a>

## Workspace
+ Hardware

	+ Intel i7 3.1GHz 8GB RAM (Windows 10 64b)
	+ Tested on Jiayu S3+ (Android 5.1 Lollipop)
	+ Tested on UMI X2T (Android 4.3 Jelly Bean)
	
+ Software

	+ Android Studio 2.1
	+ Java JDK 1.8
	+ Android API 22

<a name="def-app"></a>

## App deploy

1. Download repository
2. Import from Android Studio
    ```
    import -> existing Android Studio Project
    ```
3. Plug a device in and wait for ADB to start, or create a new AVD (Android Virtual Device)
    
4. Launch app, choosing the desired device
    

<a name="def-login"></a>

## Login

First time the app is launched, it will redirect to Evernote Authentication Screen. Just insert your email and password to link the app, and allow it to access your data.

Any time you want to log out, select the option in the right-top corner dropdown menu. The app will redirect to the Evernote Authentication Screen.

<a name="def-list"></a>

## Note List

Main activity of the app. It lists all the notes the user has in his/her default notebook.
By default the notes are sorted by modifications, but they can be sorted by title too. Just select the option in the dropdown menu (as same as logout).

<a name="def-detail"></a>

## View Note

Every time a note is clicked, it will lead to a WebView where the content of the note will be shown.
WebView was chosen because of the better performance about any kind of content.
To go back, press the top-left arrow or touch the _back button_ of the device.


<a name="def-add"></a>

## Add Note

In the main activity there is a floating button which function is to create a new note. When pressed, it will create a dialog where the user can write the title and the content of a note. 
Once the fields are filled, the user can save the note, adding it to the note list and his/her account on Evernote.

**note**: _currently, the content of the note can only be plain text_


