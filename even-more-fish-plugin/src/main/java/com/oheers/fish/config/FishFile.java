package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class FishFile extends ConfigBase {

    private static FishFile instance = null;

    public FishFile() {
        super("fish.yml", EvenMoreFish.getInstance());
        instance = this;
    }

    public static FishFile getInstance() {
        return instance;
    }

    private ConfigurationSection getFishSection(final String rarityName, final String fishName) {
        return getConfig().getConfigurationSection("fish." + rarityName + "." + fishName);
    }

    public Integer getSetWorth(final String rarityName, final String fishName) {
        return getFishSection(rarityName, fishName).getInt("set-worth");
    }

    public String getDisplayName(final String rarityName, final String fishName) {
        return getFishSection(rarityName, fishName).getString("displayname");
    }
    public Double getWorthMultiplier(final String rarityName, final String fishName) {
        return getFishSection(rarityName, fishName).getDouble("worth-multiplier");
    }

    public boolean isDisableFisherman(final String rarityName, final String fishName, final boolean defaultValue) {
        return getFishSection(rarityName,fishName).getBoolean("disable-fisherman", defaultValue);
    }

    public boolean isDisableLore(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getBoolean("disable-lore", false);
    }

    public Double getMinSize(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getDouble("size.minSize");
    }

    public Double getMaxSize(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getDouble("size.maxSize");
    }

    public String getMessage(final String rarityName, final String fishName) {
        return getFishSection(rarityName, fishName).getString("message");
    }

    public String getEffect(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getString("effect");
    }

    public List<String> getLoreOverride(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("lore-override");
    }
    public List<String> getLore(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("lore");
    }

    public List<String> getEatEvent(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("eat-event");
    }

    public List<String> getCatchEvent(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("catch-event");
    }

    public List<String> getSellEvent(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("sell-event");
    }

    public List<String> getInteractEvent(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getStringList("interact-event");
    }

    public boolean isSilent(final String rarityName, final String fishName) {
        return getFishSection(rarityName,fishName).getBoolean("silent", false);
    }
}
