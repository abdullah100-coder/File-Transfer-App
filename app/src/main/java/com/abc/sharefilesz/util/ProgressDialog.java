package com.abc.sharefilesz.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.abc.sharefilesz.R;

import java.text.NumberFormat;

public class ProgressDialog extends AlertDialog
{
    public static final int STYLE_SPINNER = 0;

    public static final int STYLE_HORIZONTAL = 1;

    private ProgressBar mProgress;
    private TextView mMessageView;

    private int mProgressStyle = STYLE_SPINNER;
    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;

    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    private boolean mHasStarted;
    private Handler mViewUpdateHandler;

    
    public ProgressDialog(Context context)
    {
        super(context);
        initFormats();
    }

    
    public ProgressDialog(Context context, int theme)
    {
        super(context, theme);
        initFormats();
    }

    
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message)
    {
        return show(context, title, message, false);
    }

    
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate)
    {
        return show(context, title, message, indeterminate, false, null);
    }

    
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate, boolean cancelable)
    {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate,
                                      boolean cancelable, OnCancelListener cancelListener)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    private void initFormats()
    {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.dialog_progress, null);

        
        mViewUpdateHandler = new ViewUpdateHandler();
        mProgress = view.findViewById(R.id.progress);
        mProgressNumber = view.findViewById(R.id.progress_number);
        mProgressPercent = view.findViewById(R.id.progress_percent);
        setView(view);

        if (mMax > 0)
            setMax(mMax);

        if (mProgressVal > 0)
            setProgress(mProgressVal);

        if (mSecondaryProgressVal > 0)
            setSecondaryProgress(mSecondaryProgressVal);

        if (mIncrementBy > 0)
            incrementProgressBy(mIncrementBy);

        if (mIncrementSecondaryBy > 0)
            incrementSecondaryProgressBy(mIncrementSecondaryBy);

        if (mProgressDrawable != null)
            setProgressDrawable(mProgressDrawable);

        if (mIndeterminateDrawable != null)
            setIndeterminateDrawable(mIndeterminateDrawable);

        if (mMessage != null)
            setMessage(mMessage);

        setIndeterminate(mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mHasStarted = false;
    }

    
    public int getProgress()
    {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    
    public void setProgress(int value)
    {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    
    public int getSecondaryProgress()
    {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    
    public void setSecondaryProgress(int secondaryProgress)
    {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    
    public int getMax()
    {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    
    public void setMax(int max)
    {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    
    public void incrementProgressBy(int diff)
    {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    
    public void incrementSecondaryProgressBy(int diff)
    {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    
    public void setProgressDrawable(Drawable d)
    {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    
    public void setIndeterminateDrawable(Drawable d)
    {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    
    public boolean isIndeterminate()
    {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    
    public void setIndeterminate(boolean indeterminate)
    {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    @Override
    public void setMessage(CharSequence message)
    {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                super.setMessage(message);
            } else {
                mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }

    
    public void setProgressStyle(int style)
    {
        mProgressStyle = style;
    }

    
    public void setProgressNumberFormat(String format)
    {
        mProgressNumberFormat = format;
        onProgressChanged();
    }

    
    public void setProgressPercentFormat(NumberFormat format)
    {
        mProgressPercentFormat = format;
        onProgressChanged();
    }

    private void onProgressChanged()
    {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }

    private class ViewUpdateHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            
            int progress = mProgress.getProgress();
            int max = mProgress.getMax();
            if (mProgressNumberFormat != null) {
                String format = mProgressNumberFormat;
                mProgressNumber.setText(String.format(format, progress, max));
            } else {
                mProgressNumber.setText("");
            }
            if (mProgressPercentFormat != null) {
                double percent = (double) progress / (double) max;
                SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                        0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mProgressPercent.setText(tmp);
            } else {
                mProgressPercent.setText("");
            }
        }
    }
}
