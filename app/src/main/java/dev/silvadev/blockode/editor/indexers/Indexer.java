package dev.silvadev.blockode.editor.indexers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.utils.FileUtil;
import com.tyron.javacompletion.JavaCompletions;
import com.tyron.javacompletion.options.JavaCompletionOptionsImpl;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.tyron.javacompletion.file.FileManager;
import com.tyron.javacompletion.file.PathUtils;
import com.tyron.javacompletion.file.SimpleFileManager;
import com.tyron.javacompletion.model.FileScope;
import com.tyron.javacompletion.model.Module;
import com.tyron.javacompletion.options.IndexOptions;
import com.tyron.javacompletion.parser.AstScanner;
import com.tyron.javacompletion.parser.ParserContext;
import com.tyron.javacompletion.parser.classfile.ClassModuleBuilder;
import com.tyron.javacompletion.project.Project;
import com.tyron.javacompletion.project.SimpleModuleManager;
import com.tyron.javacompletion.storage.IndexStore;
import java.util.zip.ZipOutputStream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class Indexer extends com.tyron.javacompletion.tool.Indexer {
    
	private int isIndexing=0;
	private String[] indexes;
    
	private String path;
	private JavaCompletions javaCompletions;
	
	public Indexer(){
		javaCompletions = new JavaCompletions();
	}
	
	public JavaCompletions getJavaCompletions(){
		return javaCompletions;
	}
	
	public boolean isIndexing(){
		return isIndexing>0;
	}
	
	private final ParserContext parserContext = new ParserContext();
	
	public String[] getIndexsFiles(){
		return indexes;
	}
	
	public Indexer indexFiles(Context context, EditorState editorState){
		isIndexing++;
        File javaDir = new File(ProjectManager.getSrcFile(editorState.project.scId), "java");
        
		try {
	    	ArrayList<String> jars=new ArrayList<>();
		    File index = new File(ProjectManager.getBinFile(editorState.project.scId), "index.json");
	    	File index2 = new File(ProjectManager.getBinFile(editorState.project.scId), "index2.json");
            File addition = new File(ProjectManager.getLibsFile(editorState.project.scId), "addition.jar");
		
	    	if((!index.exists()) || index.length() == 0){
	        	FileUtil.writeText(index.getAbsolutePath(), "");
	        	FileUtil.extractAssetFile(context, "completions/libs.jar", addition.getAbsolutePath());
		        unzipf(addition.getAbsolutePath(), new File(ProjectManager.getLibsFile(editorState.project.scId), "add").getAbsolutePath(), "");
		        jars.add(new File(ProjectManager.getLibsFile(editorState.project.scId), "add").getAbsolutePath());
		        run(jars, index.getAbsolutePath(), Collections.emptyList(), Collections.emptyList(), new File(ProjectManager.getLibsFile(editorState.project.scId), "add").getAbsolutePath());
		        FileUtil.deleteFile(new File(ProjectManager.getLibsFile(editorState.project.scId), "add").getAbsolutePath());
		        jars.clear();
		    }
            
		    jars.add(javaDir.getAbsolutePath());
		    run(jars, index2.getAbsolutePath(), Collections.emptyList(), Collections.emptyList(), javaDir.getAbsolutePath());
		    indexes = new String[]{index.getAbsolutePath(),index2.getAbsolutePath()};
		} catch(Exception exception){
			FileUtil.writeText(new File(ProjectManager.getLogFile(editorState.project.scId), "autocompletion_error.txt").getAbsolutePath(), "Failed to index files : \n"  + Log.getStackTraceString(exception));
			return this;
		}
		
		ArrayList<String> list = new ArrayList<>();
		File logPath = new File(ProjectManager.getLogFile(editorState.project.scId), "autocompletion.log");
        
		if(!logPath.exists()) FileUtil.writeText(logPath.getAbsolutePath(), "");
		    JavaCompletionOptionsImpl options = new JavaCompletionOptionsImpl(
		        logPath.getAbsolutePath(),
		        Level.ALL,
		        java.util.Collections.emptyList(),
		        list
		);
		
		javaCompletions.initialize(URI.create("file://" + javaDir.getAbsolutePath()), options);
		try {
			IndexerUtil.loadJdk(javaCompletions.getProject(), context, indexes);
		} catch(Exception e){
			Log.e("EditorDebug", "JDK ERROR: " + Log.getStackTraceString(e));
		}
		isIndexing--;
		return this;
	}
	
	public void getClasses(ArrayList<String> arrayList, String path){
		File[] fileArray = new File(path).listFiles();
		for(File file : fileArray)
			if(file.isDirectory())
				getClasses(arrayList, file.getAbsolutePath());
					else if(file.getName().endsWith(".class")||file.getName().endsWith(".java"))
						arrayList.add(file.getAbsolutePath());
	}
	
	public void run(
	     List<String> inputPaths,
	     String outputPath,
	     List<String> ignorePaths,
	     List<String> dependIndexFiles,
	     String root) {
		     Path rootPath = new File(root).toPath();
		     // Do not initialize the project. We handle the files on our own.
		     SimpleModuleManager moduleManager = new SimpleModuleManager();
		     Project project = new Project(moduleManager, moduleManager.getFileManager());
		     for (String inputPath : inputPaths) {
		    	Path path = Paths.get(inputPath);
		    	// Do not use module manager's file manager because we need to setup root
		   	// path and ignore paths per directory.
		    	FileManager fileManager = new SimpleFileManager(path, ignorePaths);
		    	ClassModuleBuilder classModuleBuilder = new ClassModuleBuilder(moduleManager.getModule());
		    	ImmutableMap<String, Consumer<Path>> handlers =
		    	ImmutableMap.<String, Consumer<Path>>of(
		    	".class",
		    	classModuleBuilder::processClassFile,
		    	".java",
		    	subpath -> {
                addJavaFile(subpath, moduleManager.getModule(), fileManager);
                });
			 if (Files.isDirectory(path)) {
		    	System.out.println("Indexing directory: " + inputPath);
				PathUtils.walkDirectory(
			    	path,
			    	handlers,
				    /* ignorePredicate= */ fileManager::shouldIgnorePath);
		 	} else if (inputPath.endsWith(".jar") || inputPath.endsWith(".srcjar")) {
				System.out.println("Indexing JAR file: " + inputPath);
				try {
					PathUtils.walkDirectory(rootPath,
					handlers,
					/* ignorePredicate= */ subpath -> false);
				} catch (Exception t) {
					throw new RuntimeException(t);
				}
		 	}
		}
	    	 for (String dependIndexFile : dependIndexFiles) {
			    project.loadTypeIndexFile(dependIndexFile);
	     	}
		     System.out.println("Writing index file to " + outputPath);
		     new IndexStore().writeModuleToFile(moduleManager.getModule(), Paths.get(outputPath));
	}
	
	private void addJavaFile(Path path, Module module, FileManager fileManager) {
		Optional<CharSequence> content = fileManager.getFileContent(path);
		if (content.isPresent()) {
			FileScope fileScope =
			new AstScanner(IndexOptions.NON_PRIVATE_BUILDER.build())
			.startScan(
			    parserContext.parse(path.toString(), content.get()),
			    path.toString(),
		    	content.get()
            );
			module.addOrReplaceFileScope(fileScope);
		}
	}
    
    public static void unzipf(String path,String to,String password) throws Exception {
			if(new ZipFile(path).isEncrypted()) new ZipFile(path,password.toCharArray()).extractAll(to);
				else new ZipFile(path).extractAll(to);
		}
	
}