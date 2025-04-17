package dev.silvadev.blockode.ui.fragments.editor;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import dev.silvadev.blockode.databinding.FragmentEditorBinding;
import dev.silvadev.blockode.databinding.FragmentEditorContainerBinding;
import dev.silvadev.blockode.editor.BlockodeEditor;
import dev.silvadev.blockode.editor.analyzers.DiagnosticMaker;
import dev.silvadev.blockode.editor.language.TsLanguageJava;
import dev.silvadev.blockode.editor.language.TsLanguageJava;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.editor.core.ProjectHolder;
import dev.silvadev.blockode.utils.FileUtil;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.event.SubscriptionReceipt;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import java.io.File;

public class EditorContainerFragment extends BaseFragment {
    
    @NonNull private FragmentEditorContainerBinding binding;
    
    private File file;
    
    private SubscriptionReceipt<ContentChangeEvent> eventReceive;
    
    @Override
    @NonNull
    protected View bindLayout() {
        binding = FragmentEditorContainerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onBindLayout(final Bundle savedInstanceState) {
        if (getArguments() != null) {
            file = (File) getArguments().getSerializable("file");
            setEditorText();
            setEditorLanguage(file.getName());
            setColorScheme();
        }
    }
    
    public BlockodeEditor getEditor() {
    	return binding.editor;
    }
    
    public void setEditorText() {
    	if (file != null && file.exists()) getEditor().setText(FileUtil.readFile(file.getAbsolutePath(), false));
    }
    
    public void setColorScheme() {
        try {
        	binding.editor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
        } catch(Exception err) {}
    }
    
    public void setEditorLanguage(String name) {
    	switch (FileUtil.getFileExtension(name)) {
            case "java":
                binding.editor.setEditorLanguage(TsLanguageJava.getInstance(
                    binding.editor,
                    file
                ));
                binding.editor.subscribeEvent(ContentChangeEvent.class, new DiagnosticMaker(binding.editor, file, ProjectHolder.editorState));
                break;
        }
    }
    
    public long getHashCode() {
    	return (long)file.hashCode();
    }
    
    
}
