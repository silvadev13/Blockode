package dev.silvadev.blockode.ui.fragments.editor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import dev.silvadev.blockode.databinding.FragmentEditorBinding;
import dev.silvadev.blockode.editor.indexers.Indexer;
import dev.silvadev.blockode.editor.indexers.IndexerUtil;
import dev.silvadev.blockode.model.FileViewModel;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.activities.main.MainActivity;
import dev.silvadev.blockode.ui.fragments.editor.adapter.EditorAdapter;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.R;
import dev.silvadev.blockode.ui.fragments.editor.core.ProjectHolder;
import dev.silvadev.blockode.utils.AndroidUtilities;
import dev.silvadev.build.BuildTask;
import dev.silvadev.build.project.Project;
import java.io.File;
import java.util.List;

public class EditorFragment extends BaseFragment {
    
    @NonNull private FragmentEditorBinding binding;
    
    @Nullable public EditorState editorState;
    private EditorAdapter editorAdapter;
    
    public ProjectManager projectManager;
    private FileViewModel fileViewModel;
    private TabLayoutMediator tabLayoutMediator;
    
    public static EditorFragment newInstance(EditorState editorState) {
        EditorFragment editor = new EditorFragment();
        
        Bundle args = new Bundle();
        args.putParcelable("editorState", editorState);
        editor.setArguments(args);
        
        return editor;
    }
    
    public EditorFragment() {}
    
    @Override
    protected View bindLayout() {
        binding = FragmentEditorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        if (getArguments() != null) {
            editorState = getArguments().getParcelable("editorState");
        }
        setupData();
        setupViewModels();
        setupToolbar();
        setupDrawer();
        setupIndex();
        setupBottomSheet();
        
        editorAdapter = new EditorAdapter(EditorFragment.this, fileViewModel);
        binding.pager.setAdapter(editorAdapter);
        binding.pager.setUserInputEnabled(false);
        binding.pager.setSaveEnabled(true);
        
        binding.tabs.setSmoothScrollingEnabled(false);
        
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
    }
    
    @Override
    protected boolean onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            ProjectHolder.clear();
            getParentFragmentManager().popBackStack();
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    private void setupViewModels() {
    	fileViewModel.getFiles().observe(this, files -> {
            onFilesUpdated(files);
            
            if(tabLayoutMediator != null) {
            	tabLayoutMediator.detach();
            }
        
            tabLayoutMediator = new TabLayoutMediator(binding.tabs, binding.pager, true, false, (tab, pos) -> {
                if (pos >= getFiles().size()) {
                    tab.setText("Tab Ghost " + pos);
                    return;
                }
                tab.setText(getFiles().get(pos).getName());
            });
            tabLayoutMediator.attach();
        });
        
        fileViewModel.currentPosition.observe(this, pos -> {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) binding.drawer.closeDrawer(GravityCompat.START);
            
            final var tab = binding.tabs.getTabAt(pos);
            if(tab != null && !tab.isSelected()) {
            	tab.select();
                binding.pager.setCurrentItem(pos);
            }
        });
        
        fileViewModel.setCurrentPosition(0);
        if(getFiles().isEmpty()) {
        	binding.container.setDisplayedChild(1);
        }
    }
    
    private void setupToolbar() {
    	MainActivity main = (MainActivity) getActivity();
        if(main != null) {
        	main.setSupportActionBar(binding.toolbar);
            setHasOptionsMenu(true);
        }
        binding.toolbar.setSubtitle(editorState.project.basicInfo.name);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.menu_build) {
            	var project = new Project(ProjectManager.getProjectRootFile(editorState.project.scId));
                var buildTask = new BuildTask(getContext(), new BuildTask.onBuildStatusChanged() {
                    @Override
                    public void onChanged(BuildTask.BuildKind kind, String message) {
                        if(kind == BuildTask.BuildKind.INFO) {
                        	Log.d("EditorDebug", "INFO: " + message);
                        }
                        
                        if(kind == BuildTask.BuildKind.WARN) {
                        	Log.d("EditorDebug", "WARN: " + message);
                        }
                        
                        if(kind == BuildTask.BuildKind.ERROR) {
                        	Log.d("EditorDebug", "ERROR: " + message);
                        }
                        
                        if(kind == BuildTask.BuildKind.SUCCESS) {
                        	Log.d("EditorDebug", "SUCCESS: " + message);
                        }
                    }
                });
                buildTask.execute(project);
            }
            return true;
        });
    }
    
    private void setupDrawer() {
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
    	if(editorState != null) {
            projectManager = new ProjectManager(getContext(), editorState.project.scId);
            fileViewModel = new ViewModelProvider(getActivity()).get(FileViewModel.class);
            
            ProjectHolder.setEditorState(editorState);
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
        
    }
    
    private void setupIndex() {
        binding.indexing.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                	Looper.prepare();
                    IndexerUtil.setIndexer(new Indexer().indexFiles(getContext(), editorState));
                } catch(Exception err) {
                	err.printStackTrace();
                }
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!IndexerUtil.getIndexer().isIndexing())
                    binding.indexing.setVisibility(View.GONE);
                });
            }
        }.start();
    }
    
    private void setupBottomSheet() {
    	BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.editorSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View arg0, int arg1) {
                // TODO: Implement this method
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                setOffset(slideOffset);
            }
        });
        behavior.setHalfExpandedRatio(0.5f);
        behavior.setFitToContents(false);
    }
    
    private List<File> getFiles() {
        return fileViewModel.getFiles().getValue();
    }
    
    private void setOffset(float offset) {
    	if(offset >= 0.50f) {
            var invertedOffset = 0.5f - offset;
    		var newOffset = ((invertedOffset + 0.5f) * 2f);
            setHeaderOffset(newOffset);
    	} else if(binding.editorSheet.getHeight() != AndroidUtilities.dp(60)) {
    		setHeaderOffset(1f);
    	}
    }
    
    private void setHeaderOffset(float offset) {
        var header = ((EditorBottomFragment)binding.editorSheet.getFragment()).getHeaderView();
    	header.getLayoutParams().height = Math.round(AndroidUtilities.dp(70) * offset);
        header.requestLayout();
    }
    
}
