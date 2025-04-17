package dev.silvadev.blockode.utils;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.util.Log;
import android.widget.TextView;
import com.downloader.*;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloaderUtils {

    private static final String TAG = "DownloaderUtils";
    private int downloadID;

    public DownloaderUtils(Context context) {
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build();
        PRDownloader.initialize(context, config);
    }

    public void start(String url, String dir, String fileName, LinearProgressIndicator progressBar, TextView textProgress, OnStatusChanged status) {
        if (Status.RUNNING == PRDownloader.getStatus(downloadID) || Status.PAUSED == PRDownloader.getStatus(downloadID)) return;

        try {
            new URL(url);

            downloadID = PRDownloader.download(url, dir, fileName).build()
                .setOnStartOrResumeListener(() -> {})
                .setOnPauseListener(() -> {})
                .setOnCancelListener(() -> {})
                .setOnProgressListener(progress -> {
                    long currentBytes = progress.currentBytes;
                    long totalBytes = progress.totalBytes;
                    if (totalBytes != -1) {
                        int percent = (int) (currentBytes * 100 / totalBytes);
                        progressBar.setIndeterminate(false);
                        progressBar.setProgress(percent);
                        textProgress.setText(percent + "%");
                    } else {
                        progressBar.setIndeterminate(true);
                        textProgress.setText("0%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override 
                    public void onDownloadComplete() {
                        status.onCompleted();
                    }
                    @Override
                    public void onError(com.downloader.Error error) {
                        status.onError(error);
                    }
                });

        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid url: " + e.getMessage());
        } catch (Exception | OutOfMemoryError e) {
            Log.e(TAG, "Donwload error: " + e.getMessage());
        }
    }

    public interface OnStatusChanged {
        void onCompleted();
        void onError(com.downloader.Error error);
    }
}