package com.yjt.plguin.listener;

import java.io.File;
import java.util.Set;

public interface SoLibraryBehaviorListener extends PluginBehaviorListener {

    void loadSoLibraryy();

    Set<File> getSoLibrary();
}
