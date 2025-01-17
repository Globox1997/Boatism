package net.shirojr.boatism.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.world.World;
import net.shirojr.boatism.entity.BoatismEntities;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.BoatismItems;

import java.util.List;
import java.util.stream.StreamSupport;

public class BoatEngineNbtHelper {

    public static void writeItemStacksToNbt(List<ItemStack> stacks, String name, NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : stacks) {
            NbtCompound nbtCompound = new NbtCompound();
            if (!itemStack.isEmpty()) {
                itemStack.writeNbt(nbtCompound);
            }
            nbtList.add(nbtCompound);
        }
        nbt.put(name, nbtList);
    }

    public static DefaultedList<ItemStack> readItemStacksFromNbt(NbtCompound nbt, String name, int size) {
        NbtList nbtList;
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size);
        if (nbt.contains(name, NbtElement.LIST_TYPE)) {
            nbtList = nbt.getList(name, NbtElement.COMPOUND_TYPE);
            stacks = DefaultedList.ofSize(nbtList.size(), ItemStack.EMPTY);
            for (int i = 0; i < nbtList.size(); i++) {
                stacks.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
            }
        }
        return stacks;
    }


    public static ItemStack getItemStackFromBoatEngineEntity(BoatEngineEntity engineEntity) {
        ItemStack stack = new ItemStack(BoatismItems.BASE_ENGINE);
        NbtCompound nbt = stack.getOrCreateNbt();
        List<ItemStack> armorList = StreamSupport
                .stream(engineEntity.getArmorItems().spliterator(), false).toList();
        List<ItemStack> heldList = StreamSupport
                .stream(engineEntity.getHeldItems().spliterator(), false).toList();

        engineEntity.getHookedBoatEntityUuid().ifPresent(hookedBoatEntityUuid ->
                nbt.putUuid(NbtKeys.HOOKED_ENTITY, hookedBoatEntityUuid));
        BoatEngineNbtHelper.writeItemStacksToNbt(armorList, NbtKeys.ARMOR_ITEMS, nbt);
        BoatEngineNbtHelper.writeItemStacksToNbt(heldList, NbtKeys.HELD_ITEMS, nbt);
        nbt.putInt(NbtKeys.POWER_OUTPUT, engineEntity.getPowerLevel());
        nbt.putInt(NbtKeys.OVERHEAT, engineEntity.getOverheat());
        nbt.put(NbtKeys.ROTATION, engineEntity.getArmRotation().toNbt());
        nbt.putBoolean(NbtKeys.IS_SUBMERGED, engineEntity.isSubmerged());
        nbt.putFloat(NbtKeys.FUEL, engineEntity.getFuel());
        nbt.putBoolean(NbtKeys.IS_LOCKED, engineEntity.isLocked());

        return stack;
    }

    @SuppressWarnings("CommentedOutCode")
    public BoatEngineEntity getBoatEngineEntityFromItemStack(ItemStack stack, World world) {
        BoatEngineEntity boatEngine = new BoatEngineEntity(BoatismEntities.BOAT_ENGINE, world);
        NbtCompound stackNbt = stack.getOrCreateNbt();

        // hook will be done with entity later on
        /*if (stackNbt.contains(NbtKeys.HOOKED_ENTITY)) {
            boatEngine.setHookedBoatEntity(stackNbt.getUuid("HookedEntity"));
        }*/
        if (stackNbt.contains(NbtKeys.ARMOR_ITEMS)) {
            boatEngine.setArmorItems(BoatEngineNbtHelper.readItemStacksFromNbt(stackNbt, NbtKeys.ARMOR_ITEMS, 4));
        }
        if (stackNbt.contains(NbtKeys.HELD_ITEMS)) {
            boatEngine.setHeldItems(BoatEngineNbtHelper.readItemStacksFromNbt(stackNbt, NbtKeys.HELD_ITEMS, 2));
        }
        boatEngine.setPowerLevel(Math.min(stackNbt.getInt(NbtKeys.POWER_OUTPUT), BoatEngineHandler.MAX_POWER_LEVEL / 2));
        boatEngine.setOverheat(stackNbt.getInt(NbtKeys.OVERHEAT));
        boatEngine.setArmRotation(new EulerAngle(stackNbt.getList(NbtKeys.ROTATION, NbtElement.FLOAT_TYPE)));
        boatEngine.setSubmerged(stackNbt.getBoolean(NbtKeys.IS_SUBMERGED));
        boatEngine.setFuel(stackNbt.getFloat(NbtKeys.FUEL));
        boatEngine.setLocked(stackNbt.getBoolean(NbtKeys.IS_LOCKED));
        return boatEngine;
    }
}
