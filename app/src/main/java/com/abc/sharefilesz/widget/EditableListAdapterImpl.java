package com.abc.sharefilesz.widget;

import com.abc.sharefilesz.util.NotReadyException;
import com.abc.sharefilesz.model.Editable;
import com.genonbeta.android.framework.widget.ListAdapterImpl;

import java.util.List;

public interface EditableListAdapterImpl<T extends Editable> extends ListAdapterImpl<T>
{
    boolean filterItem(T item);

    T getItem(int position) throws NotReadyException;

    void notifyAllSelectionChanges();

    void notifyItemChanged(int position);

    void notifyItemRangeChanged(int positionStart, int itemCount);

    void syncSelectionList();

    void syncSelectionList(List<T> itemList);
}
