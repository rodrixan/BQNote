package es.rodrixan.apps.android.bqnote.services;

import android.app.Activity;
import android.content.Context;

import com.evernote.client.android.EvernoteSession;

/**
 * Implementation of EvernoteService
 */
public class EvernoteServiceImpl implements EvernoteService {
    private static final String CONSUMER_KEY = "rodrixan-5042";
    private static final String CONSUMER_SECRET = "3caeb6da5a2e5ce5";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = false;

    @Override
    public EvernoteSession createSession(final Context context) {
        return new EvernoteSession.Builder(context)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }

    @Override
    public void loginToEvernote(final Activity activity) {
        EvernoteSession.getInstance().authenticate(activity);
    }


    @Override
    public boolean isLoggedIn() {
        return EvernoteSession.getInstance().isLoggedIn();
    }
}
