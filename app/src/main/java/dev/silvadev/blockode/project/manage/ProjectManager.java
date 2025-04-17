package dev.silvadev.blockode.project.manage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import dev.silvadev.blockode.Blockode;
import dev.silvadev.blockode.base.Contextualizable;
import dev.silvadev.blockode.beans.BlockBean;
import dev.silvadev.blockode.beans.ProjectBasicInfoBean;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.beans.VariableBean;
import java.io.File;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.utils.GsonUtil;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager extends Contextualizable {
  private String scId;

  public ProjectManager(@NonNull final Context context) {
    this(context, null);
  }

  public ProjectManager(@NonNull final Context context, final String scId) {
    super(context);
    this.scId = scId;
  }

  @Nullable
  public final ProjectBean getCurrentProject() {
    return getProjectByScId(scId);
  }

  /**
   * Creates and returns a ProjectBean based in files by scId
   *
   * @param scId The id of project to be searched
   */
  @Nullable
  public static final ProjectBean getProjectByScId(final String scId) {
    var basicInfoFileJsonType = new TypeToken<ProjectBasicInfoBean>() {}.getType();
    var basicInfoJsonContent = FileUtil.readFile(getBasicInfoFile(scId).getAbsolutePath(), false);
    var basicInfo = GsonUtil.getGson().fromJson(basicInfoJsonContent, basicInfoFileJsonType);
    var variablesFileJsonType = new TypeToken<List<VariableBean>>() {}.getType();
    var variablesFileJsonContent =
        FileUtil.readFile(getVariablesFile(scId).getAbsolutePath(), false);
    var blocksFileJsonType = new TypeToken<List<BlockBean>>() {}.getType();
    var blocksFileJsonContent = FileUtil.readFile(getBlocksFile(scId).getAbsolutePath(), false);
    var variables = GsonUtil.getGson().fromJson(variablesFileJsonContent, variablesFileJsonType);
    var blocks = GsonUtil.getGson().fromJson(blocksFileJsonContent, blocksFileJsonType);
    var toReturnProject = new ProjectBean();
    toReturnProject.scId = scId;
    toReturnProject.basicInfo = (ProjectBasicInfoBean) basicInfo;
    toReturnProject.variables = (ArrayList<VariableBean>) variables;
    toReturnProject.blocks = (ArrayList<BlockBean>) blocks;
    return toReturnProject;
  }

  /**
   * Creates nescessary files of project
   *
   * @param project The instance of ProjectBean with data to be created.
   */
  public static final void createProjectByBean(@NonNull final ProjectBean project) {
    var projectRootDir = new File(getProjectsFile(), project.scId).getAbsolutePath();
    var mainFile = new File(getSrcFile(project.scId), "java/" + project.basicInfo.packageName.replaceAll("\\.", "/") + "/Main.java");
    String mainCode = 
    "package " + project.basicInfo.packageName + ";\n\n" +
    "public class Main {\n" +
    "    \n" +
    "    public static void main(String[] args) {\n" +
    "        System.out.println(\"Hello, BlockodeUser!\");\n" +
    "    }\n" +
    "    \n" +
    "}";
    var basicInfoFileJson = GsonUtil.getGson().toJson(project.basicInfo);
    var variablesFileJson = GsonUtil.getGson().toJson(project.variables);
    var blocksFileJson = GsonUtil.getGson().toJson(project.blocks);
    FileUtil.makeDir(projectRootDir);
    FileUtil.writeText(mainFile.getAbsolutePath(), mainCode);
    FileUtil.writeText(getBasicInfoFile(project.scId).getAbsolutePath(), basicInfoFileJson);
    FileUtil.writeText(getVariablesFile(project.scId).getAbsolutePath(), variablesFileJson);
    FileUtil.writeText(getBlocksFile(project.scId).getAbsolutePath(), blocksFileJson);
  }
  
  public static final String generateScId() {
      int index = 100;
      File[] projectsArray = getProjectsFile().listFiles();
      if (projectsArray == null) return String.valueOf(0);
      List<File> dirs = new ArrayList<>();
      for(File file : projectsArray) {
      	if(file.exists() && file.isDirectory()) {
      		dirs.add(file);
      	}
      }
  	return String.valueOf(index + (dirs.size() + 1));
  }

  /** Folder where all projects are stored */
  public static final File getProjectsFile() {
      File file = new File(Blockode.getPublicFolderFile(), "projects/");
      if(!file.exists()) {
    	file.mkdirs();
      }
      return file;
  }
  
  public static final File getProjectRootFile(String scId) {
      return new File(getProjectsFile(), scId);
  }
  
  /** Folder where src of project are stored */
  public static final File getSrcFile(String scId) {
      File file = new File(getProjectsFile(), scId + "/src/main/");
      if(!file.exists()) {
    	file.mkdirs();
      }
      return file;
  }
  
  /** Folder where build of project are stored */
  public static final File getOutputFile(String scId) {
      File file = new File(getProjectsFile(), scId + "/build/");
      if(!file.exists()) {
    	file.mkdirs();
      }
  	return file;
  }
  
  /** Folder where logs of completions are stored */
  public static final File getLogFile(String scId) {
      File file = new File(getOutputFile(scId), "log/");
      if(!file.exists()) {
    	file.mkdirs();
      }
  	return file;
  }
  
  /** Folder where bin of build are stored */
  public static final File getBinFile(String scId) {
      File file = new File(getOutputFile(scId), "bin/");
      if(!file.exists()) {
    	file.mkdirs();
      }
      return file;
  }
  
  /** Folder where libs of project are stored */
  public static final File getLibsFile(String scId) {
      File file = new File(getOutputFile(scId), "libs/");
      if(!file.exists()) {
    	file.mkdirs();
      }
  	return file;
  }

  /** The file where basic info of project are stored, like name, packageName */
  public static final File getBasicInfoFile(final String scId) {
    return new File(getProjectsFile(), scId + "/.blockode/basic_info.json");
  }

  /** The file where variables are stored */
  public static final File getVariablesFile(final String scId) {
    return new File(getProjectsFile(), scId + "/.blockode/variables.json");
  }

  /** The file where blocks are stored */
  public static final File getBlocksFile(final String scId) {
    return new File(getProjectsFile(), scId + "/.blockode/blocks.json");
  }

  @Nullable
  public String getScId() {
    return this.scId;
  }

  public void setScId(final String scId) {
    this.scId = scId;
  }
}
