package dev.silvadev.blockode.ui.fragments.editor;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import dev.silvadev.blockode.databinding.FragmentEditorBinding;
import dev.silvadev.blockode.databinding.FragmentEditorContainerBinding;
import dev.silvadev.blockode.editor.BlockodeEditor;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.utils.FileUtil;
import java.io.File;

public class EditorContainerFragment extends BaseFragment {
    
    @NonNull private FragmentEditorContainerBinding binding;
    
    private File file;
    
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
        }
    }
    
    public BlockodeEditor getEditor() {
    	return binding.editor;
    }
    
    public void setEditorText() {
    	if (file != null && file.exists()) getEditor().setText(FileUtil.readFile(file.getAbsolutePath(), false));
    }
    
    public long getHashCode() {
    	return (long)file.hashCode();
    }
    
    
}
