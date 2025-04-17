package dev.silvadev.blockode.editor.language;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSParser;
import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.tyron.javacompletion.JavaCompletions;
import com.tyron.javacompletion.completion.CompletionCandidate;
import com.tyron.javacompletion.options.JavaCompletionOptionsImpl;
import com.tyron.javacompletion.project.Project;
import dev.silvadev.blockode.editor.completion.CompletionUtils;
import dev.silvadev.blockode.editor.completion.JavaCompletionItem;
import dev.silvadev.blockode.editor.indexers.Indexer;
import dev.silvadev.blockode.editor.indexers.IndexerUtil;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import io.github.rosemoe.sora.editor.ts.TsAnalyzeManager;
import io.github.rosemoe.sora.editor.ts.TsLanguage;
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec;
import io.github.rosemoe.sora.editor.ts.TsThemeBuilder;
import io.github.rosemoe.sora.lang.QuickQuoteHandler;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionItemKind;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TsLanguageJava extends TsLanguage {
    
    private CompletionUtils completionUtils;
    
    private static TSLanguage TS_LANGUAGE_JAVA = TSLanguageJava.getInstance();
    private TSParser parser = TSParser.create();
    
    public TsLanguageJava(TsLanguageSpec languageSpec, Function1<TsThemeBuilder, Unit> themeDesc, CodeEditor editor, File file) {
        super(languageSpec, false, themeDesc);
        parser.setLanguage(TS_LANGUAGE_JAVA);
        completionUtils = new CompletionUtils(file, editor);
    }
    
    public static TsLanguageJava getInstance(CodeEditor editor, File file) {
        TsLanguageSpec languageSpec = TreeSitterUtils.createLanguageSpec(TS_LANGUAGE_JAVA, editor.getContext().getAssets(), "java");
        Function1<TsThemeBuilder, Unit> themeApplier = new Function1<TsThemeBuilder, Unit>() {
            @Override
            public Unit invoke(TsThemeBuilder it) {
                TreeSitterUtils.applyTheme(it);
            	return Unit.INSTANCE;
            }
        };
        return new TsLanguageJava(
            languageSpec,
            themeApplier,
            editor,
            file
        );
    }
    
    @Override
    public void requireAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher,Bundle args) {
        super.requireAutoComplete(content, position, publisher, args);
        
        var prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        var line = content.getLine(position.getLine());
        completionUtils.requireAutoComplete(content,line,position,prefix,publisher,args);
    }
    
    private CompletionItemKind getKind(CompletionCandidate.Kind candKind){
		CompletionItemKind kind;
		switch (candKind) {
			case CLASS:
			kind = CompletionItemKind.Class;
			break;
			case INTERFACE:
			kind = CompletionItemKind.Interface;
			break;
			case ENUM:
			kind = CompletionItemKind.Enum;
			break;
			case METHOD:
			kind = CompletionItemKind.Method;
			break;
			case FIELD:
			kind = CompletionItemKind.Field;
			break;
			case VARIABLE:
			kind = CompletionItemKind.Variable;
			break;
			case PACKAGE:
			kind = CompletionItemKind.Module;
			break;
			case KEYWORD:
			kind = CompletionItemKind.Keyword;
			break;
			default:
			kind = CompletionItemKind.Text;
			break;
		}
		return kind;
	}
    
    @Override
    public void destroy() {
        super.destroy();
    }
    
    @Override
    public TsAnalyzeManager getAnalyzer() {
        return super.getAnalyzer();
    }
    
}
