package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.FileUtil;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ConfigBase {

    private final String fileName;
    private final String resourceName;
    private final Plugin plugin;
    private final boolean configUpdater;

    private YamlDocument config = null;
    private File file = null;

    public ConfigBase(@NotNull String fileName, @NotNull String resourceName, @NotNull Plugin plugin, boolean configUpdater) {
        this.fileName = fileName;
        this.resourceName = resourceName;
        this.plugin = plugin;
        this.configUpdater = configUpdater;
        reload();
    }

    public void reload() {
        File configFile = FileUtil.loadFileOrResource(getPlugin().getDataFolder(), getFileName(), getResourceName(), getPlugin());
        if (configFile == null) {
            return;
        }

        List<Settings> settingsList = new ArrayList<>(Arrays.asList(
                GeneralSettings.builder().setUseDefaults(false).build(),
                DumperSettings.DEFAULT
        ));

        if (configUpdater) {
            settingsList.add(LoaderSettings.builder().setAutoUpdate(true).build());
            settingsList.add(UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        }

        final Settings[] settings = settingsList.toArray(new Settings[0]);

        try {
            InputStream resource = EvenMoreFish.getInstance().getResource(getResourceName());
            if (resource == null) {
                this.config = YamlDocument.create(configFile, settings);
            } else {
                this.config = YamlDocument.create(configFile, resource, settings);
            }
            this.file = configFile;
            if (configUpdater) {
                this.config.update();
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public YamlDocument getConfig() {
        if (this.config == null) {
            throw new RuntimeException(getFileName() + " has not loaded properly. Please check for startup errors.");
        }
        return this.config;
    }

    public File getFile() { return this.file; }

    public Plugin getPlugin() { return this.plugin; }

    public String getFileName() { return this.fileName; }

    public String getResourceName() { return this.resourceName; }

}
