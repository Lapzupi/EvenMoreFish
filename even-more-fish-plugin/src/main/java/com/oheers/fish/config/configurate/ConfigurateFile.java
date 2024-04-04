package com.oheers.fish.config.configurate;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.ConfigBase;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.logging.Level;

public abstract class ConfigurateFile extends ConfigBase {
    protected final YamlConfigurationLoader loader;
    protected final YamlConfigurationLoader.Builder loaderBuilder = this.loadBuilder();
    protected final String resourceFolder;
    protected CommentedConfigurationNode rootNode;
    protected Transformation transformation;


    protected ConfigurateFile(String resourceFolder, String fileName) throws ConfigurateException {
        super(fileName);
        this.resourceFolder = resourceFolder;
        this.loaderBuilder.defaultOptions(opts -> opts.serializers(this::builderOptions));

        this.loader = this.loaderBuilder.build();
        this.rootNode = this.loader.load();
        this.loadFile(new File(resourceFolder));

        this.transformation = this.getTransformation();
        if (this.transformation != null) {
            this.loader.save(this.transformation.updateNode(this.rootNode));
            this.rootNode = this.loader.load();
        }

        this.initValues();
        EvenMoreFish.debug(Level.INFO, "Loading " + fileName);
    }

    protected abstract void initValues() throws ConfigurateException;

    protected abstract YamlConfigurationLoader.Builder loadBuilder();

    protected abstract void builderOptions(TypeSerializerCollection.Builder builder);

    protected abstract Transformation getTransformation();

    public void reloadConfig() {
        try {
            this.rootNode = this.loader.load();
            this.initValues();
        } catch (ConfigurateException e) {
            getPlugin().getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void updateConfig() {
        //Overriden for use of transformations.
    }
}
