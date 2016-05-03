package es.rodrixan.apps.android.bqnote.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.type.NoteRef;
import com.squareup.okhttp.Response;

/**
 * https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/task/BaseTask.java
 * @author rwondratschek
 */
public class GetNoteHtmlTask extends BaseTask<String> {

    private final NoteRef mNoteRef;

    public GetNoteHtmlTask(final NoteRef noteRef) {
        super(String.class);
        mNoteRef = noteRef;
    }

    @Override
    protected String checkedExecute() throws Exception {
        final EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

        final EvernoteHtmlHelper htmlHelper;
        if (mNoteRef.isLinked()) {
            htmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
        } else {
            htmlHelper = clientFactory.getHtmlHelperDefault();
        }

        final Response response = htmlHelper.downloadNote(mNoteRef.getGuid());
        return htmlHelper.parseBody(response);
    }

    public NoteRef getNoteRef() {
        return mNoteRef;
    }
}