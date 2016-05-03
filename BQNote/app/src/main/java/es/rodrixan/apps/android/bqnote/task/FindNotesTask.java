package es.rodrixan.apps.android.bqnote.task;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * Modified by Rodrigo de Blas for fitting BQNote App
 * @author rwondratschek
 */
public class FindNotesTask extends BaseTask<List<NoteRef>> {

    private final EvernoteSearchHelper.Search mSearch;

    @SuppressWarnings("unchecked")
    public FindNotesTask(int offset, int maxNotes, @Nullable NoteFilter noteFilter) {
        super((Class) List.class);


        if(noteFilter==null){
            noteFilter=new NoteFilter();
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
        EvernoteSearchHelper.Result searchResult = EvernoteSession.getInstance()
                .getEvernoteClientFactory()
                .getEvernoteSearchHelper()
                .execute(mSearch);

        return searchResult.getAllAsNoteRef();
    }
}
