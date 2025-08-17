package dev.silvadev.blockode.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import dev.silvadev.blockode.Blockode;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.content.Links;
import dev.silvadev.blockode.databinding.ActivityMainBinding;
import dev.silvadev.blockode.editor.utils.EditorThemeBuilder;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.activities.editor.LogicEditorActivity;
import dev.silvadev.blockode.ui.base.BaseAppCompatActivity;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.main.MainFragment;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.utils.URLUtil;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import java.io.File;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class MainActivity extends BaseAppCompatActivity {

  @NonNull private ActivityMainBinding binding;

  @Override
  @NonNull
  protected View bindLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onBindLayout(@Nullable final Bundle savedInstanceState) {
    if (savedInstanceState == null) {
        showFragment(binding.content.getId(), new MainFragment(), "mainFragment");
    }
    setupClassPathDir();
    try {
    	setEditorThemes();
    } catch(Exception err) {
    	err.printStackTrace();
    }
    
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.binding = null;
  }
  
  public void setEditorThemes() throws Exception {
  	var themeRegistry = ThemeRegistry.getInstance();
      themeRegistry.loadTheme(loadTheme("darcula.json", "darcula"));
      themeRegistry.loadTheme(loadTheme("QuietLight.tmTheme.json", "QuietLight"));
      
      Blockode.applyEditorThemeByThemeMode();
  }
  
  public ThemeModel loadTheme(String fileName, String themeName) throws Exception {
  	var inputStream = EditorThemeBuilder.getTheme(this, fileName);
      var source = IThemeSource.fromInputStream(inputStream, fileName, null);
      return new ThemeModel(source);
  }
  
  public void setupClassPathDir() {
  	var classPath = FileUtil.getClassPathDir(this);
      
      if(!classPath.exists()) {
      	classPath.mkdirs();
      }
      
      var cpArray = classPath.listFiles();
      
      if(cpArray.length < 2) {
      	try {
      		FileUtil.extractAssetFile(this, "android.jar", classPath.getAbsolutePath() + "/android.jar");
              FileUtil.extractAssetFile(this, "core-lambda-stubs.jar", classPath.getAbsolutePath() + "/core-lambda-stubs.jar");
      	} catch(Exception err) {
      		err.printStackTrace();
      	}
      }
  }
  
}
