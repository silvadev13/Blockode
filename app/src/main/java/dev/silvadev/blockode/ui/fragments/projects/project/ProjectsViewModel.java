package dev.silvadev.blockode.ui.fragments.projects.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.utils.PrintUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectsViewModel extends ViewModel {
  private final MutableLiveData<List<File>> projects = new MutableLiveData<>();

  public final void fetch() {
    var ogFiles = ProjectManager.getProjectsFile().listFiles();
    PrintUtil.print(ogFiles);
    if (ogFiles == null) return;
    projects.setValue(toFiles(Arrays.asList(ogFiles)));
  }

  private final List<File> toFiles(final List<java.io.File> ogFiles) {
    var toReturnList = new ArrayList<File>();
    ogFiles.forEach(
        ogFile -> {
          if (ogFile.isDirectory()) toReturnList.add(new File(ogFile.getAbsolutePath()));
        });
    return toReturnList;
  }

  public final LiveData<List<File>> getProjects() {
    return projects;
  }
  
}
