package dev.silvadev.blockode.utils;

import androidx.annotation.NonNull;
import dev.silvadev.blockode.R;

public class SpecUtil {
  public static String getSpecForFileName(@NonNull String className) {
    return StringUtil.getString(R.string.root_spec_define) + " " + className;
  }
}
