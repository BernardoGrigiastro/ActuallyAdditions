package de.ellpeck.actuallyadditions.common.util;

import de.ellpeck.actuallyadditions.common.ActuallyAdditions;
import de.ellpeck.actuallyadditions.common.tile.FilterSettings;
import de.ellpeck.actuallyadditions.common.util.compat.SlotlessableItemHandlerWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;

public final class WorldUtil {

    public static boolean doItemInteraction(SlotlessableItemHandlerWrapper extractWrapper, SlotlessableItemHandlerWrapper insertWrapper, int maxExtract) {
        return doItemInteraction(extractWrapper, insertWrapper, maxExtract, null);
    }

    public static boolean doItemInteraction(SlotlessableItemHandlerWrapper extractWrapper, SlotlessableItemHandlerWrapper insertWrapper, int maxExtract, FilterSettings filter) {
        return doItemInteraction(extractWrapper, insertWrapper, maxExtract, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, filter);
    }

    public static boolean doItemInteraction(SlotlessableItemHandlerWrapper extractWrapper, SlotlessableItemHandlerWrapper insertWrapper, int maxExtract, int extractSlotStart, int extractSlotEnd, int insertSlotStart, int insertSlotEnd, FilterSettings filter) {
        ItemStack theoreticalExtract = extractItem(extractWrapper, maxExtract, true, extractSlotStart, extractSlotEnd, filter);
        if (StackUtil.isValid(theoreticalExtract)) {
            ItemStack remaining = StackUtil.insertItem(insertWrapper, theoreticalExtract, false, insertSlotStart, insertSlotEnd);
            if (!ItemStack.areItemStacksEqual(remaining, theoreticalExtract)) {
                int toExtract = theoreticalExtract.getCount() - remaining.getCount();
                extractItem(extractWrapper, toExtract, false, extractSlotStart, extractSlotEnd, filter);
                return true;
            }
        }
        return false;
    }

    public static ItemStack extractItem(SlotlessableItemHandlerWrapper extractWrapper, int maxExtract, boolean simulate, int slotStart, int slotEnd, FilterSettings filter) {
        ItemStack extracted = StackUtil.getEmpty();

        if (ActuallyAdditions.commonCapsLoaded) {
            Object handler = extractWrapper.getSlotlessHandler();
//            if (handler instanceof ISlotlessItemHandler) {
//                ISlotlessItemHandler slotless = (ISlotlessItemHandler) handler;
//
//                if (filter == null || !filter.needsCheck()) {
//                    extracted = slotless.extractItem(maxExtract, simulate);
//                    return extracted;
//                } else {
//                    ItemStack would = slotless.extractItem(maxExtract, true);
//                    if (filter.check(would)) {
//                        if (simulate) {
//                            extracted = would;
//                        } else {
//                            extracted = slotless.extractItem(maxExtract, false);
//                        }
//                    }
//                    //Leave the possibility to fall back to vanilla when there is a filter
//                }
//            }
        }

        if (!StackUtil.isValid(extracted)) {
            IItemHandler handler = extractWrapper.getNormalHandler();
            if (handler != null) {
                for (int i = Math.max(0, slotStart); i < Math.min(slotEnd, handler.getSlots()); i++) {
                    if (filter == null || !filter.needsCheck() || filter.check(handler.getStackInSlot(i))) {
                        extracted = handler.extractItem(i, maxExtract, simulate);

                        if (StackUtil.isValid(extracted)) {
                            break;
                        }
                    }
                }
            }
        }

        return extracted;
    }

    public static void doEnergyInteraction(TileEntity tileFrom, TileEntity tileTo, Direction sideTo, int maxTransfer) {
        if (maxTransfer > 0) {
            Direction opp = sideTo == null ? null : sideTo.getOpposite();
            tileFrom.getCapability(CapabilityEnergy.ENERGY, sideTo).ifPresent(handlerFrom -> tileTo.getCapability(CapabilityEnergy.ENERGY, opp).ifPresent(handlerTo -> {
                int drain = handlerFrom.extractEnergy(maxTransfer, true);
                if (drain > 0) {
                    int filled = handlerTo.receiveEnergy(drain, false);
                    handlerFrom.extractEnergy(filled, false);
                }
            }));
        }
    }

    public static void doFluidInteraction(TileEntity tileFrom, TileEntity tileTo, Direction sideTo, int maxTransfer) {
        if (maxTransfer > 0) {
            tileFrom.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideTo).ifPresent(handlerFrom -> tileTo.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideTo.getOpposite()).ifPresent(handlerTo -> {
                FluidStack drain = handlerFrom.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
                int filled = handlerTo.fill(drain.copy(), IFluidHandler.FluidAction.EXECUTE);
                handlerFrom.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            }));
        }
    }

    /**
     * Checks if a given Block with a given Meta is present in given Positions
     *
     * @param positions The Positions, an array of {x, y, z} arrays containing Positions
     * @param block     The Block
     * @param world     The World
     * @return Is every block present?
     */
    public static boolean hasBlocksInPlacesGiven(BlockPos[] positions, Block block, World world) {
        for (BlockPos pos : positions) {
            BlockState state = world.getBlockState(pos);
            if (!(state.getBlock() == block)) { return false; }
        }
        return true;
    }

    public static ItemStack useItemAtSide(Direction side, World world, BlockPos pos, ItemStack stack) {
        if (world instanceof ServerWorld && StackUtil.isValid(stack) && pos != null) {
            BlockPos offsetPos = pos.offset(side);
            BlockState state = world.getBlockState(offsetPos);
            boolean replaceable = state.getMaterial().isReplaceable();

            //Redstone
            if (replaceable && stack.getItem() == Items.REDSTONE) {
                world.setBlockState(offsetPos, Blocks.REDSTONE_WIRE.getDefaultState(), 2);
                return StackUtil.shrink(stack, 1);
            }

            //Plants
            if (replaceable && stack.getItem() instanceof IPlantable) {
                if (((IPlantable) stack.getItem()).getPlant(world, offsetPos).isValidPosition(world, offsetPos)) {
                    if (world.setBlockState(offsetPos, ((IPlantable) stack.getItem()).getPlant(world, offsetPos), 2)) return StackUtil.shrink(stack, 1);
                }
            }

            //Everything else
            try {
                FakePlayer fake = FakePlayerFactory.getMinecraft((ServerWorld) world);
                if (fake.connection == null) fake.connection = new NetHandlerSpaghettiServer(fake);
                ItemStack heldBefore = fake.getHeldItemMainhand();
                setHandItemWithoutAnnoyingSound(fake, Hand.MAIN_HAND, stack.copy());
                fake.interactionManager.processRightClick(fake, world, fake.getHeldItemMainhand(), Hand.MAIN_HAND);
                ItemStack result = fake.getHeldItem(Hand.MAIN_HAND);
                setHandItemWithoutAnnoyingSound(fake, Hand.MAIN_HAND, heldBefore);
                return result;
            } catch (Exception e) {
                ActuallyAdditions.LOGGER.error("Something that places Blocks at " + offsetPos.getX() + ", " + offsetPos.getY() + ", " + offsetPos.getZ() + " in World " + world.getDimension() + " threw an Exception! Don't let that happen again!", e);
            }
        }
        return stack;
    }

    public static boolean dropItemAtSide(Direction side, World world, BlockPos pos, ItemStack stack) {
        BlockPos coords = pos.offset(side);
        if (world.isBlockLoaded(coords)) {
            ItemEntity item = new ItemEntity(world, coords.getX() + 0.5, coords.getY() + 0.5, coords.getZ() + 0.5, stack);
            item.setMotion(0, 0, 0);

            return world.addEntity(item);
        }
        return false;
    }

    public static Direction getDirectionBySidesInOrder(int side) {
        switch (side) {
        case 0:
            return Direction.UP;
        case 1:
            return Direction.DOWN;
        case 2:
            return Direction.NORTH;
        case 3:
            return Direction.EAST;
        case 4:
            return Direction.SOUTH;
        default:
            return Direction.WEST;
        }
    }

    public static Direction getDirectionByPistonRotation(BlockState state) {
        return state.get(BlockStateProperties.FACING);
    }

    public static ArrayList<Material> getMaterialsAround(World world, BlockPos pos) {
        ArrayList<Material> blocks = new ArrayList<>();
        blocks.add(world.getBlockState(pos.offset(Direction.NORTH)).getMaterial());
        blocks.add(world.getBlockState(pos.offset(Direction.EAST)).getMaterial());
        blocks.add(world.getBlockState(pos.offset(Direction.SOUTH)).getMaterial());
        blocks.add(world.getBlockState(pos.offset(Direction.WEST)).getMaterial());
        return blocks;
    }

    public static RayTraceResult getNearestPositionWithAir(World world, PlayerEntity player, int reach) {
        return getMovingObjectPosWithReachDistance(world, player, reach, false, false, true);
    }

    // todo: migrate to standard rayTrace
    @Deprecated
    private static RayTraceResult getMovingObjectPosWithReachDistance(World world, PlayerEntity player, double distance, boolean p1, boolean p2, boolean p3) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        double d0 = player.getPosX();
        double d1 = player.getPosY() + player.getEyeHeight();
        double d2 = player.getPosZ();
        Vec3d vec3 = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3d vec31 = vec3.add(f6 * distance, f5 * distance, f7 * distance);
        return null; // world.rayTraceBlocks(vec3, vec31, p1, p2, p3);
    }

    public static RayTraceResult getNearestBlockWithDefaultReachDistance(World world, PlayerEntity player) {
        return getNearestBlockWithDefaultReachDistance(world, player, false, true, false);
    }

    public static RayTraceResult getNearestBlockWithDefaultReachDistance(World world, PlayerEntity player, boolean stopOnLiquids, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        return getMovingObjectPosWithReachDistance(world, player, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue(), stopOnLiquids, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
    }

    public static void setHandItemWithoutAnnoyingSound(PlayerEntity player, Hand hand, ItemStack stack) {
        if (hand == Hand.MAIN_HAND) {
            player.inventory.mainInventory.set(player.inventory.currentItem, stack);
        } else if (hand == Hand.OFF_HAND) {
            player.inventory.offHandInventory.set(0, stack);
        }
    }

    //I think something is up with this, but I'm not entirely certain what.
    public static float fireFakeHarvestEventsForDropChance(TileEntity caller, NonNullList<ItemStack> drops, World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            FakePlayer fake = FakePlayerFactory.getMinecraft((ServerWorld) world);
            BlockPos tePos = caller.getPos();
            fake.setPosition(tePos.getX() + 0.5, tePos.getY() + 0.5, tePos.getZ() + 0.5);
            BlockState state = world.getBlockState(pos);

            BreakEvent event = new BreakEvent(world, pos, state, fake);
            if (!MinecraftForge.EVENT_BUS.post(event)) { return ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1, false, fake); }
        }
        return 0F;
    }

    /**
     * Tries to break a block as if this player had broken it.  This is a complex operation.
     * @param stack The player's current held stack, main hand.
     * @param world The player's world.
     * @param player The player that is breaking this block.
     * @param pos The pos to break.
     * @return If the break was successful.
     */
    public static boolean breakExtraBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (player.isCreative()) {
            if (block.removedByPlayer(state, world, pos, player, false, world.getFluidState(pos))) {
                block.onPlayerDestroy(world, pos, state);
            }

            // send update to client
            if (!world.isRemote) {
                ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
            }
            return true;
        }

        // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
        stack.onBlockDestroyed(world, state, pos, player);

        // server sided handling
        if (!world.isRemote) {
            // send the blockbreak event
            int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
            if (xp == -1) return false;

            TileEntity tileEntity = world.getTileEntity(pos);
            if (block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) { // boolean is if block can be harvested, checked above
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, tileEntity, stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }

            // always send block update to client
            ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
            return true;
        }
        // client sided handling
        else {
            // clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
            // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

            // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
            world.playEvent(2001, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) {
                block.onPlayerDestroy(world, pos, state);
            }
            // callback to the tool
            stack.onBlockDestroyed(world, state, pos, player);

            // send an update to the server, so we get an update back
            ActuallyAdditions.PROXY.sendBreakPacket(pos);
            return true;
        }
    }
}
