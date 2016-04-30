package es.rodrixan.apps.android.bqnote.services;

import android.app.Activity;
import android.content.Context;

import com.evernote.client.android.EvernoteSession;

/**
 * Evernote service interface
 */
public interface EvernoteService {
    /**
     * Creates a new evernote session
     *
     * @param context activity which calls the method
     * @return instance of evernote session
     */
    EvernoteSession createSession(Context context);

    /**
     * Attemps to login to Evernote
     *
     * @param activity activity which calls the method
     */
    void loginToEvernote(Activity activity);

    /**
     * @return true if there is a user logged in, false in other case.
     */
    boolean isLoggedIn();
}
