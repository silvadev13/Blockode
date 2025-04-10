package dev.silvadev.blockode.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileViewModel extends ViewModel {
    
    private final MutableLiveData<List<File>> files = new MutableLiveData<>(new ArrayList<>());
    
    private final MutableLiveData<Integer> _currentPosition = new MutableLiveData<>(-1);
    public LiveData<Integer> currentPosition = _currentPosition;
    
    public File getCurrentFile() {
        List<File> fileList = files.getValue();
        int pos = currentPosition.getValue();
        
        if(fileList != null && pos != 0 && pos >= 0 && pos < fileList.size()) {
        	return fileList.get(pos);
        }
        
    	return null;
    }
    
    public LiveData<List<File>> getFiles() {
        return files;
    }
    
    public void setCurrentPosition(int pos) {
    	_currentPosition.setValue(pos);
    }
    
    public void addFile(File file) {
    	int index = files.getValue() != null ? files.getValue().indexOf(file) : -1;
        if(index == -1) {
        	List<File> currentFiles = files.getValue();
            if(currentFiles != null) {
            	List<File> updatedFiles = new ArrayList<>(currentFiles);
                updatedFiles.add(file);
                files.setValue(updatedFiles);
                setCurrentPosition(files.getValue().size() - 1);
            }
        } else {
        	setCurrentPosition(index);
        }
    }
    
    public void removeFile(int pos) {
    	List<File> currentFiles = files.getValue();
        if(currentFiles != null) {
        	List<File> updatedFiles = new ArrayList<>(currentFiles);
            updatedFiles.remove(pos);
            files.setValue(updatedFiles);
        }
        
        if(currentPosition.getValue() == pos) {
        	if(files.getValue().isEmpty()) {
        		setCurrentPosition(-1);
        	}
        }
        
        if(currentPosition.getValue() > pos) {
        	setCurrentPosition(currentPosition.getValue() - 1);
        }
    }
    
    public void removeOthers(File file) {
    	if(files.getValue() != null) {
    		if(files.getValue().size() > 1) {
    			files.setValue(new ArrayList<>(List.of(file)));
                setCurrentPosition(0);
    		}
    	}
    }
    
    public void removeAll() {
    	if(files.getValue() != null) {
    		files.setValue(new ArrayList<>());
            setCurrentPosition(-1);
    	}
    }
    
}
