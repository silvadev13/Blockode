package dev.silvadev.build.project;
import java.io.File;

public class Project {
    
    private File rootDir;
    
    public Project(File mRootDir) {
        rootDir = mRootDir;
    }
    
    public File getRootDir(){
        return rootDir;
    }
    
    public File getOutDir() {
        return new File(getRootDir(), "build");
    }
    
    public File getBinDir() {
        return new File(getOutDir(), "bin");
    }
    
    public File getClassesDir() {
        return new File(getBinDir(), "classes");
    }
    
    public File getLibsDir() {
        return new File(getOutDir(), "libs");
    }
    
    public File getJavaDir() {
        return new File(getRootDir(), "src/main/java");
    }
    
}
