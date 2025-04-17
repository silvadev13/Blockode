package dev.silvadev.blockode.editor.analyzers;

import dev.silvadev.blockode.utils.FileUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class Cache {
    public static HashMap<String, JavaFileObject> cacheMap = new HashMap<>();
    
    public static JavaFileObject saveCache(File file) {
        var obj = new SimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.SOURCE) {
            private long lastModified = file.lastModified();
            
            @Override
            public CharSequence getCharContent(boolean arg0) throws IOException {
                return FileUtil.readFileIfExist(file.getAbsolutePath());
            }
            
            @Override
            public long getLastModified() {
            	return lastModified;
            }
        };
        cacheMap.put(file.getAbsolutePath(), obj);
        return obj;
    }
    
    public static void saveCache(JavaFileObject obj) {
    	cacheMap.put(obj.getName(), obj);
    }
    
    public static JavaFileObject getCache(File key) {
    	return cacheMap.get(key.getAbsolutePath());
    }
}
