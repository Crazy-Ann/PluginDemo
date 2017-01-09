package com.yjt.plguin.constant;

public class Constant {

    public final static class UpdateException {
        public static final int DOWNLOAD_FAILED = 0x00001;       // 下载失败
        public static final int DOWNLOAD_CANCELED = 0x00002;       // 客户取消
        public static final int CREATE_TEMP_FILE_FAILED = 0x00003;        // 创建临时文件失败
        public static final int EXTRACT_ASSETS_FAILED = 0x00004;        // 解压 Assets 插件失败
        public static final int LACK_SPACE = 0x00005;       // 文件空间不够
        public static final int HTTP_REQUEST_FAILED = 0x00006;        // 请求网络插件信息失败
    }

    public final static class InstallException {
        public static final int FILE_NOT_FOUND = 0x10001;      // 文件丢失
        public static final int OBTAIN_PACKAGE_INFO_FAILED = 0x10002;   // 无法获取 PackageInfo
        public static final int SIGNATURE_ERROR = 0x10003;      // 签名不对
        public static final int INSTALL_FAILED = 0x10004;        // 安装失败
        public static final int LACK_SPACE = 0x10005;       // 文件空间不够
        public static final int OBTAIN_INSTALL_PATH_FAILED = 0x10006;   // 获取安装路径失败
    }

    public final static class LoadException {
        public static final int FILE_NOT_FOUND = 0x20001;      // 文件丢失
        public static final int CREATE_OPTIMIZED_DEX_DIR_FAILED = 0x20002;        // 创建 Optimized Dex 目录失败
        public static final int CREATE_SO_LIBRARY_DIR_FAILED = 0x20003;         // 创建 so库 目录失败
        public static final int INSTALL_SO_LIBRARY_FAILED = 0x20004;     // 安装 so库 失败
        public static final int CREATE_CLASS_LOADER_FAILED = 0x20005;    // 创建 ClassLoader 失败
        public static final int CREATE_ASSET_MANAGER_FAILED = 0x20006;  // 创建 AssetManager 失败
        public static final int OBTAIN_PLUGIN_CLASS_FAILED = 0x20007;          // 获取插件的 Class 失败
        public static final int OBTAIN_PLUGIN_BEHAVIOR_FAILED = 0x20008;       // 获取插件的 Behavior 失败
        public static final int OBTAIN_MANIFEST_BEHAVIOR_FAILED = 0x20009; // 通过Manifest自动获取插件 Behavior 失败
        public static final int PLUGIN_NOT_LOADED = 0x20010;     // 插件还没有被加载
        public static final int CREATE_PLUGIN_FAILED = 0x20011;  // 创建 PluginBaseInfo 对象失败
        public static final int PLUGIN_DEPENDENCY_FAILED = 0x20012;     // 无法满足插件依赖
        public static final int RETRY_ERROR = 0x20013;
    }

    public final static class Configuration {
        public static final int RETRY_MAXIMUM_TIMES_NONE = -9999;
        public static final int RETRY_MAXIMUM_TIMES = 3;
        public static final String PLUGIN_DIR = "plugin";
        public static final String OPTIMIZED_DEX_DIR = "code_cache";
        public static final String SO_LIBRARY_DIR = "lib";
        public static final String TEMP_SO_LIBRARY_DIR = "lib_cache";
        public static final String TEMP_FILE_SUFFIX = ".tmp";
        public static final String PLUGIN_NAME = "base-1.apk";
    }

    public final static class State {
        public static final int INITIATION = 0x30001;                           // 初始状态
        public static final int CANCELED = 0x30002;                      // 任务被取消
        public static final int REMOTE_INFO_OBTAIN_FAIL = 0x30003;          // 无法获取远程插件信息
        public static final int LOCAL_AND_REMOTE_PLUGIN_NON_EXISTENT = 0x30004;                 // 没有插件可用(在线和本地均没有)
        public static final int UPDATE_PLUGIN_UPDATE_FAIL = 0x30005;        // 插件更新失败
        public static final int PLUGIN_RELEASE_FROM_ASSETS = 0x30006;               // 准备从ASSETS释放插件
        public static final int PLUGIN_DOWNLOAD = 0x30007;              // 准备下载插件
        public static final int PLUGIN_DOWNLOAD_SUCCESS = 0x30008;// 下载插件成功
        public static final int PLUGIN_UPDATE_SUCCESS = 0x30009;                    // 插件更新完成、准备加载
        public static final int PLUGIN_LOADED_SUCCESS = 0x30010;                    // 插件已经加载成功
        public static final int PLUGIN_LOADED_FAIL = 0x30011;               // 插件加载失败
        public static final int ONLINE_PLUGIN_ILLEGAL = 0x30012;
    }

    public final static class Space {
        public static final int IO_BUFFER_CAPACITY = 1024 * 4;
        public static final int PLUGIN_MINIMUM_REQUIRED_CAPACITY = 10 * 1000 * 1000;
    }

    public final static class Manifest {
        public static final String DEFAULT_MANIFEST_NAME = "AndroidManifest.xml";
        public static final float RADIX_MULTS[] = {0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F};
        public static final String DIMENSION_UNITS[] = {"px", "dip", "sp", "pt", "in", "mm", "", ""};
        public static final String FRACTION_UNITS[] = {"%", "%p", "", "", "", "", "", ""};
        public static final int CHUNK_TYPE = 0x40001;
        public static final int ATTRIBUTE_IX_NAMESPACE_URI = 0;
        public static final int ATTRIBUTE_IX_NAME = 1;
        public static final int ATTRIBUTE_IX_VALUE_STRING = 2;
        public static final int ATTRIBUTE_IX_VALUE_TYPE = 3;
        public static final int ATTRIBUTE_IX_VALUE_DATA = 4;
        public static final int ATTRIBUTE_LENGHT = 5;
        public static final int CHUNK_AXML_FILE = 0x40002;
        public static final int CHUNK_RESOURCEIDS = 0x40003;
        public static final int CHUNK_XML_FIRST = 0x40004;
        public static final int CHUNK_XML_START_NAMESPACE = 0x40005;
        public static final int CHUNK_XML_END_NAMESPACE = 0x40006;
        public static final int CHUNK_XML_START_TAG = 0x40007;
        public static final int CHUNK_XML_END_TAG = 0x40008;
        public static final int CHUNK_XML_TEXT = 0x40009;
        public static final int CHUNK_XML_LAST = 0x40010;
        public static final String METHOD_NOT_SUPPORTED = "Method is not supported.";
        public static final int DEFAULT_VALUE = -1;
    }

    public final static class Mode {
        public static final int UPDATE = 0x50001;      //更新插件, 从远程下载最新版本的插件并拷贝到对应的安装路径
        public static final int LOAD = 0x50002;      //从对应的安装路径加载插件
    }

}
