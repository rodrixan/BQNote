package es.rodrixan.apps.android.bqnote.task;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;

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


    public CreateNewNoteTask(final String title, final String content) {
        super(Note.class);

        mTitle = title;
        mContent = content;

    }

    @Override
    protected Note checkedExecute() throws Exception {
        final Note note = new Note();
        note.setTitle(mTitle);

        note.setContent(EvernoteUtil.NOTE_PREFIX + mContent + EvernoteUtil.NOTE_SUFFIX);
        return createNote(note);
    }

    protected Note createNote(final Note note) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        final EvernoteNoteStoreClient noteStoreClient = EvernoteUtils.getNoteStoreClient();
        return noteStoreClient.createNote(note);

    }
}
