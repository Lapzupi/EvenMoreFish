package com.oheers.fish.advents;


import com.oheers.fish.exceptions.InvalidFishException;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;

/**
 * An advent fish is a normal fish that can be obtained only during advents.
 */
public class AdventFish extends Fish {
    private int day;

    public AdventFish(Rarity rarity, String name, boolean isXmas2022Fish, int day) throws InvalidFishException {
        //isXmas2022Fish is for legacy compat, and until this feature is fully implemented
        super(rarity, name, isXmas2022Fish);
        this.day = day;
    }
}
