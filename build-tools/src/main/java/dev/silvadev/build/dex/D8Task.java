package dev.silvadev.build.dex;
import android.content.Context;
import android.os.Build;
import com.android.tools.r8.CompilationMode;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import dev.silvadev.build.BuildTask;
import dev.silvadev.build.Task;
import dev.silvadev.build.project.Project;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class D8Task extends Task {
    
    private Project project;
    private Context context;
    
    private int MIN_API_LEVEL = Build.VERSION_CODES.O;
    private CompilationMode COMPILATION_MODE = CompilationMode.DEBUG;
    
    public D8Task(Context context, Project project) {
        this.project = project;
        this.context = context;
    }
    
    @Override
    public void configure() {
        // TODO: Implement this method
    }
    
    @Override
    public void start() throws Exception {
        var classes = getClassFiles(project.getClassesDir());
        if(classes.isEmpty()) {
        	onProgressUpdate(BuildTask.BuildKind.ERROR, "No classes found");
            return;
        }
        
        D8.run(
            D8Command.builder()
            .setMinApiLevel(MIN_API_LEVEL)
            .setMode(COMPILATION_MODE)
            .addClasspathFiles(getClassPathDir(context).stream()
                .map(File::toPath)
                .collect(Collectors.toList())
            )
            .addProgramFiles(classes.stream()
                .map(File::toPath)
                .collect(Collectors.toList())
            )
            .setOutput(project.getBinDir().toPath(), OutputMode.DexIndexed)
            .build()
        );
        
        var libs = project.getLibsDir();
        if(libs.exists() && libs.isDirectory()) {
            var libsDex = new File(project.getOutDir(), "libs") {{
                if (exists()) mkdirs();
            }};
        	var filesJar = Arrays.stream(libs.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith("jar"))
                .collect(Collectors.toList());
                
            for(File jar : filesJar) {
            	if(libsDex.toPath().resolve(removeExtension(jar.getName()) + ".dex").toFile().exists()) {
            		onProgressUpdate(BuildTask.BuildKind.INFO, "Compiling library " + jar.getName());
                    compileJar(jar, libsDex);
            	}
            }
        }
    }
    
    private List<File> getClassFiles(File dir) {
        List<File> files = new ArrayList<>();
        File[] fileArr = dir.listFiles();
        if (fileArr == null) {
            return files;
        }
        
        for (File file : fileArr) {
            if (file.isDirectory()) {
                files.addAll(getClassFiles(file));
            } else {
                files.add(file);
            }
        }
        
        return files;
    }
    
    private String removeExtension(String fileName) {
        int point = fileName.lastIndexOf('.');
        if (point > 0 && point < fileName.length() - 1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }
    
    private void compileJar(File jar, File output) throws Exception {
    	D8.run(
            D8Command.builder()
            .setMinApiLevel(MIN_API_LEVEL)
            .setMode(COMPILATION_MODE)
            .addClasspathFiles(getClassPathDir(context).stream()
                .map(File::toPath)
                .collect(Collectors.toList())
            )
            .addProgramFiles(jar.toPath())
            .setOutput(output.toPath(), OutputMode.DexIndexed)
            .build()
        );
        Files.move(
            output.toPath().resolve("classes.dex"),
            output.toPath().resolve(removeExtension(jar.getName()) + ".dex")
        );
    }
    
}
