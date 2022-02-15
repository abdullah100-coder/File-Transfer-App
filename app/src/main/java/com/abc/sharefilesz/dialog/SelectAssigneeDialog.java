package com.abc.sharefilesz.dialog;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.abc.sharefilesz.R;
import com.abc.sharefilesz.activity.Activity;
import com.abc.sharefilesz.view.TextDrawable;
import com.abc.sharefilesz.model.ShowingAssignee;
import com.abc.sharefilesz.util.AppUtils;
import com.abc.sharefilesz.util.NetworkDeviceLoader;

import java.util.ArrayList;
import java.util.List;

public class SelectAssigneeDialog extends AlertDialog.Builder
{
    private List<ShowingAssignee> mList = new ArrayList<>();
    private LayoutInflater mInflater;
    private TextDrawable.IShapeBuilder mIconBuilder;

    public SelectAssigneeDialog(@NonNull Activity activity, List<ShowingAssignee> assigneeList,
                                DialogInterface.OnClickListener clickListener)
    {
        super(activity);

        mList.addAll(assigneeList);
        mInflater = LayoutInflater.from(activity);
        mIconBuilder = AppUtils.getDefaultIconBuilder(activity);

        if (assigneeList.size() > 0)
            setAdapter(new ListAdapter(), clickListener);
        else
            setMessage(R.string.text_listEmpty);

        setTitle(R.string.butn_useKnownDevice);
        setNegativeButton(R.string.butn_close, null);
    }

    private class ListAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return mList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.dialog_assignee_selector, parent,
                        false);

            ShowingAssignee assignee = (ShowingAssignee) getItem(position);
            ImageView image = convertView.findViewById(R.id.image);
            TextView text = convertView.findViewById(R.id.text);

            text.setText(assignee.device.nickname);
            NetworkDeviceLoader.showPictureIntoView(assignee.device, image, mIconBuilder);

            return convertView;
        }
    }
}
