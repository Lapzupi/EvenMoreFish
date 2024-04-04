package com.oheers.fish.advents;


import com.oheers.fish.fishing.items.Rarity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An advent event. "Unlocks" special fish & rarities that can only be obtained during the event.
 */
public class Advent {
    private final String id;
    private final String displayName;

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final List<Rarity> rarities;
    private final List<AdventFish> adventFish;

    private final AdventGui adventGui;

    public Advent(String id, String displayName, LocalDateTime startDate, LocalDateTime endDate, List<Rarity> rarities, List<AdventFish> adventFish, AdventGui adventGui) {
        this.id = id;
        this.displayName = displayName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rarities = rarities;
        this.adventFish = adventFish;
        this.adventGui = adventGui;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public LocalDateTime startDate() {
        return startDate;
    }

    public LocalDateTime endDate() {
        return endDate;
    }

    public List<Rarity> rarities() {
        return rarities;
    }

    public List<AdventFish> adventFish() {
        return adventFish;
    }

    public AdventGui adventGui() {
        return adventGui;
    }
}
