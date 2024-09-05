package com.oheers.fish.baits;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.config.BaitFile;
import com.oheers.fish.config.messages.Message;
import com.oheers.fish.exceptions.MaxBaitReachedException;
import com.oheers.fish.exceptions.MaxBaitsReachedException;
import com.oheers.fish.utils.nbt.NbtKeys;
import com.oheers.fish.utils.nbt.NbtUtils;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * FIXME
 * The bait is handled by nbt.
 * The lore should be checked when a bait is used or when a bait is applied.
 * A change in the format shouldn't impact the lore, perhaps we cache the older format:
 *  user has a format
 *  user changes to b format, a format is cached.
 *  plugin tried to apply b format, error, tries to get a format and apply new b format.?
 * Maybe set the bait lore in specific lines?
 */
public class BaitNBTManager {
    private BaitNBTManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether the item has nbt to suggest it is a bait object.
     *
     * @param itemStack The item stack that could potentially be a bait.
     * @return If the item stack is a bait or not (or if itemStack is null)
     */
    public static boolean isBaitObject(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.hasItemMeta()) {
            return NbtUtils.hasKey(itemStack, NbtKeys.EMF_BAIT);
        } else {
            return false;
        }
    }

    /**
     * @param itemStack The item stack that is a bait.
     * @return The name of the bait.
     */
    public static @Nullable String getBaitName(@NotNull ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            return NbtUtils.getString(itemStack, NbtKeys.EMF_BAIT);
        }
        return null;
    }

    /**
     * Gives an ItemStack the nbt required for the plugin to see it as a valid bait that can be applied to fishing rods.
     * It is inadvisable to use a block as a bait, as these will lose their nbt tags if they're placed - and the plugin
     * will forget that it was ever a bait.
     *
     * @param item The item stack being turned into a bait.
     * @param bait The name of the bait to be applied.
     */
    //todo, NBT.modify directly modifies the itemstack, there shouldn't be a need to return it..
    public static ItemStack applyBaitNBT(ItemStack item, String bait) {
        if (item == null) {
            return null;
        }

        NBT.modify(item, nbt -> {
            nbt.getOrCreateCompound(NbtKeys.EMF_COMPOUND).setString(NbtKeys.EMF_BAIT, bait);
        });
        return item;
    }

    /**
     * This checks against the item's NBTs to work out whether the fishing rod passed through has applied baits.
     *
     * @param itemStack The fishing rod that could maybe have bait NBTs applied.
     * @return Whether the fishing rod has bait NBT.
     */
    public static boolean isBaitedRod(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getType() != Material.FISHING_ROD) {
            return false;
        }

        if (itemStack.hasItemMeta()) {
            return NbtUtils.hasKey(itemStack, NbtKeys.EMF_APPLIED_BAIT);
        }

        return false;
    }

    /**
     * This applies a bait NBT reference to a fishing rod, and also checks whether the bait is already applied,
     * making an effort to increase it rather than apply it.
     *
     * @param item     The fishing rod having its bait applied.
     * @param bait     The name of the bait being applied.
     * @param quantity The number of baits being applied. These must be of the same bait.
     * @return An ApplicationResult containing the updated "item" itemstack and the remaining baits for the cursor.
     * @throws MaxBaitsReachedException When too many baits are tried to be applied to a fishing rod.
     * @throws MaxBaitReachedException  When one of the baits has hit maximum set by max-baits in baits.yml
     */
    public static ApplicationResult applyBaitedRodNBT(ItemStack item, Bait bait, int quantity) throws MaxBaitsReachedException, MaxBaitReachedException {
        boolean doingLoreStuff = BaitFile.getInstance().doRodLore();
        AtomicBoolean maxBait = new AtomicBoolean(false);
        AtomicInteger cursorModifier = new AtomicInteger();

        @Deprecated StringBuilder combined = new StringBuilder();
        if (isBaitedRod(item)) {
            try {
                if (doingLoreStuff) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(deleteOldLore(item));
                    item.setItemMeta(meta);
                }
            } catch (IndexOutOfBoundsException exception) {
                EvenMoreFish.getInstance()
                        .getLogger()
                        .severe("Failed to apply bait: " + bait.getName() + " to a user's fishing rod. This is likely caused by a change in format in the baits.yml config.");
                return null;
            }

            String[] baitList = NbtUtils.getBaitArray(item);

            boolean foundBait = false;
            //baitName = "name:quantity"
            for (String baitName : baitList) {
                final String[] splitBaitName = baitName.split(":");
                final String potentialBaitName = splitBaitName[0];
                if (potentialBaitName.equals(bait.getName())) {
                    //todo, i feel like this is here twice
                    final int currentQuantity = Integer.parseInt(splitBaitName[1]);
                    final int newQuantity = currentQuantity + quantity;

                    if (newQuantity > bait.getMaxApplications() && bait.getMaxApplications() != -1) {
                        combined.append(potentialBaitName).append(":").append(bait.getMaxApplications()).append(",");
                        // new cursor amt = -(max app - old app)
                        cursorModifier.set(-bait.getMaxApplications() + (newQuantity - quantity));
                        maxBait.set(true);
                    } else if (newQuantity != 0) {
                        combined.append(potentialBaitName).append(":").append(currentQuantity + quantity).append(",");
                        cursorModifier.set(-quantity);
                    }
                    foundBait = true;
                } else {
                    combined.append(baitName).append(",");
                }
            }

            // We can manage the last character not being a colon if we have to add it in ourselves.
            if (!foundBait) {

                if (getNumBaitsApplied(item) >= BaitFile.getInstance().getMaxBaits()) {
                    // the lore's been taken out, we're not going to be doing anymore here, so we're just re-adding it now.
                    if (doingLoreStuff) {
                        ItemMeta rodMeta = item.getItemMeta();
                        rodMeta.setLore(newApplyLore(item));
                        item.setItemMeta(rodMeta);
                    }
                    throw new MaxBaitsReachedException("Max baits reached.", new ApplicationResult(item, cursorModifier.get()));
                }
                //todo, i feel like this is here twice
                if (isMaxBait(quantity,bait)) {
                    cursorModifier.set(-bait.getMaxApplications());
                    combined.append(bait.getName()).append(":").append(bait.getMaxApplications());
                    maxBait.set(true);
                } else {
                    combined.append(bait.getName()).append(":").append(quantity);
                    cursorModifier.set(-quantity);
                }
            } else {
                if (!combined.isEmpty()) {
                    combined.deleteCharAt(combined.length() - 1);
                }
            }
            NBT.modify(item, nbt -> {
                ReadWriteNBT emfCompound = nbt.getOrCreateCompound(NbtKeys.EMF_COMPOUND);
                /*
                TODO
                Currently we set baits with emf-applied-bait: "baitname:quanity,baitname2:quantity"
                Instead we should use a list:
                    "emf-applied-bait":
                        - "baitname:quantity"
                        - "baitname2:quantity"
                 */
                if (!combined.isEmpty()) {
                    emfCompound.setString(NbtKeys.EMF_APPLIED_BAIT, combined.toString());
                } else {
                    emfCompound.removeKey(NbtKeys.EMF_APPLIED_BAIT);
                }
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT compound = nbt.getOrCreateCompound(NbtKeys.EMF_COMPOUND);
                if (isMaxBait(quantity, bait)) {
                    combined.append(bait.getName()).append(":").append(bait.getMaxApplications());
                    compound.setString(NbtKeys.EMF_APPLIED_BAIT, combined.toString());
                    cursorModifier.set(-bait.getMaxApplications());
                    maxBait.set(true);
                } else {
                    combined.append(bait.getName()).append(":").append(quantity);
                    compound.setString(NbtKeys.EMF_APPLIED_BAIT, combined.toString());
                    cursorModifier.set(-quantity);
                }
            });
        }

        if (doingLoreStuff && !combined.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(newApplyLore(item));
            item.setItemMeta(meta);
        }

        if (maxBait.get()) {
            throw new MaxBaitReachedException(bait, new ApplicationResult(item, cursorModifier.get()));
        }

        return new ApplicationResult(item, cursorModifier.get());
    }

    private static boolean isMaxBait(final int quantity, final Bait bait) {
        return quantity > bait.getMaxApplications() && bait.getMaxApplications() != -1;
    }

    private static List<Bait> getExistingBaitsFromRod(final ItemStack fishingRod) {
        final List<Bait> baits = new ArrayList<>();
        for (String baitName : NbtUtils.getStringListOrStringBait(fishingRod)) {
            final String[] splitBaitName = baitName.split(":");
            final Bait bait = EvenMoreFish.getInstance().getBaits().get(splitBaitName[0]);
            if (bait != null) {
                baits.add(bait);
            }
        }

        return baits;
    }

    /**
     * This fetches a random bait applied to the rod, based on the application-weight of the baits (if they exist). The
     * weight defaults to "1" if there is no value applied for them.
     *
     * @param fishingRod The fishing rod.
     * @return A random bait applied to the fishing rod.
     */
    public static Bait randomBaitApplication(ItemStack fishingRod) {
        if (fishingRod.getItemMeta() == null) {
            return null;
        }


        return getRandomBait(
                getExistingBaitsFromRod(fishingRod).stream().collect(Collectors.toMap(
                        bait -> bait,
                        bait -> bait.applicationWeight
                ))
        );
    }

    /**
     * Calculates a random bait to throw out based on their catch-weight. It uses the same weight algorithm as
     * randomBaitApplication, using the baits from the main class in the baits list.
     *
     * @return A random bait weighted by its catch-weight.
     */
    public static Bait randomBaitCatch() {
        return getRandomBait(
                EvenMoreFish.getInstance().getBaits().values().stream().collect(Collectors.toMap(
                        bait -> bait,
                        bait -> bait.catchWeight
                ))
        );
    }

    /**
     * Runs through the metadata of the rod to try and figure out whether a certain bait is applied or not.
     *
     * @param itemStack The fishing rod in item stack form.
     * @param bait      The name of the bait that could have been applied, must be the same as the time it was applied to the rod.
     * @return If the fishing rod contains the bait or not.
     */
    public static boolean hasBaitApplied(ItemStack itemStack, String bait) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        String[] baitList = NbtUtils.getBaitArray(itemStack);

        for (String appliedBait : baitList) {
            if (appliedBait.split(":")[0].equals(bait)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes all the baits stored in the nbt of the fishing rod. It then returns the total number of baits deleted just
     * incase you fancy doing something special with this number. It first checks whether there's any baits actually on
     * the rod in the first place. It loops through each bait stored to find out how many will be deleted then simply removes
     * the namespacedkey from the fishing rod.
     *
     * @param itemStack The fishing rod with baits on it/
     * @return The number of baits that were deleted in total.
     */
    public static int deleteAllBaits(ItemStack itemStack) {
        if (!NbtUtils.hasKey(itemStack, NbtKeys.EMF_APPLIED_BAIT)) {
            return 0;
        }

        int totalDeleted = 0;
        for (String appliedBait : NbtUtils.getStringListOrStringBait(itemStack)) {
            totalDeleted += Integer.parseInt(appliedBait.split(":")[1]);
        }
        NBT.modify(itemStack, nbt -> {
            nbt.getOrCreateCompound(NbtKeys.EMF_COMPOUND).removeKey(NbtKeys.EMF_APPLIED_BAIT);
        });

        itemStack.setItemMeta(itemStack.getItemMeta()); //we can modify meta via nbtapi
        return totalDeleted;
    }

    public static List<String> newApplyLore(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return Collections.emptyList();
        }

        ItemMeta meta = itemStack.getItemMeta();

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        List<String> format = BaitFile.getInstance().getRodLoreFormat();
        for (String lineAddition : format) {
            if (lineAddition.equals("{baits}")) {
                lore.addAll(getBaitsInfo(itemStack, lore));
            } else {
                Message message = new Message(lineAddition);
                message.setCurrentBaits(Integer.toString(getNumBaitsApplied(itemStack)));
                message.setMaxBaits(Integer.toString(BaitFile.getInstance().getMaxBaits()));
                lore.add(message.getRawMessage(true));
            }
        }

        return lore;
    }

    private static List<String> getBaitsInfo(final ItemStack itemStack, final List<String> lore) {
        final List<String> baitList = NbtUtils.getStringListOrStringBait(itemStack).stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .toList();
        //todo, requires testing but we don't need this now?
        //                if (rodNBT == null || rodNBT.isEmpty()) {
        //                    return lore;
        //                }

        int baitCount = baitList.size();

        for (String bait : baitList) {
            Message message = new Message(BaitFile.getInstance().getBaitFormat());
            message.setBait(getBaitFormatted(bait.split(":")[0]));
            message.setAmount(bait.split(":")[1]);
            lore.add(message.getRawMessage(true));
        }

        if (BaitFile.getInstance().showUnusedBaitSlots()) {
            for (int i = baitCount; i < BaitFile.getInstance().getMaxBaits(); i++) {
                lore.add(FishUtils.translateColorCodes(BaitFile.getInstance().unusedBaitSlotFormat()));
            }
        }

        return lore;
    }

    /**
     * This deletes all the old lore inserted by the plugin to the baited fishing rod. If the config value for the lore
     * format had lines added/removed this will break the old rods.
     *
     * @param itemStack The lore of the itemstack having the bait section of its lore removed.
     * @throws IndexOutOfBoundsException When the fishing rod doesn't have enough lines of lore to delete, this could be
     *                                   caused by a modification to the format in the baits.yml config.
     */
    public static List<String> deleteOldLore(ItemStack itemStack) throws IndexOutOfBoundsException {
        if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null || !itemStack.getItemMeta().hasLore()) {
            return Collections.emptyList();
        }

        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore == null) {
            return Collections.emptyList();
        }

        //todo bug here:
        /*
        This should be done while referencing the format.
        It will check each line if it matches the format, and if it does, only then it will erase.
         */
        if (BaitFile.getInstance().showUnusedBaitSlots()) {
            // starting at 1, because at least one bait replacing {baits} is repeated.
            int maxBaits = BaitFile.getInstance().getMaxBaits() + BaitFile.getInstance().getRodLoreFormat().size();
            //todo, to help this be compliant with java:S5413, we should iterate in reverse order, this should be done in another pr, left here for reference
            //compliant version
            if (lore.size() > maxBaits) {
                lore.subList(maxBaits, lore.size()).clear();
            }

            //            for (int i = 1; i < maxBaits; i++) {
            //                lore.remove(lore.size() - 1);
            //            }
        } else {
            // starting at 1, because at least one bait replacing {baits} is repeated.
            int numBaitsApplied = getNumBaitsApplied(itemStack) + BaitFile.getInstance().getRodLoreFormat().size();
            //compliant version
            if (lore.size() > numBaitsApplied) {
                lore.subList(numBaitsApplied, lore.size()).clear();
            }
            //            for (int i = 1; i < numBaitsApplied; i++) {
            //                lore.remove(lore.size() - 1);
            //            }
        }

        return lore;
    }

    /**
     * Works out how many baits are applied to an object based on the nbt data.
     *
     * @param itemStack The fishing rod with baits applied
     * @return How many baits have been applied to this fishing rod.
     */
    private static int getNumBaitsApplied(ItemStack itemStack) {
        String rodNBT = NbtUtils.getString(itemStack, NbtKeys.EMF_APPLIED_BAIT);
        if (rodNBT == null) {
            return 1;
        }

        return rodNBT.split(",").length;
    }

    /**
     * Checks the bait from baitID to see if it has a displayname and returns that if necessary - else it just returns
     * the baitID itself.
     *
     * @param baitID The baitID the bait is registered under in baits.yml
     * @return How the bait should look in the lore of the fishing rod, for example.
     */
    private static @NotNull String getBaitFormatted(String baitID) {
        Bait bait = EvenMoreFish.getInstance().getBaits().get(baitID);
        return FishUtils.translateColorCodes(bait.getDisplayName());
    }

    private static Bait getRandomBait(final @NotNull Map<Bait, Double> weights) {
        if (new HashSet<>(weights.values()).size() == 1) {
            //when everything is equal chance...
            CollectionSampler<Bait> sampler = new CollectionSampler<>(RandomSource.MWC_256.create(), weights.keySet());
            return sampler.sample();
        }

        DiscreteProbabilityCollectionSampler<Bait> sampler = new DiscreteProbabilityCollectionSampler<>(RandomSource.MWC_256.create(), weights);
        return sampler.sample();
    }
}