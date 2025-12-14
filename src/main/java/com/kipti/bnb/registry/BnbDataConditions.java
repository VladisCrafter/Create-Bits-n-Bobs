package com.kipti.bnb.registry;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.foundation.config.conditions.BnbFeatureEnabledCondition;
import com.kipti.bnb.foundation.config.conditions.BnbFeatureItemEnabledCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

public class BnbDataConditions {

//    public static final Supplier<MapCodec<BnbFeatureEnabledCondition>> FEATURE_ENABLED_CONDITION =
//            CONDITION_CODECS.register("feature_enabled", () -> BnbFeatureEnabledCondition.CODEC);
//    public static final Supplier<MapCodec<BnbFeatureItemEnabledCondition>> ITEM_ENABLED_BY_FEATURE =
//            CONDITION_CODECS.register("feature_item_enabled", () -> BnbFeatureItemEnabledCondition.CODEC);

    public static final BnbFeatureEnabledCondition.Serializer FEATURE_ENABLED_CONDITION = new BnbFeatureEnabledCondition.Serializer();

    public static final BnbFeatureItemEnabledCondition.Serializer ITEM_ENABLED_BY_FEATURE = new BnbFeatureItemEnabledCondition.Serializer();

    public static void registerSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
            helper -> CraftingHelper.register(FEATURE_ENABLED_CONDITION)
        );
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
            helper -> CraftingHelper.register(ITEM_ENABLED_BY_FEATURE)
        );
    }

}
