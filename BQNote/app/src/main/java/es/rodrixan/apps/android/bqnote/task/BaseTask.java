package es.rodrixan.apps.android.bqnote.task;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.evernote.client.android.helper.Cat;
import com.evernote.edam.error.EDAMUserException;

import net.vrallev.android.task.Task;
import net.vrallev.android.task.TaskExecutor;

import java.util.concurrent.Executors;

import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;

/**
 * Modified by Rodrigo de Blas for using it on BQNote
 * https://github.com/evernote/evernote-sdk-android/blob/master/demo/src/main/java/com/evernote/android/demo/task/BaseTask.java
 *
 * @author rwondratschek
 */
public abstract class BaseTask<RESULT> extends Task<RESULT> {

    private static final Cat CAT = new Cat("BaseTask");

    private static final TaskExecutor TASK_EXECUTOR = new TaskExecutor.Builder()
            .setExecutorService(Executors.newFixedThreadPool(12))
            .build();

    private final Class<RESULT> mResultClass;

    public BaseTask(final Class<RESULT> resultClass) {
        mResultClass = resultClass;
    }

    public void start(final Activity activity) {
        TASK_EXECUTOR.execute(this, activity);
    }

    public void start(final Activity activity, final String annotationId) {
        TASK_EXECUTOR.execute(this, activity, annotationId);
    }

    public void start(final Fragment fragment) {
        TASK_EXECUTOR.execute(this, fragment);
    }

    public void start(final Fragment fragment, final String annotationId) {
        TASK_EXECUTOR.execute(this, fragment, annotationId);
    }

    @Override
    protected final RESULT execute() {
        try {
            return checkedExecute();
        } catch (final Exception e) {
            CAT.e(e);
            checkException(e, getActivity());
            return null;
        }
    }

    protected abstract RESULT checkedExecute() throws Exception;

    @Override
    protected final Class<RESULT> getResultClass() {
        return mResultClass;
    }

    protected static void checkException(@NonNull final Exception e, @Nullable final Activity activity) {
        if (e instanceof EDAMUserException) {

            if (activity != null) {
                EvernoteUtils.logoutEvernote(activity);
            }

        }
    }
}
