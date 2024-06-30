package dev.maxoduke.mods.portallinkingcompass.item;

import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class PortalLinkingCompassItem extends Item
{
    public PortalLinkingCompassItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack item, Level level, @NotNull Entity ignore, int ignore2, boolean ignore3)
    {
        if (level.isClientSide)
            return;

        LinkedPortalTracker tracker = item.get(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);
        if (tracker == null)
            return;

        LinkedPortalTracker newTracker = tracker.tick(level);
        if (tracker != newTracker)
            item.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, newTracker);
    }

    @SuppressWarnings("DataFlowIssue")
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

        LinkedPortalTracker tracker = new LinkedPortalTracker(usedOnBlockPos, level);
        if (!player.isCreative() && heldItem.getCount() == 1)
            heldItem.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, tracker);
        else
        {
            ItemStack newCompass = new ItemStack(PortalLinkingCompass.ITEM, 1);
            newCompass.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, tracker);

            if (!player.isCreative())
                heldItem.shrink(1);

            if (!player.getInventory().add(newCompass))
                player.drop(newCompass, false);
        }

        level.playSound(null, usedOnBlockPos, PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT, SoundSource.PLAYERS, 1.0f, 1.0f);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static GlobalPos pointToTarget(ClientLevel clientLevel, ItemStack itemStack, Entity ignore)
    {
        LinkedPortalTracker tracker = itemStack.get(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);

        if (tracker == null ||
            tracker.isNotLinked() ||
            tracker.originalPosition().isEmpty() ||
            tracker.originalDimension().isEmpty() ||
            tracker.targetPosition().isEmpty() ||
            tracker.targetDimension().isEmpty()
        )
            return null;

        ResourceKey<Level> currentDimension = clientLevel.dimension();
        ResourceKey<Level> originalDimension = tracker.originalDimension().get();
        ResourceKey<Level> targetDimension = tracker.targetDimension().get();
        BlockPos originalPosition = tracker.originalPosition().get();
        BlockPos targetPosition = tracker.targetPosition().get();

        if (currentDimension == originalDimension)
            return GlobalPos.of(originalDimension, originalPosition);
        else if (currentDimension == targetDimension)
            return GlobalPos.of(targetDimension, targetPosition);

        return null;
    }
}
