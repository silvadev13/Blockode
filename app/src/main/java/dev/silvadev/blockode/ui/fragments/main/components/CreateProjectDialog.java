package dev.silvadev.blockode.ui.fragments.main.components;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import dev.silvadev.blockode.R;
import dev.silvadev.blockode.beans.ProjectBasicInfoBean;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.databinding.DialogCreateProjectBinding;
import dev.silvadev.blockode.project.manage.ProjectManager;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CreateProjectDialog extends BottomSheetDialog {
  private final DialogCreateProjectBinding binding;

  public CreateProjectDialog(@NonNull Context context) {
    super(context);
    binding = DialogCreateProjectBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    binding.next.setOnClickListener(v -> onNext());
    binding.cancel.setOnClickListener(v -> dismiss());
  }

  private void onNext() {
    var project = new ProjectBean();
    var basicInfo = new ProjectBasicInfoBean();
    basicInfo.name = Objects.requireNonNull(binding.projectName.getText()).toString();
    basicInfo.packageName = Objects.requireNonNull(binding.projectPackage.getText()).toString();
    project.scId = String.valueOf(ProjectManager.generateScId()); // random scid for now
    project.basicInfo = basicInfo;
    project.variables = new ArrayList<>();
    project.blocks = new ArrayList<>();

    if (!((binding.projectName.getText()).toString().isEmpty()
        || (binding.projectPackage.getText()).toString().isEmpty())) {
      ProjectManager.createProjectByBean(project);
      dismiss();
    } else {
      Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
    }
  }
}
