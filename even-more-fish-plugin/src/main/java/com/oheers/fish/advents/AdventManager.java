package com.oheers.fish.advents;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.configurate.ConfigUtil;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AdventManager {
    private static AdventManager instance = null;
    private static final String ADVENT_FOLDER_PATH = "advents";
    private final File adventFolder = new File(EvenMoreFish.getInstance().getDataFolder(), ADVENT_FOLDER_PATH);


    //id + advent
    private Map<String, AdventConfig> adventMap;


    public AdventManager() {
        this.adventMap = new HashMap<>();
    }

    /**
     * Loads the advent configs for the first time
     */
    public void load() {
        final File[] adventFiles = adventFolder.listFiles(ConfigUtil.yamlFiles());
        if (adventFiles == null) {
            return;
        }

        final List<String> adventFileNames = Arrays.stream(adventFiles).map(File::getName).collect(Collectors.toList());
        for (final String name: adventFileNames) {
            try {
                final AdventConfig config = new AdventConfig(name);
                adventMap.put(config.getAdvent().id(), config);
            } catch (ConfigurateException e) {
                EvenMoreFish.getInstance().getLogger().warning(e.getMessage());
            }
        }

    }

    /**
     * Loads any new files & removes old ones.
     * Reloads existing configs if at all.
     */
    public void reload() {

    }

    public static AdventManager getInstance() {
        if (instance == null) {
            instance = new AdventManager();
        }
        return instance;
    }

    public Advent getAdvent(final String name) {
        return adventMap.get(name).getAdvent();
    }

}
