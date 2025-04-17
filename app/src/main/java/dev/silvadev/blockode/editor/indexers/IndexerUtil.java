package dev.silvadev.blockode.editor.indexers;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.tyron.javacompletion.project.ModuleManager;
import com.tyron.javacompletion.project.Project;
import com.tyron.javacompletion.storage.IndexStore;
import dev.silvadev.blockode.utils.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class IndexerUtil {
    
    public static Indexer indexer;
    
	public static ModuleManager getModule(Project project){
		try {
			Field f = project.getClass().getDeclaredField("moduleManager");
			f.setAccessible(true);
			return (ModuleManager)f.get(project);
		} catch(Exception exception){
			Log.e("getting module", Log.getStackTraceString(exception));
			return null;
		}
	}
	
	public static void loadFile(Project project,String path){
		try {
			InputStream inputStream = new FileInputStream(path);
			loadStream(project, inputStream);
		} catch(Exception exception){
			Log.e("loading file error",Log.getStackTraceString(exception));
		}
	}
	
	public static void loadStream(Project project,InputStream stream){
		InputStreamReader reader = new InputStreamReader(stream);
		getModule(project).addDependingModule(new IndexStore().readModule(reader));
	}
	
	public static void loadJdk(Project project, Context context, String... other) throws Exception { 
        InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(FileUtil.getPackageDataDir(context), "index.json")));
		IndexStore indexStore = new IndexStore();
		ModuleManager manager = getModule(project);
		manager.addDependingModule(indexStore.readModule(reader));
		for(String s:other)
				manager.addDependingModule(indexStore.readModule(new InputStreamReader(new FileInputStream(new File(s)))));
	}
	
	public static InputStreamReader getInputStream(String file) throws Exception {
		return new InputStreamReader(new FileInputStream(file));
	}
    
    public static void setIndexer(Indexer nIndexer) {
        indexer = nIndexer;
    }
    
    public static Indexer getIndexer() {
        return indexer;
    }
}