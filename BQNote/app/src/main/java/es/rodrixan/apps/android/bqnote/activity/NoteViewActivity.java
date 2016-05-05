package es.rodrixan.apps.android.bqnote.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.evernote.client.android.type.NoteRef;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Detail of a note (Activity version for mobiles)
 * Shows the html view of the note
 */
public class NoteViewActivity extends AppCompatActivity {

    private static final String EXTRA_HTML = "html";
    private static final String EXTRA_NOTEREF = "noteref";

    private NoteRef mNoteRef;
    private String mHtmlString;
    private WebView mWebView;

    /**
     * Creates a new intent with some associated data
     *
     * @param packageContext context for the activity
     * @param noteRef        note to display
     * @param html           string with the html content
     * @return intent with extra data
     */
    public static Intent newIntent(final Context packageContext, final NoteRef noteRef, final String html) {
        final Intent i = new Intent(packageContext, NoteViewActivity.class);
        i.putExtra(EXTRA_HTML, html);
        i.putExtra(EXTRA_NOTEREF, noteRef);
        return i;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mNoteRef = getIntent().getParcelableExtra(EXTRA_NOTEREF);
        mHtmlString = getIntent().getStringExtra(EXTRA_HTML);


        wireComponents();
        Utils.setWebViewNoteContent(mWebView, mHtmlString, mNoteRef);
    }

    /**
     * Matches the layout components to the local fields
     */
    private void wireComponents() {

        setTitle(mNoteRef.getTitle());

        mWebView = (WebView) findViewById(R.id.webView);

    }


}
