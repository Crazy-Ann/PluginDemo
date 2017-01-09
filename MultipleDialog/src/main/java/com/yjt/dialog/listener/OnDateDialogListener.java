package com.yjt.dialog.listener;

import java.util.Date;

public interface OnDateDialogListener extends OnDialogNegativeListener {

    void onPositiveButtonClicked(int requestCode, Date date);

}
