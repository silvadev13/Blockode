package dev.silvadev.blockode.filetree.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import dev.silvadev.blockode.filetree.Node;
import dev.silvadev.blockode.filetree.R;
import dev.silvadev.blockode.filetree.listener.OnTreeItemClickListener;
import dev.silvadev.blockode.filetree.utils.FileTreeUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTreeAdapter extends RecyclerView.Adapter<FileTreeAdapter.ViewHolder> {

    private final List<Node<File>> nodes;
    private final Drawable fileIcon;
    private final Drawable folderIcon;
    private final Drawable chevronRightIcon;
    private final Drawable expandMoreIcon;
    private OnTreeItemClickListener listener;

    public FileTreeAdapter(Context context, List<Node<File>> nodes) {
        this.nodes = nodes;

        fileIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.outline_insert_drive_file_24, context.getTheme());
        folderIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.outline_folder_24, context.getTheme());
        chevronRightIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_chevron_right_24, context.getTheme());
        expandMoreIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_expand_more_24, context.getTheme());
    }

    public void setOnItemClickListener(OnTreeItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public FileTreeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filetree, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileTreeAdapter.ViewHolder holder, int position) {
        Node<File> node = nodes.get(position);
        int indentation = node.depth * 36;
        holder.itemView.setPaddingRelative(indentation, 0, 0, 0);

        if (node.value.isDirectory()) {
            holder.expandView.setImageDrawable(node.isExpanded ? expandMoreIcon : chevronRightIcon);
            holder.fileView.setPadding(0, 0, 0, 0);
            holder.fileView.setImageDrawable(folderIcon);
        } else {
            holder.expandView.setImageDrawable(null);
            holder.fileView.setPaddingRelative(chevronRightIcon.getIntrinsicWidth(), 0, 0, 0);
            holder.fileView.setImageDrawable(fileIcon);
        }

        holder.textView.setText(node.value.getName());

        holder.itemView.setOnClickListener(view -> {
            if (node.value.isDirectory()) {
                toggleDirectory(node, position);
            }
            if (listener != null) {
                listener.onItemClick(view, position);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) {
                listener.onItemLongClick(view, position);
            }
            return true;
        });
    }

    private void toggleDirectory(Node<File> node, int position) {
        if (!node.isExpanded) {
            expandDirectory(node, position);
        } else {
            collapseDirectory(node, position);
        }
        notifyItemChanged(position);
    }

    private void expandDirectory(Node<File> node, int position) {
        Node<File> parent = node;
        List<Node<File>> children;
        int index = position;
        int count = 0;

        do {
            children = FileTreeUtils.toNodes(parent.value);
            nodes.addAll(index + 1, children);
            FileTreeUtils.addChildren(parent, children);

            if (!children.isEmpty()) {
                parent = children.get(0);
                count += children.size();
                index++;
            }
        } while (children.size() == 1 && children.get(0).value.isDirectory());

        notifyItemRangeInserted(position + 1, count);
    }

    private void collapseDirectory(Node<File> node, int position) {
        List<Node<File>> descendants = FileTreeUtils.getDescendants(node);
        Set<Node<File>> descendantSet = new HashSet<>(descendants);
        nodes.removeAll(descendantSet);
        FileTreeUtils.removeChildren(node);
        notifyItemRangeRemoved(position + 1, descendants.size());
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView expandView;
        public ImageView fileView;
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            expandView = view.findViewById(R.id.expand);
            fileView = view.findViewById(R.id.file_view);
            textView = view.findViewById(R.id.text_view);
        }
    }
}