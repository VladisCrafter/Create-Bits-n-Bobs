package com.kipti.bnb.foundation.config.conditions;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registry.BnbFeatureFlag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.NotNull;

public record BnbFeatureItemEnabledCondition(ResourceKey<Item> itemKey) implements ICondition {

    public static final ResourceLocation ID = CreateBitsnBobs.asResource("feature_item_enabled");
//    public static final MapCodec<BnbFeatureItemEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
//            c ->
//                    c.group(
//                            ResourceKey.codec(BuiltInRegistries.ITEM.key()).fieldOf("item").forGetter(BnbFeatureItemEnabledCondition::itemKey)
//                    ).apply(c, BnbFeatureItemEnabledCondition::new)
//    );

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(@NotNull final IContext context) {
        return BnbFeatureFlag.isEnabled(BuiltInRegistries.ITEM.get(itemKey));
    }

    public static class Serializer implements IConditionSerializer<BnbFeatureItemEnabledCondition> {

        @Override
        public void write(@NotNull final com.google.gson.JsonObject jsonObject,
                          @NotNull final BnbFeatureItemEnabledCondition condition) {
            jsonObject.addProperty("item", condition.itemKey.location().toString());
        }

        @Override
        public BnbFeatureItemEnabledCondition read(@NotNull final com.google.gson.JsonObject jsonObject) {
            String itemString = jsonObject.get("item").getAsString();
            ResourceKey<Item> itemKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse(itemString));
            return new BnbFeatureItemEnabledCondition(itemKey);
        }

        @Override
        public @NotNull ResourceLocation getID() {
            return ID;
        }
    }


}
