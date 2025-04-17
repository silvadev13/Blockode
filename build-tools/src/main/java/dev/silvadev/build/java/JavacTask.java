package dev.silvadev.build.java;
import android.content.Context;
import android.util.Log;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import dev.silvadev.build.BuildTask;
import dev.silvadev.build.Task;
import dev.silvadev.build.project.Project;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;

public class JavacTask extends Task {
    
    private Project project;
    private Context context;
    
    private JavacTool tool;
    private DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    private JavacFileManager fileManager;
    
    public JavacTask(Context context, Project project) {
        this.project = project;
        this.context = context;
    }
    
    @Override
    public void configure() {
        tool = JavacTool.create();
        fileManager = tool.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.defaultCharset());
    }
    
    @Override
    public void start() throws Exception {
        var version = 17;
        var output = project.getClassesDir();
        
        final var outputStream = new OutputStream() {
            
            private StringBuilder sb = new StringBuilder();
            
            @Override
            public void write(int b) {
                sb.append((char)b);
            }
            
        };
        
        var javaFiles = getJavaFiles(project.getJavaDir());
        if(javaFiles.isEmpty()) {
        	onProgressUpdate(BuildTask.BuildKind.INFO, "No java files found, skipping.");
            return;
        }
        
        var size = javaFiles.size();
        var fileOrFiles = size > 0 ? "files" : "file";
        onProgressUpdate(BuildTask.BuildKind.INFO, "Compiling " + size + " java " + fileOrFiles);
        
        var javaFileObjects = javaFiles.stream()
            .map(file -> new SimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean arg0) throws IOException {
                    return readText(file.getAbsolutePath());
                }
            })
            .collect(Collectors.toList());
            
         fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(output));
         fileManager.setLocation(StandardLocation.PLATFORM_CLASS_PATH, getClassPathDir(context));
         fileManager.setLocation(StandardLocation.CLASS_PATH, getClassPath());
         fileManager.setLocation(StandardLocation.SOURCE_PATH, javaFiles);
         
         if(getClassPath() == null) {
         	Log.d("EditorDebug", "ClassPaths is null");
         }
         
         if(javaFileObjects == null) {
         	Log.d("EditorDebug", "JavaFileObjects is null");
         }
         
         var options = Arrays.asList(
             "-XDstringConcat=inline",
             "-proc:none",
             "-source",
             String.valueOf(version),
             "-target",
             String.valueOf(version)
         );
         
         var task = tool.getTask(
             new PrintWriter(outputStream),
             fileManager,
             diagnostics,
             options,
             null,
             javaFileObjects
         );
         
         task.call();
         
         for(var diagnostic : diagnostics.getDiagnostics()) {
         	var message = new StringBuilder();
             if (diagnostic.getSource() != null) message.append(diagnostic.getSource().getName() + ":" + diagnostic.getLineNumber() + ": ");
             message.append(diagnostic.getMessage(Locale.getDefault()));
             
             var kind = diagnostic.getKind();
             if(kind == Diagnostic.Kind.ERROR || diagnostic.getKind() == Diagnostic.Kind.OTHER) {
             	onProgressUpdate(BuildTask.BuildKind.ERROR, message.toString());
             } else if(kind == Diagnostic.Kind.NOTE || kind == Diagnostic.Kind.WARNING || kind == Diagnostic.Kind.MANDATORY_WARNING) {
             	onProgressUpdate(BuildTask.BuildKind.WARN, message.toString());
             } else {
                 onProgressUpdate(BuildTask.BuildKind.INFO, message.toString());
             }
         }
    }
    
    public List<File> getClassPath() {
        List<File> classpath = Arrays.asList(project.getClassesDir());
        File libDir = project.getLibsDir();
        if(libDir.exists() && libDir.isDirectory()) {
        	classpath.addAll(
                Arrays.asList(
                    libDir.listFiles(file -> file.getName().endsWith(".jar"))
                )
            );
        }
        return classpath;
    }
    
    public List<File> getJavaFiles(File input) {
    	List<File> javaFiles = new ArrayList<>();
        
        if(input.isDirectory()) {
        	File[] contents = input.listFiles();
            if(contents != null) {
            	for(File child : contents) {
            		javaFiles.addAll(getJavaFiles(child));
            	}
            }
        } else if(input.getName().endsWith(".java")) {
            javaFiles.add(input);
        }
        
        return javaFiles;
    }
    
    public static String readText(String path) {
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(path)) {
            char[] buff = new char[1024];
            int length;

            while ((length = fr.read(buff)) > 0) {
                sb.append(new String(buff, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
}
