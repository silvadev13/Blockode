package dev.silvadev.blockode.ui.activities.editor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import dev.silvadev.blockode.databinding.ActivityEditorBinding;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.base.BaseAppCompatActivity;
import dev.silvadev.blockode.R;

public class EditorActivity extends BaseAppCompatActivity {
    
    @NonNull private ActivityEditorBinding binding;
    
    @Nullable public EditorState editorState;
    
    public ProjectManager projectManager;
    
    @Override
    protected View bindLayout() {
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        setupDrawer();
        setupData(savedInstanceState);
        projectManager = new ProjectManager(this, editorState.project.scId);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("editor_state", editorState);
    }
    
    
    @Override
    @Deprecated
    public void onBackPressed() {
        super.onBackPressed();
        if(binding.drawer.isDrawerOpen(GravityCompat.START))
        	binding.drawer.closeDrawer(GravityCompat.START);
    }
    
    private void setupDrawer() {
        setSupportActionBar(binding.toolbar);
        binding.drawer.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            EditorActivity.this,
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
    public void setupData(final Bundle savedInstanceState) {
    	if(savedInstanceState == null) {
    		editorState = getParcelable("editor_state", EditorState.class);
    	} else {
    		editorState = getParcelable(savedInstanceState, "editor_state", EditorState.class);
    	}
    }
    
    public ProjectManager getProjectManager() {
    	return projectManager;
    }
    
}
