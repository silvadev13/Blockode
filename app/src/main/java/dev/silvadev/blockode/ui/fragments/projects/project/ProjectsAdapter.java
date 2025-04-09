package dev.silvadev.blockode.ui.fragments.projects.project;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.databinding.LayoutProjectBinding;
import java.io.File;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.utils.function.Listener;

public class ProjectsAdapter extends ListAdapter<File, ProjectsAdapter.ProjectsAdapterViewHolder> {
  private Listener<ProjectBean> onProjectClickListener;

  public ProjectsAdapter() {
    super(new ProjectsAdapterDiffUtil());
  }

  @Override
  public ProjectsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int parentType) {
    return new ProjectsAdapterViewHolder(
        LayoutProjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  @NonNull
  public void onBindViewHolder(@NonNull ProjectsAdapterViewHolder holder, int position) {
    var item = getItem(position); // project folder
    // try load project data based in folder name.
    var project = ProjectManager.getProjectByScId(item.getName());
    assert project != null;
    holder.binding.title.setText(project.basicInfo.name);
    holder.binding.summary.setText(project.basicInfo.mainClassPackage);
    holder.binding.card.setOnClickListener(
        v -> {
          if (onProjectClickListener != null) onProjectClickListener.call(project);
        });
  }

  public void setOnProjectClick(final Listener<ProjectBean> onProjectClickListener) {
    this.onProjectClickListener = onProjectClickListener;
  }

  public static class ProjectsAdapterViewHolder extends RecyclerView.ViewHolder {
    private LayoutProjectBinding binding;

    public ProjectsAdapterViewHolder(@NonNull LayoutProjectBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public static class ProjectsAdapterDiffUtil extends DiffUtil.ItemCallback<File> {
    @Override
    public boolean areItemsTheSame(@NonNull File oldItem, @NonNull File newItem) {
      return oldItem.getName().equals(newItem.getName());
    }

    @Override
    public boolean areContentsTheSame(@NonNull File oldItem, @NonNull File newItem) {
      return oldItem.equals(newItem);
    }
  }
}
