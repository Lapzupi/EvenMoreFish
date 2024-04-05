package com.oheers.fish.config.configurate.serializers;


import com.oheers.fish.fishing.items.Rarity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class RarityTypeSerializer implements TypeSerializer<Rarity> {
    @Override
    public Rarity deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final String displayName = node.node("displayname").getString();
        final int weight = node.node("weight").getInt();
        final String colour = node.node("colour").getString();
        final int worthMultiplier = node.node("worth-multiplier").getInt();
        final boolean broadcast = node.node("broadcast").getBoolean();

        final int minSize = node.node("size").node("minSize").getInt();
        final int maxSize = node.node("size").node("maxSize").getInt();

        Rarity rarity = new Rarity(displayName, colour, weight, broadcast, null);
        
        return rarity;
    }

    @Override
    public void serialize(Type type, @Nullable Rarity obj, ConfigurationNode node) throws SerializationException {
        // read-only
    }
}
