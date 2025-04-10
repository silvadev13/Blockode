package dev.silvadev.blockode.ui.fragments.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.databinding.FragmentProjectsBinding;
import dev.silvadev.blockode.ui.fragments.editor.EditorFragment;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.editor.core.ProjectHolder;
import dev.silvadev.blockode.ui.fragments.main.components.CreateProjectDialog;
import dev.silvadev.blockode.ui.fragments.projects.project.ProjectsAdapter;
import dev.silvadev.blockode.ui.fragments.projects.project.ProjectsViewModel;
import dev.silvadev.blockode.R;
public class ProjectsFragment extends BaseFragment {
	
    @NonNull private FragmentProjectsBinding binding;
    
    private ProjectsViewModel projectsViewModel;
    private ProjectsAdapter projectsAdapter;
    
    @Override
    protected View bindLayout() {
        binding = FragmentProjectsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(
            v -> {
                requireActivity().onBackPressed();
            }
        );
        projectsViewModel = new ViewModelProvider(getActivity()).get(ProjectsViewModel.class);
        projectsAdapter = new ProjectsAdapter();
        projectsAdapter.setOnProjectClick(this::openProject);
        projectsViewModel.fetch();
        projectsViewModel.getProjects().observe(this, projects -> {
            if(!projects.isEmpty()) {
            	projectsAdapter.submitList(projects);
                binding.list.setVisibility(View.VISIBLE);
                binding.card.setVisibility(View.GONE);
                binding.list.setAdapter(projectsAdapter);
            } else {
                binding.list.setVisibility(View.GONE);
                binding.card.setVisibility(View.VISIBLE);
                cardClick();
            }
        });
    }
    
    public void openProject(final ProjectBean projectBean) {
        final var editorState = new EditorState();
        editorState.project = projectBean;
        ProjectHolder.setEditorState(editorState);
        showFragment(R.id.content, new EditorFragment(), "editorFragment");
    }
    
    public void cardClick() {
        binding.card.setOnClickListener(
            v -> {
                final var cpt = new CreateProjectDialog(getContext());
                cpt.show();
                cpt.setOnDismissListener(dialog -> projectsViewModel.fetch());
            }
        );
    }
    
}