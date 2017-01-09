package com.yjt.dialog;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yjt.dialog.base.BaseDialogFragment;
import com.yjt.dialog.builder.ProgressDialogBuilder;
import com.yjt.dialog.constant.Temp;
import com.yjt.utils.BundleUtil;
import com.yjt.utils.ViewUtil;

public class ProgressDialog extends BaseDialogFragment {

    @Override
    protected Builder build(Builder builder) {
        CharSequence title = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_TITLE.getContent());
        CharSequence prompt = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_PROMPT.getContent());
        View view = builder.getLayoutInflater().inflate(R.layout.dialog_progress, null);
        builder.setView(view);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(prompt)) {
            ((TextView) ViewUtil.getInstance().findView(view, R.id.tvPrompt)).setText(prompt);
        }
        return builder;
    }

    public static ProgressDialogBuilder createBuilder(FragmentManager fragmentManager) {
        return new ProgressDialogBuilder(fragmentManager, ProgressDialog.class);
    }
}
