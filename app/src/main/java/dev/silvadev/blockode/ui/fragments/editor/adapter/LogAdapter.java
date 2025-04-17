package dev.silvadev.blockode.ui.fragments.editor.adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import dev.silvadev.blockode.model.logger.Log;

public class LogAdapter extends ListAdapter<Log, LogAdapter.LogAdapterViewHolder> {

    public LogAdapter() {
      super(new LogsAdapterDiffUtil());
    }
  
    @Override
    public LogAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int parentType) {
      return new LogAdapterViewHolder(new FrameLayout(parent.getContext()));
    }
  
    @Override
    public void onBindViewHolder(LogAdapterViewHolder holder, int pos) {
      var log = getItem(pos);
      holder.textView.setText((pos + 1) + log.getLevel() + log.getMessage());
    }
  
    public static class LogAdapterViewHolder extends RecyclerView.ViewHolder {
      private TextView textView;
    
      public LogAdapterViewHolder(@NonNull View view) {
        super(view);
        textView = new TextView(view.getContext());
        ((ViewGroup)view).addView(textView);
      }
    }
  
    public static class LogsAdapterDiffUtil extends DiffUtil.ItemCallback<Log> {
      @Override
      public boolean areItemsTheSame(@NonNull Log oldItem, @NonNull Log newItem) {
        return oldItem.getMessage().equals(newItem.getMessage());
      }

      @Override
      public boolean areContentsTheSame(@NonNull Log oldItem, @NonNull Log newItem) {
        return oldItem.equals(newItem);
      }
    }
}
