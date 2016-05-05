package es.rodrixan.apps.android.bqnote.task;

import android.util.Log;

import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;

import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;

/**
 * Deletes a note from the evernote account
 */
public class DeleteNoteTask extends BaseTask<Boolean> {
    private final String mGuid;

    public DeleteNoteTask(final String guid) {
        super(Boolean.class);

        mGuid = guid;

    }

    @Override
    protected Boolean checkedExecute() {
        final EvernoteNoteStoreClient noteStoreClient = EvernoteUtils.getNoteStoreClient();

        try {
            noteStoreClient.deleteNote(mGuid);
        } catch (final Exception e) {
            Log.e(this.getClass().getName(), "Error while deleting note: " + e.getCause().getMessage());
            return false;
        }
        return true;
    }
}
