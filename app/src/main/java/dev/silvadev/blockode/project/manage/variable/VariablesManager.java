package dev.silvadev.blockode.project.manage.variable;

import android.content.Context;
import androidx.annotation.NonNull;
import dev.silvadev.blockode.base.Contextualizable;
import dev.silvadev.blockode.beans.VariableBean;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.utils.GsonUtil;
import java.util.ArrayList;
import java.util.List;

public class VariablesManager extends Contextualizable {

  private String scId;
  private List<VariableBean> variables;

  public VariablesManager(@NonNull final Context context) {
    this(context, null);
  }

  public VariablesManager(@NonNull final Context context, final String scId) {
    super(context);
    variables = new ArrayList<>();
    this.scId = scId;
    readVariables();
  }

  /**
   * Add new variable in list and save
   *
   * @param variable: The Bean of Variable with info
   * @see VariableBean
   */
  public void addVariable(final VariableBean variable) {
    variables.add(variable);
    saveVariables();
  }

  /*
   * Return only variables with requested type.
   *
   * @param type: The Type of Variables
   */
  public List<VariableBean> getVariablesByType(final int type) {
    List<VariableBean> result = new ArrayList<>();
    variables.forEach(
        variable -> {
          if (variable.type == type) {
            result.add(variable);
          }
        });
    return result;
  }

  /*
   * Read and return all variables of project from file.
   *
   * @ see getVariablesFile()
   */
  public List<VariableBean> readVariables() {
    var variables = ProjectManager.getProjectByScId(scId).variables;
    return variables;
  }

  /**
   * Save all variables in file
   *
   * @see getVariablesFile()
   */
  public void saveVariables() {
    var json = GsonUtil.getGson().toJson(variables);
    FileUtil.writeText(ProjectManager.getVariablesFile(scId).getAbsolutePath(), json);
  }

  public void setScId(final String scId) {
    this.scId = scId;
    readVariables(); // read variables again if scId changes
  }

  public String getScId() {
    return scId;
  }

  public List<VariableBean> getVariables() {
    readVariables();
    return this.variables;
  }

  public void setVariables(List<VariableBean> variables) {
    this.variables = variables;
  }
}
