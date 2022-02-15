package com.abc.sharefilesz.view;

import com.abc.sharefilesz.fragment.EditableListFragment;
import com.abc.sharefilesz.widget.EditableListAdapter;

public interface EditableListFragmentModelImpl<V extends EditableListAdapter.EditableViewHolder>
{
    void setLayoutClickListener(EditableListFragment.LayoutClickListener<V> clickListener);
}
