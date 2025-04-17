package dev.silvadev.build;
import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Task {
   
   public interface onProgressUpdateListener {
       void onProgressUpdate(BuildTask.BuildKind kind, String message);
   }
   
   protected onProgressUpdateListener listener;
   
   public void setProgressListener(onProgressUpdateListener listener) {
       this.listener = listener;
   }
   
   public void onProgressUpdate(BuildTask.BuildKind kind, String message) {
   	if(kind != null && message != null) {
   		listener.onProgressUpdate(kind, message);
   	}
   }
   
   public abstract void configure();
   public abstract void start() throws Exception;
   
   public List<File> getClassPathDir(Context context) {
   	List<File> files = new ArrayList<>();
       
       File[] fileArray = new File(context.getExternalFilesDir(null), "classpath").listFiles();
       for(File file : fileArray) {
       	files.add(file);
       }
       
       return files;
   }
}
