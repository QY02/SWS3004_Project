package org.eventCenter.fileServer.component;

import org.springframework.stereotype.Component;

@Component
public class GlobalData {
    public static final String FILE_DIRECTORY = System.getProperty("user.dir") + "/data/files/";
}
