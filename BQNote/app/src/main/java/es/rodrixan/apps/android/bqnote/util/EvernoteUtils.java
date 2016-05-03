package es.rodrixan.apps.android.bqnote.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;

import es.rodrixan.apps.android.bqnote.activity.EntryPointActivity;

/**
 * Utilities for the login process
 */
public final class EvernoteUtils {
    private static final String CONSUMER_KEY = "rodrixan-5042";
    private static final String CONSUMER_SECRET = "3caeb6da5a2e5ce5";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = false;

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

}
