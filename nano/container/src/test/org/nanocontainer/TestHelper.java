package org.nanocontainer;

import java.io.File;

public class TestHelper {

    public static File getTestCompJarFile() {
        String testcompJarProperty = System.getProperty("testcomp.jar");
        if (testcompJarProperty != null) {
            return new File(testcompJarProperty);
        }

        Class aClass = TestHelper.class;
        File base = new File(aClass.getProtectionDomain().getCodeSource().getLocation().getFile());
        File tj = new File(base,"src/test-comp/testcomp.jar");
        while (!tj.exists()) {
            base = base.getParentFile();
            tj = new File(base,"src/test-comp/testcomp.jar");
        }
        return tj;
    }


}
