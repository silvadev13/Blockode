package dev.silvadev.blockode.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import dev.silvadev.blockode.beans.ProjectBean;
import dev.silvadev.blockode.content.Links;
import dev.silvadev.blockode.databinding.ActivityMainBinding;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.activities.editor.LogicEditorActivity;
import dev.silvadev.blockode.ui.base.BaseAppCompatActivity;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.main.MainFragment;
import dev.silvadev.blockode.utils.URLUtil;

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
    showFragment(binding.content.getId(), new MainFragment(), "mainFragment");
  }
  
  @Override
  @SuppressWarnings("DEPRECATION")
  public void onBackPressed() {
  	FragmentManager fm = getSupportFragmentManager();
      if(fm.getBackStackEntryCount() > 1 || fm.getBackStackEntryCount() != 1) {
      	fm.popBackStack();
      } else {
      	finish();
      }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.binding = null;
  }
}
