package tfar.thehandofgod.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.HandOfGodConfig;
import tfar.thehandofgod.init.ModItems;
import tfar.thehandofgod.inventory.EnchantmentItemStackHandler;
import tfar.thehandofgod.inventory.PotionItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

    public static List<Entity> getEntitiesInCone(EntityLivingBase player) {
        List<Entity> entities = player.world.getEntities(Entity.class,entity -> entity != player);
        List<Entity> coneEntities = new ArrayList<>();
        Vec3d coneTip = player.getPositionVector();
        Vec3d direction = player.getLookVec();
        Vec3d basementCenter = coneTip.add(direction .scale( HandOfGodConfig.cone_length));
        Vec3d axisVect = coneTip.subtract(basementCenter);
        for (Entity entity : entities) {
            Vec3d entityPos = entity.getPositionVector();
            Vec3d apexToXVec = coneTip.subtract(entityPos);

            boolean isInInfiniteCone = apexToXVec.dotProduct(axisVect)
                    /(apexToXVec.length()*axisVect.length())
                    >
                    // We can safely compare cos() of angles
                    // between vectors instead of bare angles.
                    Math.cos(Math.toRadians(HandOfGodConfig.cone_angle / 2));
            if (isInInfiniteCone) {

                // X is contained in cone only if projection of apexToXVect to axis
                // is shorter than axis.
                // We'll use dotProd() to figure projection length.
                boolean isUnderRoundCap = apexToXVec.dotProduct(axisVect)
                        /axisVect.length()
                        <
                        axisVect.length();
                if (isUnderRoundCap) {
                    coneEntities.add(entity);
                }

                coneEntities.add(entity);
            }
        }
        return coneEntities;
    }

    private static final ItemStack HAND = new ItemStack(ModItems.HAND_OF_GOD);

    public static boolean hasHand(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return player.inventory.hasItemStack(HAND);
        }
        return false;
    }

    public static ItemStack getHand(EntityPlayer player) {
            return getSlotFor(player.inventory,HAND);
    }


    /**
     * Finds the stack or an equivalent one in the main inventory
     */
    //the original function is clientside only hooray
    public static ItemStack getSlotFor(InventoryPlayer inv,ItemStack stack)
    {
        for (int i = 0; i < inv.mainInventory.size(); ++i)
        {
            if (!inv.mainInventory.get(i).isEmpty() && ItemStack.areItemsEqual(HAND,inv.mainInventory.get(i)))//*inv.stackEqualExact(stack, inv.mainInventory.get(i)))
            {
                return inv.mainInventory.get(i);
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Tries to break a block as if this player had broken it.  This is a complex operation.
     * @param stack The player's current held stack, main hand.
     * @param world The player's world.
     * @param player The player that is breaking this block.
     * @param pos The pos to break.
     * @return If the break was successful.
     */
    public static boolean breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (player.capabilities.isCreativeMode) {
            if (block.removedByPlayer(state, world, pos, player, false)) {
                block.onPlayerDestroy(world, pos, state);
            }

            // send update to client
            if (!world.isRemote) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            return true;
        }

        // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
        stack.onBlockDestroyed(world, state, pos, player);

        // server sided handling
        if (!world.isRemote) {
            // send the blockbreak event
            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
            if (xp == -1) return false;

            TileEntity tileEntity = world.getTileEntity(pos);
            if (block.removedByPlayer(state, world, pos, player, true)) { // boolean is if block can be harvested, checked above
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, tileEntity, stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }

            // always send block update to client
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
            return true;
        }
        // client sided handling
        else {
            // clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
            // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

            // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
            world.playEvent(2001, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onPlayerDestroy(world, pos, state);
            }
            // callback to the tool
            stack.onBlockDestroyed(world, state, pos, player);

            // send an update to the server, so we get an update back
            //ActuallyAdditions.PROXY.sendBreakPacket(pos);
            return true;
        }
    }


    public static ItemStackHandler getEnchantmentHandler(ItemStack stack) {
        ItemStackHandler handler = new EnchantmentItemStackHandler(9);
        Map<Enchantment, Integer> data = EnchantmentHelper.getEnchantments(stack);
        int i = 0;
        for (Map.Entry<Enchantment,Integer> entry : data.entrySet()) {
            ItemStack stack1 = createBookFromEnchantment(entry.getKey(),entry.getValue());
            handler.setStackInSlot(i,stack1);
            i++;
        }
        return handler;
    }

    public static ItemStackHandler getPotionHandler(ItemStack stack) {
        ItemStackHandler handler = new PotionItemStackHandler(9);
        handler.deserializeNBT(stack.getSubCompound("potions"));
        return handler;
    }

    public static ItemStack createBookFromEnchantment(Enchantment enchantment ,int level) {
        return ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment,level));
    }

    public static boolean isFriendlyMob(Entity entity) {
        return entity instanceof EntityLivingBase && !(entity instanceof IMob);
    }
}
