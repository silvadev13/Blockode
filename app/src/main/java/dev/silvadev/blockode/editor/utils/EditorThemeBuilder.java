package dev.silvadev.blockode.editor.utils;
import android.content.Context;
import com.google.android.material.color.MaterialColors;
import com.google.gson.Gson;
import dev.silvadev.blockode.utils.FileUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditorThemeBuilder {
    private static Gson gson = new Gson();
    
    public static InputStream getTheme(Context context, String fileName) throws Exception {
    	var theme = context.getAssets().open("textmate/" + fileName);
        return applyAttributies(context, theme);
    }
    
    public static InputStream applyAttributies(Context context, InputStream stream) {
    	var contents = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
        var json = gson.fromJson(contents, Map.class);
        
        var baseSettings = (List<Map<String, Object>>) json.get("settings");
        if(baseSettings != null && !baseSettings.isEmpty()) {
        	var settings = (Map<String, String>) baseSettings.get(0).get("settings");
            if(settings != null) {
            	settings.put("background", getDynamicColor(context, com.google.android.material.R.attr.colorSurfaceContainerLow));
                settings.put("foreground", getDynamicColor(context, com.google.android.material.R.attr.colorOnSurfaceVariant));
                settings.put("caret", getDynamicColor(context, com.google.android.material.R.attr.colorOnSurfaceVariant));
            }
        }
        
        return (InputStream) new ByteArrayInputStream(gson.toJson(json).getBytes(StandardCharsets.UTF_8));
    }
    
    public static String getDynamicColor(Context context, int colorId) {
        var color = MaterialColors.getColor(context, colorId, null);
        return String.format("#%08X", color);
    }
}
