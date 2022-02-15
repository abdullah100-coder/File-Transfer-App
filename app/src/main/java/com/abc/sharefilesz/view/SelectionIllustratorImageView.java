package com.abc.sharefilesz.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class SelectionIllustratorImageView extends AppCompatImageView
{
    public SelectionIllustratorImageView(Context context)
    {
        super(context);
    }

    public SelectionIllustratorImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SelectionIllustratorImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);

        if (selected) {
            getDrawable().setVisible(false, false);
        } else
            getDrawable().setVisible(true, false);
    }
}
