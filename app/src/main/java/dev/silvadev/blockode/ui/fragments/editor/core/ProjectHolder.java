package dev.silvadev.blockode.ui.fragments.editor.core;

import dev.silvadev.blockode.model.FileViewModel;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;

public class ProjectHolder {
    
    public static EditorState editorState;
    public static ProjectManager projectManager;
    public static FileViewModel fileViewModel;
    
    /* Method Setters */
    
    public static void setEditorState(EditorState nEditorState) {
    	editorState = nEditorState;
    }
    
    public static void setProjectManager(ProjectManager nProjectManager) {
    	projectManager = nProjectManager;
    }
    
    public static void setFileViewModel(FileViewModel nFileViewModel) {
        fileViewModel = nFileViewModel;
    }
    
    /* Method Getters */
    
    public static EditorState getEditorState() {
    	return editorState;
    }
    
    public static ProjectManager getProjectManager() {
    	return projectManager;
    }
    
    public static FileViewModel getFileViewModel() {
    	return fileViewModel;
    }
}
