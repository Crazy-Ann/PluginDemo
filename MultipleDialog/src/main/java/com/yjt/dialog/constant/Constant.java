package com.yjt.dialog.constant;

public class Constant {

    private Constant() {}

    public static class View {
        public static final int COLOR_DEFAULT = 0x9999;
        public static final int SIZE_DEFAULT = 0x9999;
        public static final int RESOURCE_DEFAULT = 0x9999;
        public static final int DRAWABLE_TOP = 0x1001;
        public static final int DRAWABLE_LEFT = 0x1002;
        public static final int DRAWABLE_RIGHT = 0x1003;
        public static final int DRAWABLE_BOTTOM = 0x1004;
        public static final String CUSTOM_DIALOG = "custom_dialog";
        public static final String ROBOTO_REGULAR = "Roboto-Regular";
        public static final String ROBOTO_MEDIUM = "Roboto-Medium";
        public static final long CLICK_PERIOD = -500;
        public static final long CLICK_INTERVAL_PERIOD = 1000;
        public static final long CLICK_INTERVAL = 60 * 1000;
    }

    public static class RequestCode {
        public static final int DIALOG = 0x5001;
        public static final int DIALOG_PROMPT_CANCEL = 0x5002;
        public static final int DIALOG_PROMPT_DOWNLOAD = 0x5003;
    }
}
