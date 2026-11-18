package me.deecaad.core.compatibility;

import com.cjcrafter.foliascheduler.util.MinecraftVersions;
import org.jetbrains.annotations.Nullable;

public class CompatibilitySetup {

    /**
     * Example return values:
     * 
     * <pre>
     * v1_8_R2
     * v1_11_R1
     * v1_13_R3
     * </pre>
     *
     * @return the server version as string
     */
    public String getVersionAsString() {
        String version = MinecraftVersions.getCurrent().toProtocolString();
        // FoliaScheduler 0.8 marks unknown drops (26.3) as v26_3_R-1.
        // That is not a class name. Use the R1 compatibility class.
        int unknown = version.indexOf("_R-");
        if (unknown >= 0) {
            return version.substring(0, unknown) + "_R1";
        }
        return version;
    }

    /**
     * @param interfaceClazz the compatibility interface type
     * @param directory the directory in code where compatibility should exist
     * @return the compatible version from given directory
     */
    @Nullable public <T> T getCompatibleVersion(Class<T> interfaceClazz, String directory) {
        String version = getVersionAsString();
        Class<?> compatibilityClass;
        try {
            compatibilityClass = Class.forName(directory + "." + version, false, interfaceClazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            return interfaceClazz.cast(compatibilityClass.getConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Found compatibility class " + compatibilityClass.getName() + " but failed to instantiate it", e);
        }
    }
}