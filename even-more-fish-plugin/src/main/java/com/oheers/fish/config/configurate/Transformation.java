package com.oheers.fish.config.configurate;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;


public abstract class Transformation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Transformation() {
    }

    public ConfigurationTransformation initialTransformation() {
        return ConfigurationTransformation.builder().addAction(NodePath.path("", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
            value.node("config-version").set(0);
            return null;
        }).build();
    }

    public abstract int getLatestVersion();

    protected abstract ConfigurationTransformation.Versioned create();

    public <N extends ConfigurationNode> N updateNode(@NotNull N node) throws ConfigurateException {
        if (!node.virtual()) {
            ConfigurationTransformation.Versioned transformation = this.create();
            int startVersion = transformation.version(node);
            transformation.apply(node);
            int endVersion = transformation.version(node);
            if (startVersion != endVersion) {
                this.logger.info(String.format("Updated config schema from %d to %d", startVersion, endVersion));
            }
        }

        return node;
    }
}
