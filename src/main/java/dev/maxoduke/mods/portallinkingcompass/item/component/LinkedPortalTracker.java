package dev.maxoduke.mods.portallinkingcompass.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.Level;

import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
public record LinkedPortalTracker(
    Optional<BlockPos> originalPosition,
    Optional<ResourceKey<Level>> originalDimension,
    Optional<BlockPos> targetPosition,
    Optional<ResourceKey<Level>> targetDimension
)
{
    public static final Codec<LinkedPortalTracker> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
            BlockPos.CODEC.optionalFieldOf("originalPosition").forGetter(LinkedPortalTracker::originalPosition),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("originalDimension").forGetter(LinkedPortalTracker::originalDimension),
            BlockPos.CODEC.optionalFieldOf("targetPosition").forGetter(LinkedPortalTracker::targetPosition),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("targetDimension").forGetter(LinkedPortalTracker::targetDimension)
        )
        .apply(instance, LinkedPortalTracker::new)
    );

    public static final StreamCodec<ByteBuf, LinkedPortalTracker> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
        LinkedPortalTracker::originalPosition,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Level.RESOURCE_KEY_CODEC)),
        LinkedPortalTracker::originalDimension,
        ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
        LinkedPortalTracker::targetPosition,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Level.RESOURCE_KEY_CODEC)),
        LinkedPortalTracker::targetDimension,
        LinkedPortalTracker::new
    );

    public LinkedPortalTracker()
    {
        this(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    public LinkedPortalTracker(BlockPos originalPosition, Level level)
    {
        this(
            Optional.of(originalPosition),
            Optional.of(level.dimension()),
            Optional.of(determineTargetPosition(originalPosition, level)),
            Optional.of((level.dimension() == Level.NETHER) ? Level.OVERWORLD : Level.NETHER)
        );
    }

    @SuppressWarnings("DataFlowIssue")
    public LinkedPortalTracker tick(Level level)
    {
        if (originalDimension.isEmpty() || originalPosition.isEmpty())
            return this;

        MinecraftServer server = level.getServer();
        ServerLevel originalDimension = server.getLevel(this.originalDimension.get());
        if (originalDimension == null)
            return this;

        PoiManager poiManager = originalDimension.getPoiManager();
        boolean originalPortalBlockExists = poiManager.existsAtPosition(PoiTypes.NETHER_PORTAL, originalPosition.get());
        if (originalPortalBlockExists)
            return this;

        return new LinkedPortalTracker();
    }

    public boolean isNotLinked()
    {
        return originalPosition.isEmpty() ||
            originalDimension.isEmpty() ||
            targetPosition.isEmpty() ||
            targetDimension.isEmpty();
    }

    private static BlockPos determineTargetPosition(BlockPos originalPosition, Level level)
    {
        double overworldCoordinateScale = 1.0;
        double netherCoordinateScale = 8.0;
        double coordinateScale = (level.dimension() == ServerLevel.NETHER)
            ? netherCoordinateScale / overworldCoordinateScale
            : overworldCoordinateScale / netherCoordinateScale;

        double x = originalPosition.getX() * coordinateScale;
        double y = Math.max(originalPosition.getY(), 0.0);
        double z = originalPosition.getZ() * coordinateScale;

        return level.getWorldBorder().clampToBounds(x, y, z);
    }
}
