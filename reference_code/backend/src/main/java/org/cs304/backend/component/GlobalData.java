package org.cs304.backend.component;

import org.springframework.stereotype.Component;

@Component
public class GlobalData {
    public static final String FILE_DIRECTORY = System.getProperty("user.dir") + "/files/";
}
