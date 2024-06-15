package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;
import org.bukkit.configuration.ConfigurationSection;

public class RaritiesFile extends ConfigBase {

    private static RaritiesFile instance = null;

    public RaritiesFile() {
        super("rarities.yml", EvenMoreFish.getInstance());
        instance = this;
    }

    public static RaritiesFile getInstance() {
        return instance;
    }

    private ConfigurationSection getRaritiesSection() {
        return getConfig().getConfigurationSection("rarities");
    }

    public Boolean isDisableFisherman(final String rarityName) {
        return getRaritiesSection().getBoolean(rarityName + ".disable-fisherman", false);
    }

    public Double getMaxSize(final String rarityName) {
        return getRaritiesSection().getDouble(rarityName + ".size.maxSize");
    }

    public Double getMinSize(final String rarityName) {
        return getRaritiesSection().getDouble(rarityName + ".size.minSize");
    }

    public Double getWorthMultiplier(final String rarityName) {
        return getRaritiesSection().getDouble(rarityName + ".worth-multiplier");
    }

    public boolean isBroadcast(final String rarityName) {
        return getRaritiesSection().getBoolean(rarityName + ".broadcast", true);
    }

    public String getOverridenLore(final String rarityName) {
        return getRaritiesSection().getString(rarityName + ".override-lore");
    }

    public boolean isUseThisCasing(final String rarityName) {
        return getRaritiesSection().getBoolean(rarityName + ".use-this-casing", false);
    }

    public String getDisplayName(final String rarityName) {
        return getRaritiesSection().getString(rarityName + ".displayname");
    }

    public String getPermission(final String rarityName) {
        return getRaritiesSection().getString(rarityName + ".permission");
    }

    public String getColour(final String rarityName) {
        return getRaritiesSection().getString(rarityName + ".colour", "&f");
    }

    public double getWeight(String rarityName) {
        return getRaritiesSection().getDouble(rarityName + ".weight");
    }

}
