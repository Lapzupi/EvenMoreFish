package com.oheers.fish.advents;


import com.oheers.fish.config.configurate.ConfigurateFile;
import com.oheers.fish.config.configurate.Transformation;
import com.oheers.fish.fishing.items.Rarity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class AdventConfig extends ConfigurateFile {
    private Advent advent;

    public AdventConfig(String fileName) throws ConfigurateException {
        super("advents", fileName);
    }

    public Advent getAdvent() {
        return advent;
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.advent = rootNode.get(Advent.class);
    }

    @Override
    protected YamlConfigurationLoader.Builder loadBuilder() {
        return YamlConfigurationLoader.builder().path(Paths.get(resourceFolder + File.separator + fileName)).nodeStyle(NodeStyle.BLOCK);
    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        builder.register(Advent.class,new AdventSerializer());
    }

    @Override
    protected Transformation getTransformation() {
        //nothing currently
        return null;
    }

    public static class AdventSerializer implements TypeSerializer<Advent> {

        @Override
        public Advent deserialize(Type type, @NotNull ConfigurationNode node) throws SerializationException {
            final ConfigurationNode eventNode = node.node("event");
            final String id = eventNode.node("id").getString();
            final String displayName = eventNode.node("displayname").getString(id.replace("_", " "));
            final LocalDateTime startDate = LocalDateTime.parse(eventNode.node("start-date").getString());
            final LocalDateTime endDate = LocalDateTime.parse(eventNode.node("end-date").getString());

            final ConfigurationNode guiNode = node.node("gui");
            final ConfigurationNode fillerSettingsNode = guiNode.node("filler-settings");
            final List<String> layoutFormat = fillerSettingsNode.node("layout").getList(String.class, Collections.emptyList());
            final List<String> plan = fillerSettingsNode.node("plan").getList(String.class, Collections.emptyList());
            final String guiTitle = guiNode.node("title").getString(displayName);
            final String fishTitle = guiNode.node("fish-title").getString();
            final List<String> fishLore = guiNode.node("fish-lore").getList(String.class, Collections.emptyList());
            final String largestLengthFormat = guiNode.node("largest-length-format").getString();
            final String lockedFishName = guiNode.node("locked-fish-name").getString();
            final String lockedFishMaterial = guiNode.node("locked-fish-material").getString();
            final List<String> lockedFishLore = guiNode.node("locked-fish-lore").getList(String.class, Collections.emptyList());

            final AdventGui adventGui = new AdventGui(id, layoutFormat, plan, guiTitle, fishTitle, fishLore, largestLengthFormat, lockedFishName, lockedFishMaterial, lockedFishLore);
            final ConfigurationNode rarityNode = node.node("rarities");
            List<Rarity> rarities = rarityNode.getList(Rarity.class);

            final ConfigurationNode fishNode = node.node("fish");
            final List<AdventFish> adventFish = fishNode.node("fish").getList(AdventFish.class);

            return new Advent(id, displayName, startDate, endDate, rarities, adventFish, adventGui);
        }

        @Override
        public void serialize(Type type, @Nullable Advent obj, ConfigurationNode node) throws SerializationException {
            //This is a read-only config. It must be changed through the config files. Then the plugin must be reloaded.
        }
    }
}
