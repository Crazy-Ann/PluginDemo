package com.yjt.engine.listener;

import java.io.File;
import java.util.Set;

/**
 * 插件so行为接口
 */
public interface SoLibraryBehaviorListener extends PluginBehaviorListener {

    void loadSoLibraryy();

    Set<File> getSoLibrary();
}
