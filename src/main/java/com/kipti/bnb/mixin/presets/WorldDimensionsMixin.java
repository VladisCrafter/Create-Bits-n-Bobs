package com.kipti.bnb.mixin.presets;

import com.kipti.bnb.foundation.generation.PonderLevelSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Function;

@Mixin(WorldDimensions.class)
public class WorldDimensionsMixin {

    @Redirect(method = "specialWorldProperty", at = @At(value = "INVOKE", target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"))
    private static Optional<?> modifyPonderWorldFlatProperty(Optional<LevelStem> instance, Function<LevelStem, ?> mapper) {
        return instance.map(stem -> {
            ChunkGenerator generator = stem.generator();
            if (generator instanceof PonderLevelSource) {
                return PrimaryLevelData.SpecialWorldProperty.FLAT;
            }
            return mapper.apply(stem);
        });
    }

}
