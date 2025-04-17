package dev.silvadev.blockode.editor.language;
import android.content.res.AssetManager;
import com.itsaky.androidide.treesitter.TSLanguage;
import dev.silvadev.blockode.utils.FileUtil;
import io.github.rosemoe.sora.editor.ts.LocalsCaptureSpec;
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec;
import io.github.rosemoe.sora.editor.ts.TsThemeBuilder;
import io.github.rosemoe.sora.editor.ts.predicate.builtin.MatchPredicate;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;

public class TreeSitterUtils {
    public static void applyTheme(TsThemeBuilder desc) {
    	desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.COMMENT,
            0,
            false,
            true,
            false
        ), "comment");
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.KEYWORD,
            0,
            true,
            false,
            false
        ), "keyword");
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.LITERAL
        ), new String[]{
        "constant.builtin",
        "string",
        "number"
        });
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.IDENTIFIER_VAR
        ), new String[]{
        "variable.builtin",
        "variable",
        "constant"
        });
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.IDENTIFIER_NAME
        ), new String[]{
        "type.builtin",
        "type",
        "attribute"
        });
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.FUNCTION_NAME
        ), new String[]{
        "function.method",
        "function.builtin",
        "variable.field"
        });
        desc.applyTo(TextStyle.makeStyle(
            EditorColorScheme.OPERATOR
        ), new String[]{
        "operator"
        });
    }
    public static TsLanguageSpec createLanguageSpec(TSLanguage language, AssetManager assets, String name) {
    	var tsQueryPath = "tree-sitter-queries/" + name;
        String highlights = "";
        String blocks = "";
        String brackets = "";
        String locals = "";
        try {
        	highlights = FileUtil.readAsset(assets, tsQueryPath + "/highlights.scm");
            blocks = FileUtil.readAsset(assets, tsQueryPath + "/blocks.scm");
            brackets = FileUtil.readAsset(assets, tsQueryPath + "/brackets.scm");
            locals = FileUtil.readAsset(assets, tsQueryPath + "/locals.scm");
        } catch(Exception err) {}
        return new TsLanguageSpec(
                language,
                highlights,
                blocks,
                brackets,
                locals,
                new LocalsCaptureSpec() {
                    @Override
                    public boolean isScopeCapture(String captureName) {
                    	return "scope".equals(captureName);
                    }
                    
                    @Override
                    public boolean isReferenceCapture(String captureName) {
                    	return "reference".equals(captureName);
                    }
                    
                    @Override
                    public boolean isDefinitionCapture(String captureName) {
                    	return "definition.var".equals(captureName) || "definition.field".equals(captureName);
                    }
                    
                    @Override
                    public boolean isMembersScopeCapture(String captureName) {
                    	return "scope.members".equals(captureName);
                    }
                },
                Collections.singletonList(MatchPredicate.INSTANCE)
            );
    }
}
