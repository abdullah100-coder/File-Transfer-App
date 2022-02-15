package com.abc.sharefilesz.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.abc.sharefilesz.R;


abstract public class AbstractSingleTextInputDialog extends AbstractFailureAwareDialog
{
    private EditText mEditText;
    private ViewGroup mView;

    public AbstractSingleTextInputDialog(final Context context)
    {
        super(context);

        mView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
        mEditText = mView.findViewById(R.id.layout_dialog_single_text_input_text);

        setView(mView);
        setTitle(R.string.text_createFolder);
        setNegativeButton(R.string.butn_close, null);

        mEditText.requestFocus();
    }

    public ViewGroup getContainerView()
    {
        return mView;
    }

    public EditText getEditText()
    {
        return mEditText;
    }
}
