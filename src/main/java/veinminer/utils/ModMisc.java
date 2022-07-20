package veinminer.utils;

import static veinminer.objects.Constants.*;

public class ModMisc {

    public static String getModVersion() {
        return MOD_VERSION;
    }

    public static boolean isGreaterVersion(String version) {
        String[] versions = version.split("\\.");
        double major = Integer.parseInt(versions[0]);
        double minor = Double.parseDouble(versions[1]);
        return major < MAJOR_VERSION || (major <= MAJOR_VERSION && minor < MINOR_VERSION);
    }

    public static boolean isLesserVersion(String version) {
        String[] versions = version.split("\\.");
        double major = Integer.parseInt(versions[0]);
        double minor = Double.parseDouble(versions[1]);
        return major > MAJOR_VERSION || (major >= MAJOR_VERSION && minor > MINOR_VERSION);
    }

    public static boolean isEqualVersion(String version) {
        String[] versions = version.split("\\.");
        double major = Integer.parseInt(versions[0]);
        double minor = Double.parseDouble(versions[1]);
        return major == MAJOR_VERSION && minor == MINOR_VERSION;
    }
}
