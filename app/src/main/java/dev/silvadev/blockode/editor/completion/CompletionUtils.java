package dev.silvadev.blockode.editor.completion;

import android.os.Bundle;
import android.util.Log;
import com.tyron.javacompletion.JavaCompletions;
import com.tyron.javacompletion.completion.CompletionCandidate;
import com.tyron.javacompletion.completion.CompletionResult;
import dev.silvadev.blockode.editor.indexers.IndexerUtil;
import dev.silvadev.blockode.utils.FileUtil;
import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionItem;
import io.github.rosemoe.sora.lang.completion.CompletionItemKind;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.FuzzyScore;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompletionUtils implements EventReceiver<SelectionChangeEvent> {
    
    private List<CompletionItem> items = new ArrayList<>();
    
    private Path path;
    private JavaCompletions javaCompletions;
    private CodeEditor editor;
    
    private final static CodeSnippet FOR_SNIPPET = CodeSnippetParser.parse("for(int ${1:i} = 0;$1 < ${2:count};$1++) {\n    $0\n}");
    private final static CodeSnippet STATIC_CONST_SNIPPET = CodeSnippetParser.parse("private final static ${1:type} ${2/(.*)/${1:/upcase}/} = ${3:value};");
    private final static CodeSnippet CLIPBOARD_SNIPPET = CodeSnippetParser.parse("${1:${CLIPBOARD}}");
    private final static CodeSnippet TRY_CATCH_SNIPPET = CodeSnippetParser.parse("try {\n    $0\n} catch(Exception ${1:err}) {\n    $0\n}");
    
    public CompletionUtils(File file, CodeEditor codeEditor) {
    	path = file.toPath();
        javaCompletions = IndexerUtil.getIndexer().getJavaCompletions();
        editor = codeEditor;
        editor.subscribeEvent(SelectionChangeEvent.class, this);
        
        try {
        	javaCompletions.getFileManager().openFileForSnapshot(URI.create("file://" + file.getAbsolutePath()), FileUtil.readFile(file.getAbsolutePath(), false));
        } catch(Exception err) {
        	err.printStackTrace();
        }
    }
    
    public void requireAutoComplete(ContentReference content, String line, CharPosition position, String prefix, CompletionPublisher publisher, Bundle args) {
    	javaCompletions.updateFileContent(path, content.toString());
        
        if(prefix.matches("^[a-zA-Z].*") || line.endsWith(".")) {
        	CompletionResult result = javaCompletions.getCompletions(path, position.getLine(), position.getColumn());
            
            result.getCompletionCandidates().forEach(candidate -> {
                if(!candidate.getName().equals("<error>")) {
                	var item = new JavaCompletionItem(
                        candidate.getName(),
                        candidate.getDetail().orElse(candidate.getKind().name()),
                        prefix
                    );
                    item.kind(getKind(candidate.getKind()));
                    publisher.addItem(item);
                }
            });
        }
        
        if("try".startsWith(prefix) && prefix.length() > 0) {
        	publisher.addItem(new SimpleSnippetCompletionItem("try-catch", "A try-catch statement", new SnippetDescription(prefix.length(), TRY_CATCH_SNIPPET, true)));
        }
        
        if("for".startsWith(prefix) && prefix.length() > 0) {
        	publisher.addItem(new SimpleSnippetCompletionItem("for", "For loop on index", new SnippetDescription(prefix.length(), FOR_SNIPPET, true)));
        }
        
        if("clip".startsWith(prefix) && prefix.length() > 0) {
        	publisher.addItem(new SimpleSnippetCompletionItem("clipboard", "A copy to clipboard statement", new SnippetDescription(prefix.length(), CLIPBOARD_SNIPPET, true)));
        }
        
        if("static".startsWith(prefix) && prefix.length() > 0) {
        	publisher.addItem(new SimpleSnippetCompletionItem("static", "A static constant", new SnippetDescription(prefix.length(), STATIC_CONST_SNIPPET, true)));
        }
    }
    
    private boolean checkAgressive(FuzzyScore fuzzyScore, String word, String keyword) {
        if(keyword.toLowerCase().startsWith(word.toLowerCase())) return true;
        return (fuzzyScore != null && fuzzyScore.getScore() < 20);
    }
    
    private class Checker implements CompletionHelper.PrefixChecker {
		@Override
		public boolean check(char c) {
			return MyCharacter.isJavaIdentifierPart(c);
		}
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
	public void onReceive(SelectionChangeEvent arg0, Unsubscribe arg1) {
		javaCompletions.updateFileContent(path, editor.getText().toString());
	}

}
