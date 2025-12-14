package com.kipti.bnb.foundation.config.conditions;

import com.google.gson.JsonObject;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registry.BnbFeatureFlag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record BnbFeatureEnabledCondition(String featureFlagKey) implements ICondition {

    public static final ResourceLocation ID = CreateBitsnBobs.asResource("feature_enabled");
//    public static final MapCodec<BnbFeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
//            c ->
//                    c.group(
//                            Codec.STRING.fieldOf("feature").forGetter(BnbFeatureEnabledCondition::featureFlagKey)
//                    ).apply(c, BnbFeatureEnabledCondition::new)
//    );

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return BnbFeatureFlag.isEnabled(this.featureFlagKey);
    }

    public static class Serializer implements IConditionSerializer<BnbFeatureEnabledCondition> {

        @Override
        public void write(JsonObject jsonObject, BnbFeatureEnabledCondition bnbFeatureEnabledCondition) {
            jsonObject.addProperty("feature", bnbFeatureEnabledCondition.featureFlagKey);
        }

        @Override
        public BnbFeatureEnabledCondition read(JsonObject jsonObject) {
            String featureFlagKey = jsonObject.get("feature").getAsString();
            return new BnbFeatureEnabledCondition(featureFlagKey);
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }

}
