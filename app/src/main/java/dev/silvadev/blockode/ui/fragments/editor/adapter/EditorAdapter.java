package dev.silvadev.blockode.ui.fragments.editor.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import dev.silvadev.blockode.databinding.FragmentEditorBinding;
import dev.silvadev.blockode.databinding.LayoutProjectBinding;
import dev.silvadev.blockode.model.FileViewModel;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.editor.EditorContainerFragment;
import java.io.File;
import dev.silvadev.blockode.project.manage.ProjectManager;
import dev.silvadev.blockode.utils.function.Listener;
import java.util.ArrayList;
import java.util.List;

public class EditorAdapter extends FragmentStateAdapter {

    private FileViewModel fileViewModel;
    private Fragment fragment;
    
    private ArrayList<EditorContainerFragment> fragments = new ArrayList<>();
    private List<Long> ids = new ArrayList<>();
    
    public EditorAdapter(Fragment fragment, FileViewModel fileViewModel) {
        super(fragment);
        this.fragment = fragment;
        this.fileViewModel = fileViewModel;
        
        List<Long> newIds = new ArrayList<>();
        fileViewModel.getFiles().observe(fragment, newFiles -> {
            if (newFiles == null) return;
            
            for(File file : newFiles) {
            	newIds.add((long)file.hashCode());
            }
            
            setIds(newIds);
            notifyDataSetChanged();
        });
    }
    
    @Override
    public Fragment createFragment(int pos) {
        EditorContainerFragment editorFrag = new EditorContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("file", fileViewModel.getFiles().getValue().get(pos));
        editorFrag.setArguments(bundle);
        fragments.add(editorFrag);
        return editorFrag;
    }
    
    public int getItemCount() {
        return ids.size();
    }
    
    public void setIds(List<Long> newIds) {
    	List<Long> oldIds = new ArrayList<>(ids);
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldIds.size();
            }
            
            @Override
            public int getNewListSize() {
                return newIds.size();
            }
            
            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                return oldIds.get(oldPos).hashCode() == newIds.get(newPos).hashCode();
            }
            
            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return oldIds.get(oldPos).equals(newIds.get(newPos));
            }
            
        });
        
        ids = new ArrayList<>(newIds);
        diffResult.dispatchUpdatesTo(this);
    }
    
    public EditorContainerFragment getItem(int pos) {
    	if (pos >= fragments.size()) return null;
        if (pos < 0) return null;
        return fragments.get(pos);
    }
    
    @Override
    public long getItemId(int pos) {
        return ids.get(pos).hashCode();
    }
    
    
    public void removeItem(int pos) {
    	if(fragments.size() <= pos) {
    		return;
    	}
        
        EditorContainerFragment editorFrag = fragments.get(pos);
        editorFrag.getEditor().release();
        editorFrag.getActivity().getSupportFragmentManager().beginTransaction().remove(editorFrag).commit();
        fragments.remove(pos);
    }
    
    @Override
    public boolean containsItem(long itemId) {
        for(EditorContainerFragment fragment : fragments) {
        	if (fragment.getHashCode() == itemId) return true;
        }
        return false;
    }
    
    
}
