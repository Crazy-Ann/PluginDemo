package com.yjt.dialog.listener;

public interface OnNumberDialogListener extends OnDialogNegativeListener {

    void onPositiveButtonClicked(int requestCode, int number);
}
