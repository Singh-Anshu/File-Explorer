package com.anshusingh.fileexplorer;

import java.io.File;

public interface OnFileSelectedListner {

    void onFileClicked(File file);

    void onFileLongClicked(File file);

}
