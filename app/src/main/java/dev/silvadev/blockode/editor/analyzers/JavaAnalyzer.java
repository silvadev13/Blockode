package dev.silvadev.blockode.editor.analyzers;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.utils.FileUtil;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion;
import io.github.rosemoe.sora.lang.diagnostic.Quickfix;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class JavaAnalyzer {

    private CodeEditor editor;
    private EditorState editorState;
    
    private List<String> args = new ArrayList<>() {{
        add("-XDstringConcat=inline");
        add("-XDcompilePolicy=byfile");
        add("-XD-Xprefer=source");
        add("-XDide");
        add("-XDsuppressAbortOnBadClassFile");
        add("-XDshould-stop.at=GENERATE");
        add("-XDdiags.formatterOptions=-source");
        add("-XDdiags.layout=%L%m|%L%m|%L%m");
        add("-XDbreakDocCommentParsingOnError=false");
        add("-Xlint:cast");
        add("-Xlint:deprecation");
        add("-Xlint:empty");
        add("-Xlint:fallthrough");
        add("-Xlint:finally");
        add("-Xlint:path");
        add("-Xlint:unchecked");
        add("-Xlint:varargs");
        add("-Xlint:static");
        add("-proc:none");
    }};
    private List<String> options;
    private DiagnosticCollector<JavaFileObject> diagnostic = new DiagnosticCollector<>();
    
    private JavacTool tool;
    private JavacFileManager standardFileManager;
    
    public JavaAnalyzer(CodeEditor editor, EditorState editorState, List<String> options) {
    	this.editor = editor;
        this.editorState = editorState;
        this.options = options;
        
        tool = JavacTool.create();
        standardFileManager = tool.getStandardFileManager(diagnostic, Locale.getDefault(), Charset.defaultCharset());
        
        try {
        	standardFileManager.setLocation(StandardLocation.PLATFORM_CLASS_PATH, FileUtil.listOf(FileUtil.getClassPathDir(editor.getContext())));
            if(!getBinDir().exists()) {
            	FileUtil.makeDir(getBinDir());
            }
            standardFileManager.setLocation(StandardLocation.CLASS_OUTPUT, FileUtil.listOf(getBinDir()));
        } catch(Exception err) {
        	err.printStackTrace();
        }
    }
    
    public void analyze() {
    	var version = 17;
        var filesToCompile = getJavaFiles(getSrcDir());
        
        try {
        	standardFileManager.setLocation(StandardLocation.CLASS_PATH, getClassPath());
            standardFileManager.autoClose = false;
        } catch(Exception err) {
        	err.printStackTrace();
        }
        
        var copy = new ArrayList<>(args) {{
            add("-source");
            add(String.valueOf(version));
            add("-target");
            add(String.valueOf(version));
            add("-bootclasspath");
            add(FileUtil.getClassPathDir(editor.getContext())
            addAll(options);
        }};
        
        try {
        	JavacTask task = tool.getTask(new PrintWriter(System.out), standardFileManager, diagnostic, copy, null, filesToCompile);
            task.parse();
            task.analyze();
            if(diagnostic.getDiagnostics().isEmpty()) {
                task.generate().forEach(Cache::saveCache);
            }
        } catch(Exception err) {
        	err.printStackTrace();
        }
    }
    
    public void reset() {
    	diagnostic = new DiagnosticCollector<>();
    }
    
    public List<DiagnosticRegion> getDiagnostics() {
    	var diagnostics = diagnostic.getDiagnostics();
        var problems = new ArrayList<DiagnosticRegion>();
        
        try {
        	for(var it : diagnostics) {
        		if (it.getSource() == null) continue;
                var severity = it.getKind() == Diagnostic.Kind.ERROR ? DiagnosticRegion.SEVERITY_ERROR : DiagnosticRegion.SEVERITY_WARNING;
                var message = it.getMessage(Locale.getDefault());
                var quickFixes = new ArrayList<Quickfix>();
                if(it.getCode() == "compiler.err.cant.resolve.location") {
                	var symbol = String.valueOf(it.getSource().getCharContent(true)).substring((int)it.getStartPosition(), (int)it.getEndPosition());
                    /** coming soon */
                }
                
                problems.add(
                    new DiagnosticRegion(
                        (int)it.getStartPosition(),
                        (int)it.getEndPosition(),
                        severity,
                        0,
                        new DiagnosticDetail(message, null, quickFixes, null)
                    )
                );
        	}
        } catch(Exception err) {
        	err.printStackTrace();
            Log.e("EditorDebug", "Diagnostic Failed Error: " + err.getMessage());
        }
        
        return problems;
    }
    
    public List<File> getClassPath() {
    	List<File> classpath = new ArrayList<>();
        
        classpath.add(new File(getBinDir(), "classes"));
        FileUtil.listOf(getLibsDir()).forEach(it -> {
            if(FileUtil.getFileExtension(it.getName()).equals("jar")) {
            	classpath.add(it);
            }
        });
        
        FileUtil.listOf(new File(getBinDir(), "classes")).forEach(it -> {
            if(FileUtil.getFileExtension(it.getName()).equals("class")) {
            	if(Cache.getCache(it) != null && Cache.getCache(it).getLastModified() == it.lastModified()) {
            		classpath.add(it);
            	}
            }
        });
        
        return classpath;
    }
    
    public List<JavaFileObject> getJavaFiles(File input) {
    	List<JavaFileObject> javaFiles = new ArrayList<>();
        
        if(input.isDirectory()) {
        	File[] contents = input.listFiles();
            if(contents != null) {
            	for(File child : contents) {
            		javaFiles.addAll(getJavaFiles(child));
            	}
            }
        } else if(FileUtil.getFileExtension(input.getName()).equals("java")) {
        	var cache = Cache.getCache(input);
            if(cache == null || cache.getLastModified() < input.lastModified()) {
                javaFiles.add(Cache.saveCache(input));
            }
        }
        
        return javaFiles;
    }
    
    public File getBinDir() {
        return ProjectManager.getBinFile(editorState.project.scId);
    }
    
    public File getLibsDir() {
        return ProjectManager.getLibsFile(editorState.project.scId);
    }
    
    public File getSrcDir() {
        return ProjectManager.getSrcFile(editorState.project.scId);
    }
    
}
