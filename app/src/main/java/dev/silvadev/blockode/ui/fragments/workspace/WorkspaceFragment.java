package dev.silvadev.blockode.ui.fragments.workspace;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dev.silvadev.blockode.databinding.FragmentWorkspaceBinding;
import dev.silvadev.blockode.filetree.adapter.FileTreeAdapter;
import dev.silvadev.blockode.filetree.listener.OnTreeItemClickListener;
import dev.silvadev.blockode.filetree.utils.FileTreeUtils;
import dev.silvadev.blockode.filetree.Node;
import java.io.File;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.ui.activities.editor.EditorActivity;
import dev.silvadev.blockode.ui.activities.editor.EditorState;
import dev.silvadev.blockode.ui.base.BaseAppCompatActivity;
import dev.silvadev.blockode.ui.base.BaseFragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceFragment extends BaseFragment {
    
    @NonNull private FragmentWorkspaceBinding binding;
    
    public static ProjectManager projectManager;
    
    @Override
    protected View bindLayout() {
        binding = FragmentWorkspaceBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        EditorActivity editorActivity = (EditorActivity) getActivity();
        if (editorActivity != null) projectManager = editorActivity.getProjectManager();
        setupFileTree();
    }
    
    private void setupFileTree() {
        File dir = ProjectManager.getProjectsFile();
    	List<Node<File>> nodes = FileTreeUtils.toNodes(dir);
        FileTreeAdapter adapter = new FileTreeAdapter(getContext(), nodes);
        adapter.setOnItemClickListener(new OnTreeItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: Implement this method
            }
            
            @Override
            public void onItemLongClick(View view, int position) {
                // TODO: Implement this method
            }
        });
        binding.filetree.setAdapter(adapter);
    }
    
}
