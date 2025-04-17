package dev.silvadev.blockode.ui.fragments.main.components;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import dev.silvadev.blockode.databinding.DialogInstallRequiredFilesBinding;
import dev.silvadev.blockode.utils.DownloaderUtils;
import dev.silvadev.blockode.utils.FileUtil;
import dev.silvadev.blockode.values.Urls;
import dev.silvadev.blockode.R;

public class DownloadRequiredFilesDialog extends MaterialAlertDialogBuilder {
    
    private DialogInstallRequiredFilesBinding binding;
    
    public DownloadRequiredFilesDialog(LayoutInflater inflater, Context context) {
        super(context);
        binding = DialogInstallRequiredFilesBinding.inflate(inflater);
        setView(binding.getRoot());
        setCancelable(false);
        setTitle(R.string.install_required_files);
        setMessage(R.string.install_required_files_desc);
        binding.btnInstall.setOnClickListener(v -> install(context));
    }
    
    public void install(Context context) {
    	binding.btnInstall.setEnabled(false);
        binding.progressText.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
        DownloaderUtils downloaderUtils = new DownloaderUtils(context);
        downloaderUtils.start(Urls.INDEX_JSON, FileUtil.getIndexFile(getContext()).getAbsolutePath(), "index.json", binding.progress, binding.progressText, new DownloaderUtils.OnStatusChanged() {
            @Override
            public void onCompleted() {
                create().dismiss();
            }
            @Override
            public void onError(com.downloader.Error error) {
                Toast.makeText(context, "Donwloader error: " + error.toString(), Toast.LENGTH_SHORT).show();
                create().dismiss();
            }
            
        });
    }
    
}
