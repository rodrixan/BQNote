package es.rodrixan.apps.android.bqnote.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Sends a bitmap to the OCR server, retrieving a text
 * https://ocr.space/OCRAPI
 */
public class SendBitmapOCRTask extends BaseTask<String> {
    private static final String OCR_API_URL = "https://api.ocr.space/parse/image";
    private static final String OCR_API_KEY = "helloworld";
    //always the same file for overriding it
    private static final String IMG_NAME = "IMG_HANDWRITING_NOTE.png";

    private static final int BUFFER_MAX_SIZE = 1024;

    private static final Uri ENDPOINT = Uri
            .parse(OCR_API_URL)
            .buildUpon()
            .appendQueryParameter("apikey", OCR_API_KEY)
            .appendQueryParameter("languaje", "eng")
            .build();

    private final File mImgFileDir;

    /**
     * @param bitmap  the bitmap to save as an image to send to the OCR Server
     * @param context activity for taking the external storage
     */
    public SendBitmapOCRTask(final Bitmap bitmap, final Context context) {
        super((Class) String.class);

        mImgFileDir = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).toString());
        Log.d(Utils.LOG_TAG, "File: " + mImgFileDir.toString() + "/" + IMG_NAME);
        storeImage(bitmap, mImgFileDir);

    }

    /**
     * For debugging purposes
     *
     * @return true if the external storage is writable, false otherwise
     */
    private boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Stores a bitmas as an image in the external storage
     *
     * @param bitmap     bitmap to save
     * @param imgFileDir directory where to store the bitmap
     */
    private void storeImage(final Bitmap bitmap, final File imgFileDir) {
        FileOutputStream fos = null;
        Log.d(Utils.LOG_TAG, "isExternalStorageWritable: " + isExternalStorageWritable());
        try {
            imgFileDir.mkdirs();//creating dir if not exist
            fos = new FileOutputStream(imgFileDir.toString() + "/" + IMG_NAME);
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
            fos.close();
        } catch (final FileNotFoundException e) {
            Log.e(Utils.LOG_TAG, "Error FileNotFoundException: " + e.getMessage());
        } catch (final IOException e) {
            Log.e(Utils.LOG_TAG, "Error IOException: " + e.getMessage());
        }
        Log.i(Utils.LOG_TAG, "Stored image file");
    }


    @Override
    protected String checkedExecute() throws Exception {
        return fetchTextFromImgOCR(mImgFileDir);
    }

    /**
     * Fecths the OCR String from the Server
     *
     * @param f file with the path to the image to send
     * @return the recognized text from the file
     */
    public String fetchTextFromImgOCR(final File f) {
        final String url = buildOCRServerUrl(f);
        return downloadTextFromOCRServer(url);
    }

    /**
     * Builds the URL for the OCR server
     *
     * @param file file with the path to the image to send
     * @return URL to the server uri
     */
    private String buildOCRServerUrl(final File file) {

        final Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("file", file.toString() + "/" + IMG_NAME);
        final String uriString = uriBuilder.build().toString();
        Log.i(Utils.LOG_TAG, "Building URL: " + uriString.replace("%2F", "/"));
        return uriString;
    }

    /**
     * @param url url to the server
     * @return recognized text
     */
    private String downloadTextFromOCRServer(final String url) {
        try {
            final String jsonString = getUrlString(url);
            Log.i(Utils.LOG_TAG, "Received JSON: " + jsonString);
            final JSONObject jsonBody = new JSONObject(jsonString);
            return parseItems(jsonBody);
        } catch (final IOException ioe) {
            Log.e(Utils.LOG_TAG, "Failed to fetch text:" + ioe.getMessage());
        } catch (final JSONException je) {
            Log.e(Utils.LOG_TAG, "Failed to parse JSON:" + je.getMessage());
        }
        return null;
    }

    /**
     * Parse the whole response, taking only the field needed
     *
     * @param jsonBody response
     * @return recognized text
     * @throws JSONException
     */
    private String parseItems(final JSONObject jsonBody) throws JSONException {
        final Gson gson = new Gson();
        //If there's not such parsed text, it will throw an exception
        final JSONObject textJSONObject = jsonBody.getJSONObject("ParsedText");
        final String parsedString = (String) gson.fromJson(textJSONObject.toString(), String.class);
        Log.i(Utils.LOG_TAG, "Parsed string: " + parsedString);
        return parsedString;
    }

    /**
     * Retrieve the bytes from a HTTP connection
     *
     * @param urlSpec URL
     * @return content of the response, in byte array
     * @throws IOException
     */
    private byte[] getURLBytes(final String urlSpec) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(urlSpec).openConnection();
        connection.setRequestMethod("POST");
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;

            final byte[] buffer = new byte[BUFFER_MAX_SIZE];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Retrieves the String representation of a HTTP response
     *
     * @param urlSpec URL
     * @return the string response
     * @throws IOException
     */
    private String getUrlString(final String urlSpec) throws IOException {
        return new String(getURLBytes(urlSpec));
    }


}
