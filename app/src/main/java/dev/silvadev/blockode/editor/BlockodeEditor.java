package dev.silvadev.blockode.editor;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import io.github.rosemoe.sora.langs.textmate.TextMateAnalyzer;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import org.eclipse.tm4e.core.registry.IThemeSource;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;

public class BlockodeEditor extends CodeEditor {
    
    public String path;
    
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
            /*TextMateColorScheme scheme = TextMateColorScheme.create(ThemeRegistry.getInstance());
        	setColorScheme();
            TextMateLanguage language = TextMateLanguage.create(GrammarRegistry.getInstance().loadGrammars("editor/languages.json").get(0).getScopeName(), true);
            language.setAutoCompleteEnabled(true);
            setEditorLanguage(new JavaLanguage(this, getPath()));*/
            EditorColorScheme editorColorScheme = new SchemeDarcula();
            setColorScheme(editorColorScheme);
            setTypefaceText(Typeface.MONOSPACE);
            setWordwrap(true);
            getProps().symbolPairAutoCompletion = true;
            getComponent(EditorAutoCompletion.class).setEnabled(true);
        } catch(Exception err) {
            Log.e("BlockodeEditor", err.toString());
        	err.printStackTrace();
        }
    }
    
    public void setPath(String path) {
    	this.path = path;
    }
    
    public String getPath() {
    	return path;
    }
    
}
