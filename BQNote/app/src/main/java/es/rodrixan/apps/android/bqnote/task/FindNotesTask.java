package es.rodrixan.apps.android.bqnote.task;

import android.support.annotation.Nullable;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.NoteSortOrder;

import java.util.List;

/**
 * Modified by Rodrigo de Blas for fitting BQNote App
 * https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/task/FindNotesTask.java
 * <p/>
 * Finds the notes that match certain criteria
 *
 * @author rwondratschek
 */
public class FindNotesTask extends BaseTask<List<NoteRef>> {

    private final EvernoteSearchHelper.Search mSearch;

    @SuppressWarnings("unchecked")
    public FindNotesTask(final int offset, final int maxNotes, @Nullable NoteFilter noteFilter) {
        super((Class) List.class);


        if (noteFilter == null) {
            noteFilter = new NoteFilter();
            noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());
        }
        mSearch = new EvernoteSearchHelper.Search()
                .setOffset(offset)
                .setMaxNotes(maxNotes)
                .setNoteFilter(noteFilter);

        mSearch.addScope(EvernoteSearchHelper.Scope.PERSONAL_NOTES);
    }

    @Override
    protected List<NoteRef> checkedExecute() throws Exception {
        final EvernoteSearchHelper.Result searchResult = EvernoteSession.getInstance()
                .getEvernoteClientFactory()
                .getEvernoteSearchHelper()
                .execute(mSearch);

        return searchResult.getAllAsNoteRef();
    }
}
