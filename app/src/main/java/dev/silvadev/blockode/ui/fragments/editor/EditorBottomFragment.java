package dev.silvadev.blockode.ui.fragments.editor;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.lifecycle.ViewModelProvider;
import dev.silvadev.blockode.databinding.FragmentBottomEditorBinding;
import dev.silvadev.blockode.model.logger.Log;
import dev.silvadev.blockode.model.logger.LogViewModel;
import dev.silvadev.blockode.ui.base.BaseFragment;
import dev.silvadev.blockode.ui.fragments.editor.adapter.LogAdapter;
import java.util.List;

public class EditorBottomFragment extends BaseFragment {
    
    private FragmentBottomEditorBinding binding;
    
    private LogViewModel logViewModel;
    private LogAdapter logAdapter;
    
    @Override
    protected View bindLayout() {
        binding = FragmentBottomEditorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    
    @Override
    protected void onBindLayout(final Bundle savedInstanceState) {
        //setupViewModels();
    }
    
    private void setupViewModels() {
        logViewModel = new ViewModelProvider(getActivity()).get(LogViewModel.class);
        logAdapter = new LogAdapter();
    	logViewModel.getLogs().observe(this, this::onLogsUpdate);
    }
    
    private void onLogsUpdate(List<Log> logs) {
    	if(logs == null) {
    		return;
    	}
        
        logAdapter.submitList(logs);
        binding.status.setText(logs.get(logs.size() - 1).getMessage());
    }
    
    public LinearLayout getHeaderView() {
    	return binding.header;
    }
    
}
