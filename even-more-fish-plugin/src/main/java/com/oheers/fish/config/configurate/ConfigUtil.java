package com.oheers.fish.config.configurate;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FilenameFilter;

public class ConfigUtil {
    private ConfigUtil() {
        throw new UnsupportedOperationException("Util class");
    }

    /**
     * @return a filename filter for .yml files.
     */
    @Contract(pure = true)
    public static @NotNull FilenameFilter yamlFiles() {
        return ((dir, name) -> name.toLowerCase().endsWith(".yml"));
    }
}
