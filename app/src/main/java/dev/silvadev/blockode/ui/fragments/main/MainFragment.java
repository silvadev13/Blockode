package dev.silvadev.blockode.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.databinding.FragmentMainBinding;
import dev.silvadev.blockode.ui.fragments.main.components.CreateProjectDialog;
import dev.silvadev.blockode.ui.fragments.projects.project.ProjectsViewModel;
import dev.silvadev.blockode.ui.fragments.projects.ProjectsFragment;
import dev.silvadev.blockode.R;

public class MainFragment extends BaseFragment {
    
    @NonNull private FragmentMainBinding binding;
    
    @Override
    @NonNull
    protected View bindLayout() {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        binding.createNew.setOnClickListener(
            v -> {
                final var cpt = new CreateProjectDialog(getContext());
                cpt.show();
            }
        );
        binding.projects.setOnClickListener(
            v -> {
                showFragment(R.id.content, new ProjectsFragment(), "projectsFragment");
            }
        );
    }
    
}
