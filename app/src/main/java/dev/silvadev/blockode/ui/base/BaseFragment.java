package dev.silvadev.blockode.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.transition.MaterialSharedAxis;
import dev.silvadev.blockode.R;

public abstract class BaseFragment extends Fragment {
    
    @NonNull private View root;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        root = bindLayout();
        onBindLayout(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getParentFragmentManager();
                int stackEntryCount = fm.getBackStackEntryCount();
                if (stackEntryCount > 1 || stackEntryCount != 1) {
                    fm.popBackStack();
                } else {
                    requireActivity().finish();
                }
            }
        });
        return root;
    }
    
    @NonNull
    protected abstract View bindLayout();
    
    protected abstract void onBindLayout(@Nullable final Bundle savedInstanceState);
    
    protected void showFragment(int id, BaseFragment fragment, String tag) {
        getParentFragmentManager()
            .beginTransaction()
            .replace(id, fragment, tag)
            .addToBackStack(null)
            .commit();
    }
    
    protected void showFragment(BaseFragment fragment, String tag) {
        getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.content, fragment, tag)
            .addToBackStack(null)
            .commit();
    }
    
    @Override
    public String toString() {
        return "BaseFragment";
    }
}