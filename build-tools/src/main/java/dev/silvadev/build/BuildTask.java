package dev.silvadev.build;
import android.content.Context;
import android.os.AsyncTask;
import dev.silvadev.build.dex.D8Task;
import dev.silvadev.build.java.JarPackager;
import dev.silvadev.build.java.JavacTask;
import dev.silvadev.build.project.Project;
import java.io.File;
import java.lang.ref.WeakReference;

@SuppressWarnings("deprecation")
public class BuildTask extends AsyncTask<Project, String, BuildTask.BuildKind>{
    
    private WeakReference<Context> context;
    private Project project;
    private onBuildStatusChanged status;
    
    private long startTime;
    
    public BuildTask(Context context, onBuildStatusChanged status) {
        this.context = new WeakReference<>(context);
        this.status = status;
    }
    
    @Override
    public void onPreExecute() {
    	startTime = System.currentTimeMillis();
    }
    
    @Override
    public BuildTask.BuildKind doInBackground(Project... params) {
        project = params[0];
        Context context = this.context.get();
        try {
        	Task javacTask = new JavacTask(context, project);
            javacTask.setProgressListener((kind, args) -> {
                publishProgress(args);
                status.onChanged(kind, args);
            });
            javacTask.configure();
            javacTask.start();
            
            /*publishProgress("Packaging JAR");
            status.onChanged(BuildKind.INFO, "Packaging JAR");
            new JarPackager(
                project.getClassesDir().getAbsolutePath(),
                new File(project.getClassesDir(), "classes.jar").getAbsolutePath()
            ).create();
            
            Task d8Task = new D8Task(context, project);
            d8Task.setProgressListener((kind, args) -> {
                publishProgress(args);
                status.onChanged(kind, args);
            });
            d8Task.configure();
            d8Task.start();*/
        } catch(Exception err) {
            status.onChanged(BuildKind.ERROR, "BuildTask: " + err.getMessage());
        	return BuildKind.ERROR;
        }
    	return BuildKind.SUCCESS;
    }
    
    @Override
    public void onProgressUpdate(String... progress) {
    
    }
    
    @Override
    public void onPostExecute(BuildTask.BuildKind result) {
        
    }
    
    public interface onBuildStatusChanged {
        void onChanged(BuildTask.BuildKind kind, String message);
    }
    
    public enum BuildKind {
        INFO,
        WARN,
        ERROR,
        SUCCESS
    }
    
}
