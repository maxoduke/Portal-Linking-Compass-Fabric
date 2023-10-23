package dev.maxoduke.mods.portallinkingcompass.item;

import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings({ "DataFlowIssue", "SpellCheckingInspection" })
public class PortalLinkingCompassItem extends Item implements Vanishable
{
    private static final String TAG_ORIGINAL_DIMENSION = "OriginalDimension";
    private static final String TAG_TARGET_DIMENSION = "TargetDimension";
    private static final String TAG_ORIGINAL_POS = "OriginalPos";
    private static final String TAG_TARGET_POS = "TargetPos";

    public PortalLinkingCompassItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack item, Level level, @NotNull Entity ignore, int ignore2, boolean ignore3)
    {
        if (level.isClientSide || isNotLinked(item))
            return;

        removeTagsIfNotValid(item, level);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext useOnContext)
    {
        BlockPos usedOnBlockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        ItemStack heldItem = useOnContext.getItemInHand();
        Player player = useOnContext.getPlayer();

        if (!level.getBlockState(usedOnBlockPos).is(Blocks.NETHER_PORTAL))
            return super.useOn(useOnContext);

        double overworldCoordinateScale = 1.0;
        double netherCoordinateScale = 8.0;
        double coordinateScale = (level.dimension() == Level.NETHER)
            ? netherCoordinateScale / overworldCoordinateScale
            : overworldCoordinateScale / netherCoordinateScale;

        var originalDimension = level.dimension();
        var targetDimension = originalDimension == Level.OVERWORLD ? Level.NETHER : Level.OVERWORLD;

        WorldBorder worldBorder = level.getWorldBorder();

        BlockPos targetPos = worldBorder.clampToBounds(
            usedOnBlockPos.getX() * coordinateScale,
            0 /* usedOnBlockPos.getY() */,
            usedOnBlockPos.getZ() * coordinateScale
        );

        if (heldItem.getCount() == 1)
            addTags(originalDimension, targetDimension, usedOnBlockPos, targetPos, heldItem.getOrCreateTag());
        else
        {
            ItemStack newCompass = new ItemStack(PortalLinkingCompass.ITEM, 1);
            CompoundTag newCompassTag = newCompass.getOrCreateTag();

            if (!player.isCreative())
                heldItem.shrink(1);

            addTags(originalDimension, targetDimension, usedOnBlockPos, targetPos, newCompassTag);

            if (!player.getInventory().add(newCompass))
                player.drop(newCompass, false);
        }

        level.playSound(null, usedOnBlockPos, PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT, SoundSource.PLAYERS, 1.0f, 1.0f);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static GlobalPos pointToTarget(ClientLevel clientLevel, ItemStack itemStack, Entity ignore)
    {
        if (isNotLinked(itemStack))
            return null;

        var tag = itemStack.getOrCreateTag();
        var currentDimension = clientLevel.dimension();

        var originalDimension = getOriginalPortalDimension(tag);
        var targetDimension = getTargetPortalDimension(tag);

        if (originalDimension.isEmpty() || targetDimension.isEmpty())
            return null;

        if (currentDimension == originalDimension.get())
            return getOriginalPortalPosition(tag);
        else if (currentDimension == targetDimension.get())
            return getTargetPortalPosition(tag);

        return null;
    }

    public static boolean isNotLinked(ItemStack item)
    {
        CompoundTag tag = item.getTag();

        return tag == null ||
            !tag.contains(TAG_ORIGINAL_DIMENSION) ||
            !tag.contains(TAG_TARGET_DIMENSION) ||
            !tag.contains(TAG_ORIGINAL_POS) ||
            !tag.contains(TAG_TARGET_POS);
    }

    public static Optional<ResourceKey<Level>> getOriginalPortalDimension(CompoundTag tag)
    {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_ORIGINAL_DIMENSION)).result();
    }

    public static Optional<ResourceKey<Level>> getTargetPortalDimension(CompoundTag tag)
    {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_TARGET_DIMENSION)).result();
    }

    public static GlobalPos getOriginalPortalPosition(CompoundTag tag)
    {
        Optional<ResourceKey<Level>> level = getOriginalPortalDimension(tag);
        if (level.isEmpty() || !tag.contains(TAG_ORIGINAL_DIMENSION) || !tag.contains(TAG_ORIGINAL_POS))
            return null;

        BlockPos blockPos = NbtUtils.readBlockPos(tag.getCompound(TAG_ORIGINAL_POS));
        return GlobalPos.of(level.get(), blockPos);
    }

    public static GlobalPos getTargetPortalPosition(CompoundTag tag)
    {
        Optional<ResourceKey<Level>> level = getTargetPortalDimension(tag);
        if (level.isEmpty() || !tag.contains(TAG_TARGET_DIMENSION) || !tag.contains(TAG_TARGET_POS))
            return null;

        BlockPos blockPos = NbtUtils.readBlockPos(tag.getCompound(TAG_TARGET_POS));
        return GlobalPos.of(level.get(), blockPos);
    }

    public static void addTags(ResourceKey<Level> originalDim, ResourceKey<Level> targetDim, BlockPos originalPos, BlockPos targetPos, CompoundTag compoundTag)
    {
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, originalDim)
            .resultOrPartial(PortalLinkingCompass.LOG::error)
            .ifPresent(tag -> compoundTag.put(TAG_ORIGINAL_DIMENSION, tag));

        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, targetDim)
            .resultOrPartial(PortalLinkingCompass.LOG::error)
            .ifPresent(tag -> compoundTag.put(TAG_TARGET_DIMENSION, tag));

        compoundTag.put(TAG_ORIGINAL_POS, NbtUtils.writeBlockPos(originalPos));
        compoundTag.put(TAG_TARGET_POS, NbtUtils.writeBlockPos(targetPos));
    }

    public static void removeTagsIfNotValid(ItemStack item, Level level)
    {
        CompoundTag tag = item.getOrCreateTag();

        Optional<ResourceKey<Level>> originalDimOptional = getOriginalPortalDimension(tag);
        Optional<ResourceKey<Level>> targetDimOptional = getTargetPortalDimension(tag);

        if (originalDimOptional.isEmpty() || targetDimOptional.isEmpty())
        {
            item.setTag(null);
            return;
        }

        MinecraftServer server = level.getServer();

        ServerLevel originalDimension = server.getLevel(originalDimOptional.get());
        ServerLevel targetDimension = server.getLevel(targetDimOptional.get());

        PoiManager originalDimPoiManager = originalDimension.getPoiManager();
        // PoiManager targetDimPoiManager = targetDim.getPoiManager();

        GlobalPos originalPos = getOriginalPortalPosition(tag);
        GlobalPos targetPos = getTargetPortalPosition(tag);

        boolean isOriginalPosWithinWorldBounds = originalDimension.isInWorldBounds(originalPos.pos());
        boolean isTargetPosWithinWorldBounds = targetDimension.isInWorldBounds(targetPos.pos());

        boolean originalPortalBlockExists = originalDimPoiManager.existsAtPosition(PoiTypes.NETHER_PORTAL, originalPos.pos());
        // boolean targetPortalBlockExists = targetDimPoiManager.existsAtPosition(PoiTypes.NETHER_PORTAL, targetPos.pos());

        if (!isOriginalPosWithinWorldBounds || !originalPortalBlockExists || !isTargetPosWithinWorldBounds)
            item.setTag(null);
    }
}
