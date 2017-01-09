package com.yjt.dialog;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yjt.dialog.constant.Constant;
import com.yjt.dialog.constant.Temp;
import com.yjt.dialog.base.BaseDialogFragment;
import com.yjt.dialog.builder.ImagePromptDialogBuilder;
import com.yjt.dialog.listener.OnDialogNegativeListener;
import com.yjt.dialog.listener.OnDialogNeutralListener;
import com.yjt.dialog.listener.OnDialogPositiveListener;
import com.yjt.utils.BundleUtil;
import com.yjt.utils.ViewUtil;

public class ImagePromptDialog extends BaseDialogFragment {

    @Override
    protected Builder build(Builder builder) {
        CharSequence title = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_TITLE.getContent());
        int image = BundleUtil.getInstance().getIntData(getArguments(), Temp.DIALOG_PROMPT_IMAGE.getContent());
        CharSequence prompt = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_PROMPT.getContent());
        CharSequence positive = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_BUTTON_POSITIVE.getContent());
        CharSequence negative = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_BUTTON_NEGATIVE.getContent());
        CharSequence neutral = BundleUtil.getInstance().getCharSequenceData(getArguments(), Temp.DIALOG_BUTTON_NEUTRAL.getContent());
        View view = builder.getLayoutInflater().inflate(R.layout.dialog_image_prompt, null);
        builder.setView(view);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (image != Constant.View.RESOURCE_DEFAULT && image != 0) {
            Glide.with(getContext())
                    .load(image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) ViewUtil.getInstance().findView(view, R.id.ivPromptImage));
        }
        if (!TextUtils.isEmpty(prompt)) {
            ((TextView) ViewUtil.getInstance().findView(view, R.id.tvPrompt)).setText(prompt);
        }
        if (!TextUtils.isEmpty(positive)) {
            builder.setPositiveButton(positive, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (OnDialogPositiveListener listener : getDialogListeners(OnDialogPositiveListener.class)) {
                        listener.onPositiveButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(negative)) {
            builder.setNegativeButton(negative, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (OnDialogNegativeListener listener : getDialogListeners(OnDialogNegativeListener.class)) {
                        listener.onNegativeButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(neutral)) {
            builder.setNeutralButton(neutral, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (OnDialogNeutralListener listener : getDialogListeners(OnDialogNeutralListener.class)) {
                        listener.onNeutralButtonClicked(mRequestCode);
                    }
                    dismiss();
                }
            });
        }
        return builder;
    }

    public static ImagePromptDialogBuilder createBuilder(FragmentManager fragmentManager) {
        return new ImagePromptDialogBuilder(fragmentManager, ImagePromptDialog.class);
    }
}
