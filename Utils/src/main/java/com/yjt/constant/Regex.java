package com.yjt.constant;

public enum Regex {

    NONE(""),
    UNKNOWN("?"),
    CHINESE_AREA_CODE("+86"),
    YUAN("￥"),
    EQUALS("="),
    PLUS("+"),
    MINUS("-"),
    SPACE(" "),
    COMMA(","),
    POINT("."),
    COLON(":"),
    SEMICOLON(";"),
    QUESTION_MARK("?"),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_ARROW("<"),
    LEFT_ARROW_SLASH("</"),
    LEFT_ARROW_ESCAPE("&lt;"),
    RIGHT_ARROW(">"),
    RIGHT_ARROW_ESCAPE("&gt"),
    LEFT_SLASH("/"),
    LINE_SEPARATOR("line.separator"),
    RIGHT_SLASH("\\"),
    VERTICAL("|"),
    UNDERLINE("_"),
    AND("&"),
    AND_ESCAPE("&amp;"),
    SUPERSCRIPT("#"),
    ENTER("[\r\n]+|[\n]+|[\r]+"),
    DATE_FORMAT_ALL("yyyy-MM-dd HH:mm:ss"),
    DATE_FORMAT1("yyyy-MM-dd"),
    CHMOD("chmod "),
    PERMISSION("777"),
    LOG("log"),
    LOG_TYPE(".log"),
    FILE_TTF("fonts/%s.ttf"),
    FILE_TYPE("application/vnd.android.package-archive"),
    IMAGE_JPG(".jpg"),
    ASMX(".asmx"),
    HTTP("http://"),
    HTTPS("https://"),
    DEFAULT_TYPE("application/x-www-form-urlencoded; charset=UTF-8"),
    STRING_TYPE("text/plain; charset=utf-8"),
    JSON_TYPE("application/json; charset=utf-8"),
    PNG("png"),
    PNG_TYPE("image/png; charset=UTF-8"),
    JPG("jpg"),
    JPEG("jpeg"),
    JPEG_TYPE("image/jpeg; charset=UTF-8"),
    PRAGMA("Pragma"),
    CACHE_CONTROL("Cache-Control"),
    MAX_STALE("max-stale=%d"),
    CHARSET("charset"),
    BKS("BKS"),
    TLS("TLS"),
    X509("X.509"),
    UTF_8("UTF-8");

    private String mRegext;

    Regex(String regex) {
        this.mRegext = regex;
    }

    public String getRegext() {
        return mRegext;
    }

}
