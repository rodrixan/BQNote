package es.rodrixan.apps.android.bqnote.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.evernote.client.android.type.NoteRef;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Detail of a note (Fragment version for tablets)
 * Shows the html view of the note
 */
public class NoteViewFragment extends Fragment {

    private static final String ARG_NOTEREF = "noteref";
    private static final String ARG_HTML = "html";

    private NoteRef mNoteRef;
    private String mHtmlString;

    private WebView mWebView;
    private TextView mTitleTextView;

    /**
     * @param noteRef reference to note to show
     * @param html    code to show
     * @return instance of this fragment
     */
    public static NoteViewFragment newInstance(final NoteRef noteRef, final String html) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_NOTEREF, noteRef);
        args.putString(ARG_HTML, html);

        final NoteViewFragment f = new NoteViewFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNoteRef = getArguments().getParcelable(ARG_NOTEREF);
        mHtmlString = getArguments().getString(ARG_HTML);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note, container, false);

        wireComponents(view);

        setData();

        return view;
    }

    /**
     * Assign the views to the correct component
     *
     * @param v root view
     */
    private void wireComponents(final View v) {
        mTitleTextView = (TextView) v.findViewById(R.id.note_title);
        mWebView = (WebView) v.findViewById(R.id.webView);
    }

    /**
     * Load the current data into the views
     */
    private void setData() {
        mTitleTextView.setText(mNoteRef.getTitle());
        Utils.setWebViewNoteContent(mWebView, mHtmlString, mNoteRef);
    }

}
