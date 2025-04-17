package dev.silvadev.blockode;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import androidx.appcompat.app.AppCompatDelegate;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import java.io.File;

public final class Blockode extends Application {

  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    loadTextMateTheme();
  }
  
  public static final void applyEditorThemeByThemeMode() {
  	String themeName = "darcula";
      int mode = AppCompatDelegate.getDefaultNightMode();
      
      if(mode == AppCompatDelegate.MODE_NIGHT_YES) {
      	themeName = "darcula";
      } else if(mode == AppCompatDelegate.MODE_NIGHT_NO) {
      	themeName = "QuietLight";
      }
      
      ThemeRegistry.getInstance().setTheme(themeName);
  }
  
  public void loadTextMateTheme() {
  	final var fileProvider = new AssetsFileResolver(getAssets());
      FileProviderRegistry.getInstance().addFileProvider(fileProvider);
      GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
  }

  public static final String getPublicFolderPath() {
    return getPublicFolderFile().getAbsolutePath();
  }

  public static final File getPublicFolderFile() {
    return new File(Environment.getExternalStorageDirectory(), ".blockode/");
  }

  public static final Context getAppContext() {
    return appContext;
  }
}
