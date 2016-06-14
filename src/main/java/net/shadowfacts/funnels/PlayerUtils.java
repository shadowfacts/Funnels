package net.shadowfacts.funnels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author shadowfacts
 */
public class PlayerUtils {

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace) {

		if (entityplayer == null || entityplayer.capabilities.isCreativeMode) {
			return true;
		}
		if (allowReplace && stack.stackSize <= 1) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			entityplayer.inventory.addItemStackToInventory(dropStack);
			return true;
		} else if (allowDrop) {
			stack.stackSize -= 1;
			if (dropStack != null && !entityplayer.inventory.addItemStackToInventory(dropStack)) {
				entityplayer.dropItem(dropStack, false, true);
			}
			return true;
		}
		return false;
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop) {
		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}

}
