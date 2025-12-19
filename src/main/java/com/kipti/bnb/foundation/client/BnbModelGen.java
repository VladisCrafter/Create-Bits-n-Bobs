package com.kipti.bnb.foundation.client;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.item.BlockItem;

public class BnbModelGen {

    public static <I extends BlockItem, P> NonNullFunction<ItemBuilder<I, P>, P> customItemModel(String name, String... path) {
        return b -> b.model(BnbAssetLookup.customBlockItemModel(name, path))
                .build();
    }

}
