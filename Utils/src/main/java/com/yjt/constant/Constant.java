package com.yjt.constant;

/**
 * Created by yjt on 2016/12/28.
 */

public class Constant {

    private Constant() {}

    public static class LOG_LEVEL {
        public static final int VERBOSE = 0;
        public static final int DEBUG = 1;
        public static final int INFO = 2;
        public static final int WARNING = 3;
        public static final int ERROR = 4;
        public static final int FATAL = 5;
        public static final int LEVEL_NONE = 6;
    }

    public static class Extra {
        public static final String RESULT_MESSAGE = "result_message";
        public static final String RESULT_CODE = "result_code";
    }

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

    public static class Data {
        public final static String KEY = "39EB339F80B715384793F7EF";
        public final static String FROMAT = "ToHex16";
        public static final String ALGORITHM_CBC = "DES/ECB/NoPadding";// "DES/CBC/NoPadding";
        public static final String ALGORITHM_ECB = "DES/ECB/NoPadding";
        public static final String ALGORITHM0 = "DES";
        public static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        public static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        public static final byte[] TABLE = {
                (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
                (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
                (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
                (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
                (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y',
                (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
                (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i',
                (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
                (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's',
                (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
                (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2',
                (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                (byte) '8', (byte) '9', (byte) '+', (byte) '/'
        };
    }
}
