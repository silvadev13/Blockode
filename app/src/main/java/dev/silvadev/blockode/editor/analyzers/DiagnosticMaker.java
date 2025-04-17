package dev.silvadev.blockode.editor.analyzers;

import android.util.Log;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.utils.FileUtil;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import kotlinx.coroutines.CoroutineScope;

public class DiagnosticMaker implements EventReceiver<ContentChangeEvent> {
    
    private DiagnosticsContainer diagnostics = new DiagnosticsContainer();
    private JavaAnalyzer analyzer;
    
    private CodeEditor editor;
    private EditorState editorState;
    private File file;
    
    public DiagnosticMaker(CodeEditor editor, File file, EditorState editorState) {
        this.editor = editor;
        this.file = file;
        this.editorState = editorState;
        analyzer = new JavaAnalyzer(editor, editorState, Collections.emptyList());
        analyze(editor.getText());
        Log.d("EditorDebug", "Diagnostic Maker Instancied");
    }
    
    @Override
    public void onReceive(ContentChangeEvent event, Unsubscribe unsubscribe) {
    	analyze(event.getEditor().getText());
        Log.d("EditorDebug", "onReceive");
    }
    
    private void analyze(Content content) {
    	FileUtil.writeText(file.getAbsolutePath(), content.toString());
        analyzer.reset();
        
        analyzer.analyze();
        diagnostics.reset();
        diagnostics.addDiagnostics(analyzer.getDiagnostics());
        
        editor.setDiagnostics(diagnostics);
    }
    
}
