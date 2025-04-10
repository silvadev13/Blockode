package dev.silvadev.blockode.ui.fragments.editor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import dev.silvadev.blockode.databinding.FragmentEditorBinding;
import dev.silvadev.blockode.model.FileViewModel;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.activities.main.MainActivity;
import dev.silvadev.blockode.ui.fragments.editor.adapter.EditorAdapter;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.R;
import dev.silvadev.blockode.ui.fragments.editor.core.ProjectHolder;
import java.io.File;
import java.util.List;

public class EditorFragment extends BaseFragment {
    
    @NonNull private FragmentEditorBinding binding;
    
    @Nullable public EditorState editorState;
    private EditorAdapter editorAdapter;
    
    public ProjectManager projectManager;
    private FileViewModel fileViewModel;
    
    @Override
    protected View bindLayout() {
        binding = FragmentEditorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        setupData();
        setupViewModels();
        setupDrawer();
        
        editorAdapter = new EditorAdapter(EditorFragment.this, fileViewModel);
        binding.pager.setAdapter(editorAdapter);
        
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            	fileViewModel.setCurrentPosition(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            	
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            	
            }
        });
        
        new TabLayoutMediator(binding.tabs, binding.pager, true, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int pos) {
                 if (pos >= fileViewModel.getFiles().getValue().size()) return;
                 tab.setText(fileViewModel.getFiles().getValue().get(pos).getName());
            }
        }).attach();
    }
    
    private void setupViewModels() {
    	fileViewModel.getFiles().observe(this, this::onFilesUpdated);
        
        fileViewModel.currentPosition.observe(this, pos -> {
            if (binding.drawer.isOpen()) binding.drawer.close();
            
            final var tab = binding.tabs.getTabAt(pos);
            if(tab != null && !tab.isSelected()) {
            	tab.select();
                binding.pager.setCurrentItem(pos);
            }
        });
        
        fileViewModel.setCurrentPosition(0);
        if(fileViewModel.getFiles().getValue().isEmpty()) {
        	binding.container.setDisplayedChild(1);
        }
    }
    
    private void setupDrawer() {
        ((MainActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.drawer.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            requireActivity(),
            binding.drawer,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        ) {
            @Override
            public void onDrawerSlide(View v, float slideOffset) {
                super.onDrawerSlide(v, slideOffset);
                float sliderX = v.getWidth() * slideOffset;
                binding.constraint.setTranslationX(sliderX);
            }
            
        };
        binding.drawer.setDrawerListener(toggle);
        toggle.syncState();
    }
    
    /* Get and define all needed variables */
    private void setupData() {
    	if(ProjectHolder.getEditorState() != null) {
    		editorState = ProjectHolder.getEditorState();
            projectManager = new ProjectManager(getContext(), editorState.project.scId);
            fileViewModel = new ViewModelProvider(getActivity()).get(FileViewModel.class);
            ProjectHolder.setProjectManager(projectManager);
            ProjectHolder.setFileViewModel(fileViewModel);
    	}
    }
    
    private void onFilesUpdated(List<File> files) {
        if(files.isEmpty()) {
            binding.tabs.setVisibility(View.GONE);
            binding.container.setDisplayedChild(1);
    	} else {
            binding.tabs.setVisibility(View.VISIBLE);
            binding.container.setDisplayedChild(0);
        }
        /*binding.tabs.removeAllTabs();
        for(File file : files) {
        	final var newTab = binding.tabs.newTab();
            newTab.setText(file.getName());
            binding.tabs.addTab(newTab, false);
        }*/
    }
    
}
