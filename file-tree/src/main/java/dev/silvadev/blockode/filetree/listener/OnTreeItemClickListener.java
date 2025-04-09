package dev.silvadev.blockode.filetree.listener;

import android.view.View;

public interface OnTreeItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}