package com.oheers.fish.advents;

import com.oheers.fish.EvenMoreFish;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdventGui {
    private final String adventId;
    private final Gui gui;

    private final List<String> layoutFormat;
    private final List<String> planMaterials; //Get itemsstack so we can add addons support.
    private final String displayName;
    private final String fishName;
    private final List<String> fishLore;
    private final String largestLengthFormat;
    private final String lockedFishName;
    private final String lockedFishMaterial;
    private final List<String> lockedFishLore;


    public AdventGui(String adventId, List<String> layoutFormat, List<String> planMaterials, String displayName, String fishName, List<String> fishLore, String largestLengthFormat, String lockedFishName, String lockedFishMaterial, List<String> lockedFishLore) {
        this.adventId = adventId;
        this.layoutFormat = layoutFormat;
        this.planMaterials = planMaterials;
        this.displayName = displayName;
        this.fishName = fishName;
        this.fishLore = fishLore;
        this.largestLengthFormat = largestLengthFormat;
        this.lockedFishName = lockedFishName;
        this.lockedFishMaterial = lockedFishMaterial;
        this.lockedFishLore = lockedFishLore;

        this.gui = Gui.gui()
                .type(GuiType.CHEST)
                .title(Component.text(displayName))
                .rows(layoutFormat.size() - 1)
                .create()

        ;
        this.gui.disableAllInteractions();
        setFillerItems();
        //iterate over every row, in each row over every char.
    }
    public void open(final Player player) {
        gui.open(player);
    }

    private @NotNull List<ItemStack> parseFillerItems() {
        final List<ItemStack> items = new ArrayList<>();
        for (final String material : planMaterials) {
            ItemStack itemStack = getItemStackFromMaterial(material);
            items.add(itemStack);
        }
        return items;
    }

    private void setFillerItems() {
        final List<ItemStack> items = parseFillerItems();
        for (int row = 0; row < layoutFormat.size() - 1; row++) {
            final String layoutString = layoutFormat.get(row);
            for (int col = 0; col < layoutString.length() - 1; col++) {
                char c = layoutString.charAt(col);
                if (c == 'X') {
                    continue;
                }

                int layoutChar = Character.getNumericValue(c);
                GuiItem guiItem = new GuiItem(items.get(layoutChar));
                gui.setItem(row, col, guiItem);
            }
        }
    }

    private ItemStack getItemStackFromMaterial(final @NotNull String potentialMaterial) {
        if (!potentialMaterial.contains(":")) {
            return new ItemStack(getPotentialVanillaMaterialOrDefault(potentialMaterial));
        }

        final String prefix = potentialMaterial.split(":")[0];
        final String id = potentialMaterial.split(":")[1];
        return EvenMoreFish.getInstance().getAddonManager().getItemStack(prefix, id);
    }

    private Material getPotentialVanillaMaterialOrDefault(final String potentialMaterial) {
        final Material material = Material.matchMaterial(potentialMaterial);
        if (material == null) {
            return Material.GRAY_STAINED_GLASS;
        }
        return material;
    }
}
