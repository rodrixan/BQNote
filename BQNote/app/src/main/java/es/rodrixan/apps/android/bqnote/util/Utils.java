package es.rodrixan.apps.android.bqnote.util;

import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.evernote.client.android.type.NoteRef;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Static class for Utilities
 */
public final class Utils {
    private Utils() {
    }

    /**
     * As done in https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/activity/ViewHtmlActivity.java
     * Loads into a webview the html data from a note
     *
     * @param webView    WebView where to insert the data
     * @param htmlString data to load
     * @param noteRef    given note for the EvernoteHTML Helper
     */
    public static void setWebViewNoteContent(final WebView webView, final String htmlString, final NoteRef noteRef) {

        final String data = "<html><head></head><body>" + htmlString + "</body></html>";

        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
                try {
                    final Response response = EvernoteUtils.getEvernoteHtmlHelper(noteRef).fetchEvernoteUrl(url);
                    final WebResourceResponse webResourceResponse = toWebResource(response);
                    if (webResourceResponse != null) {
                        return webResourceResponse;
                    }

                } catch (final Exception e) {
                    Log.e("Utils", "Error while displaying web content: " + e.getMessage());
                }

                return super.shouldInterceptRequest(view, url);
            }
        });

        webView.loadDataWithBaseURL("", data, "text/html", "UTF-8", null);
    }

    /**
     * As done in https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/activity/ViewHtmlActivity.java
     * Transforms a response into a web resource
     *
     * @param response data received from the server
     * @return web resource obtained from the response
     * @throws IOException
     */
    private static WebResourceResponse toWebResource(final Response response) throws IOException {
        if (response == null || !response.isSuccessful()) {
            return null;
        }

        final String mimeType = response.header("Content-Type");
        final String charset = response.header("charset");
        return new WebResourceResponse(mimeType, charset, response.body().byteStream());
    }


}
