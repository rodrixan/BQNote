package es.rodrixan.apps.android.bqnote.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Shows a dialog for creating a new note
 */
public class NewNoteDialogFragment extends DialogFragment {

    private static final String EXTRA_TITLE = "extra title";
    private static final String EXTRA_CONTENT = "extra content";

    private AutoCompleteTextView mTitle;
    private AutoCompleteTextView mContent;


    /**
     * @return new instance of this fragment
     */
    public static NewNoteDialogFragment newInstance() {
        return new NewNoteDialogFragment();
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_note, null);


        wireComponents(v);

        return createDateAlertDialog(v);
    }

    /**
     * Assigns the view to the corresponding local fields
     *
     * @param v
     */
    private void wireComponents(final View v) {
        mTitle = (AutoCompleteTextView) v.findViewById(R.id.dialog_title_text);
        mContent = (AutoCompleteTextView) v.findViewById(R.id.dialog_content_text);
    }

    /**
     * Creates a dialog from a view.
     * Possitive button: create a note given a title and description
     * Negative button: cancel the current operation
     *
     * @param v root view
     * @return Dialog created
     */
    private Dialog createDateAlertDialog(final View v) {

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_note_title)
                .setPositiveButton(R.string.dialog_create_note, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final String title = mTitle.getText().toString();
                        final String content = mContent.getText().toString();
                        if (title != null && content != null && !title.isEmpty() && !content.isEmpty()) {
                            sendResult(Activity.RESULT_OK, title, content);
                        } else {
                            sendResult(Activity.RESULT_OK, null, null);
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Log.i(Utils.LOG_TAG, "Note creation canceled");
                        dialog.cancel();
                    }
                })
                .create();

    }


    /**
     * Notifies the parent fragment to create a new note with given title and content
     *
     * @param resultCode result code of the action
     * @param title      title of the note
     * @param content    content of the note
     */
    private void sendResult(final int resultCode, final String title, final String content) {
        if (getTargetFragment() == null) {
            return;
        }
        final Intent i = new Intent();
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_CONTENT, content);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    /**
     * @param i intent where to extract the data from
     * @return title of the note
     */
    public static String getTitleFromIntent(final Intent i) {
        return i.getStringExtra(EXTRA_TITLE);
    }

    /**
     * @param i intent where to extract the data from
     * @return content of the note
     */
    public static String getContentFromIntent(final Intent i) {
        return i.getStringExtra(EXTRA_CONTENT);
    }

}
