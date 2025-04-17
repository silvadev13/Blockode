package dev.silvadev.blockode.editor;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import com.google.android.material.color.MaterialColors;
import dev.silvadev.blockode.editor.language.TsLanguageJava;
import io.github.rosemoe.sora.langs.textmate.TextMateAnalyzer;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.component.EditorDiagnosticTooltipWindow;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import org.eclipse.tm4e.core.registry.IThemeSource;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;

public class BlockodeEditor extends CodeEditor {
    
    public BlockodeEditor(Context context) {
        super(context);
        init();
    }
    
    public BlockodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public BlockodeEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    public void init() {
        try {
            setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
            setTypefaceText(Typeface.MONOSPACE);
            setWordwrap(true);
            getProps().symbolPairAutoCompletion = true;
            getComponent(EditorAutoCompletion.class).setEnabled(true);
        } catch(Exception err) {
            Log.e("BlockodeEditor", err.toString());
        	err.printStackTrace();
        }
    }
    
}
