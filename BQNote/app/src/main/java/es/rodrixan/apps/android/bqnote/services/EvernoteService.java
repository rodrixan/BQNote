package es.rodrixan.apps.android.bqnote.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.evernote.client.android.EvernoteSession;

import es.rodrixan.apps.android.bqnote.activities.EntryPointActivity;

/**
 * EvernoteService
 */
public final class EvernoteService {
    private static final String CONSUMER_KEY = "rodrixan-5042";
    private static final String CONSUMER_SECRET = "3caeb6da5a2e5ce5";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = false;

    /**
     * Private constructot in order to make the class not instantiable
     */
    private EvernoteService() {
    }

    public static EvernoteSession createSession(final Context context) {
        return new EvernoteSession.Builder(context)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }


    public static boolean isLoggedIn() {
        return EvernoteSession.getInstance().isLoggedIn();
    }


    public static void logoutEvernote(final Activity activity) {
        EvernoteSession.getInstance().logOut();
        final Intent i = EntryPointActivity.newIntent(activity);
        activity.startActivity(i);
        activity.finish();
    }

}
