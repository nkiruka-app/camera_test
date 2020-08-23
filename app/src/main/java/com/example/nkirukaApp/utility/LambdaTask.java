package com.example.nkirukaApp.utility;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class LambdaTask extends AsyncTask<Void, Void, Void> {

    public WeakReference<Activity> activityWeakReference;
    public Task onRun = null;
    public Task onFinish = null;

    public interface Task{
        void task();
    }

    public LambdaTask(Activity activity, Task onRun, Task onFinish){
        activityWeakReference = new WeakReference<>(activity);
        this.onRun = onRun;
        this.onFinish = onFinish;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        this.onRun.task();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Activity activity = activityWeakReference.get();
        if(activity != null && !activity.isFinishing() ) {
            this.onFinish.task();
        }
    }
}
