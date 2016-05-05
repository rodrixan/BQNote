package es.rodrixan.apps.android.bqnote.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.rodrixan.apps.android.bqnote.activity.EntryPointActivity;
import es.rodrixan.apps.android.bqnote.task.CreateNewNoteTask;

/**
 * Utilities for the login process
 */
public final class EvernoteUtils {
    private static final String CONSUMER_KEY = "rodrixan-5042";
    private static final String CONSUMER_SECRET = "3caeb6da5a2e5ce5";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = false;
    private static final String IMG_NAME = "EVRNT_IMG_NOTE.png";
    private static final int BITMAP_MAX_SIZE = 500;
    private static final String CLASS_NAME = "EvernoteUtils";

    private static EvernoteHtmlHelper mEvernoteHtmlHelper = null;

    /**
     * Private constructot in order to make the class not instantiable
     */
    private EvernoteUtils() {
    }

    /**
     * Initializes an Evernote session for the login process
     *
     * @param context activity which called the method
     * @return new Evernote session with custom params.
     */
    public static EvernoteSession createSession(final Context context) {
        return new EvernoteSession.Builder(context)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }

    /**
     * @return true if the user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return EvernoteSession.getInstance().isLoggedIn();
    }

    /**
     * Exits the session, going back to the Login screen
     *
     * @param activity activity which called the method. It will be finished
     */
    public static void logoutEvernote(final Activity activity) {
        EvernoteSession.getInstance().logOut();
        final Intent i = EntryPointActivity.newIntent(activity);
        activity.startActivity(i);
        activity.finish();
    }

    /**
     * Given a note, returns the HtmlHelper for it
     *
     * @param noteRef note
     * @return HtmlHelper for the note (linkd or not according to the note)
     * @throws EDAMUserException
     * @throws EDAMSystemException
     * @throws EDAMNotFoundException
     * @throws TException
     */
    public static EvernoteHtmlHelper getEvernoteHtmlHelper(final NoteRef noteRef) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        if (mEvernoteHtmlHelper == null) {
            final EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();
            if (noteRef.isLinked()) {
                mEvernoteHtmlHelper = clientFactory.getLinkedHtmlHelper(noteRef.loadLinkedNotebook());
            } else {
                mEvernoteHtmlHelper = clientFactory.getHtmlHelperDefault();
            }
        }

        return mEvernoteHtmlHelper;
    }

    /*
     * @return the default client for this session
     */
    public static EvernoteNoteStoreClient getNoteStoreClient() {
        return EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
    }


    public static CreateNewNoteTask.ImageData bitmapToImageData(final Bitmap bitmap, final Context context) {
        final String[] imgData = saveBitmapAsImage(bitmap, context);
        return new CreateNewNoteTask.ImageData(imgData[0], imgData[1], imgData[2]);
    }

    /**
     * Saves the bitmap in external storage
     *
     * @param bitmap bitmap to save
     * @return path to directory (pos 0), file name(pos 1), and mime type(pos 3) of the file inside a String array
     */
    private static String[] saveBitmapAsImage(final Bitmap bitmap, final Context context) {

        final File imgDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString());
        FileOutputStream fos = null;

        final String filePath = imgDir.toString() + "/" + IMG_NAME;

        Log.d(CLASS_NAME, "isExternalStorageWritable: " + isExternalStorageWritable());
        final Bitmap resizedBitmap = getResizedBitmap(bitmap, BITMAP_MAX_SIZE);
        try {
            imgDir.mkdirs();//create dir if not exist
            fos = new FileOutputStream(filePath);
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
            fos.close();
        } catch (final FileNotFoundException e) {
            Log.e(CLASS_NAME, "Error FileNotFoundException: " + e.getMessage());
        } catch (final IOException e) {
            Log.e(CLASS_NAME, "Error IOException: " + e.getMessage());
        }
        Log.i(CLASS_NAME, "Stored image file: " + filePath);
        return new String[]{filePath, IMG_NAME, getMimeType(filePath)};
    }

    /**
     * reduces the size of a bitmap
     *
     * @param image   bitmap to reduce size
     * @param maxSize max size for the new bitmap
     * @return resized bitmap
     */
    private static Bitmap getResizedBitmap(final Bitmap image, final int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        final float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * For debugging purposes
     *
     * @return true if the external storage is writable, false otherwise
     */
    private static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the miime type for a file
     *
     * @param url filepath
     * @return mime type
     */
    public static String getMimeType(final String url) {
        String type = null;
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
