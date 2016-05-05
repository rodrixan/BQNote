package es.rodrixan.apps.android.bqnote.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;

/**
 * Modified by Rodrigo de Blas to fit BQNote
 * Creates a new note and add it to the default notebook.
 * https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/task/CreateNewNoteTask.java
 *
 * @author rwondratschek
 */
public class CreateNewNoteTask extends BaseTask<Note> {
    private final String mTitle;
    private final String mContent;
    private final Bitmap mBitmap;
    private final Context mContext;


    /**
     * Creates a sending note task
     *
     * @param title   title of the note
     * @param content content of the note. Will not be used if bitmap is not null.
     * @param bitmap  bitmap for handwriting note. Will not be used if content is not null.
     * @param context activity which calls the task
     */
    public CreateNewNoteTask(final String title, final String content, final Bitmap bitmap, final Context context) {
        super(Note.class);

        mTitle = title;
        mContent = content;
        mBitmap = bitmap;
        mContext = context;

    }

    @Override
    protected Note checkedExecute() throws Exception {
        final Note note = new Note();
        note.setTitle(mTitle);

        if (mContent != null) {
            note.setContent(EvernoteUtil.NOTE_PREFIX + mContent + EvernoteUtil.NOTE_SUFFIX);
            return sendNoteToEvernote(note);
        }
        if (mBitmap != null) {

            final Resource resource = createResource();
            note.addToResources(resource);

            // Set the note's ENML content
            final String content = EvernoteUtil.NOTE_PREFIX
                    + EvernoteUtil.createEnMediaTag(resource)
                    + EvernoteUtil.NOTE_SUFFIX;

            note.setContent(content);

            return sendNoteToEvernote(note);
        }
        Log.e(this.getClass().getName(), "Error on note fields");
        return null;
    }

    /**
     * @param note note to send
     * @return note returned from the server
     */
    private Note sendNoteToEvernote(final Note note) {
        final EvernoteNoteStoreClient noteStoreClient = EvernoteUtils.getNoteStoreClient();
        Note returnedNote = null;
        try {
            returnedNote = noteStoreClient.createNote(note);
        } catch (final Exception e) {
            Log.e(this.getClass().getName(), "Error while deleting note: " + e.getCause().getMessage());
        }
        return returnedNote;
    }

    /**
     * Converts the bitmap received in a resource for a evernote note
     *
     * @return the resource created
     * @throws IOException
     */
    private Resource createResource() throws IOException {
        final ImageData imgData = EvernoteUtils.bitmapToImageData(mBitmap, mContext);

        final ResourceAttributes attributes = createAttributes(imgData);
        final FileData data = createFileData(imgData);

        final Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(imgData.getMimeType());
        resource.setAttributes(attributes);
        return resource;
    }

    /**
     * Creates the attributes for a resource from image data
     *
     * @param imgData data to convert
     * @return attributes for a resource
     * @throws IOException
     */
    private ResourceAttributes createAttributes(final ImageData imgData) throws IOException {
        final ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName(imgData.getFileName());
        return attributes;
    }

    /**
     * Creates a FileData from image data
     *
     * @param imgData data to convert
     * @return the file data
     * @throws IOException
     */
    private FileData createFileData(final ImageData imgData) throws IOException {
        // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
        final InputStream in = new BufferedInputStream(new FileInputStream(imgData.getPath()));
        final FileData data = new FileData(EvernoteUtil.hash(in), new File(imgData.getPath()));
        if (in != null) {
            in.close();
        }
        return data;
    }

    /**
     * Class for contain bitmap information
     * Look at the original inner class:https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/task/CreateNewNoteTask.java
     */
    public static class ImageData implements Parcelable {

        private final String mPath;
        private final String mFileName;
        private final String mMimeType;

        public ImageData(final String path, final String fileName, final String mimeType) {
            mPath = path;
            mFileName = fileName;
            mMimeType = mimeType;
        }

        public String getPath() {
            return mPath;
        }

        public String getFileName() {
            return mFileName;
        }

        public String getMimeType() {
            return mMimeType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(mPath);
            dest.writeString(mFileName);
            dest.writeString(mMimeType);
        }

        public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
            @Override
            public ImageData createFromParcel(final Parcel source) {
                return new ImageData(source.readString(), source.readString(), source.readString());
            }

            @Override
            public ImageData[] newArray(final int size) {
                return new ImageData[size];
            }
        };
    }
}
