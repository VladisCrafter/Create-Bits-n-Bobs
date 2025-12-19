package com.kipti.bnb.foundation.client;

import com.kipti.bnb.CreateBitsnBobs;
import com.simibubi.create.Create;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class BnbAssetLookup {

    /**
     * Custom block models packaged with other partials. Example:
     * models/block/schematicannon/block.json <br>
     * <br>
     * Adding "powered", "vertical" will look for /block_powered_vertical.json
     */
    public static ModelFile partialBaseModel(final DataGenContext<?, ?> ctx, final RegistrateBlockstateProvider prov,
                                             final String... suffix) {
        final StringBuilder subPath = new StringBuilder("/block");
        for (final String suf : suffix)
            if (!suf.isEmpty())
                subPath.append("_").append(suf);
        final String location = "block/" + ctx.getName() + subPath;
        return prov.models()
                .getExistingFile(Create.asResource(location));
    }

    /**
     * Custom block models packaged with other partials. Example:
     * models/block/schematicannon/block.json <br>
     * <br>
     * Adding "powered", "vertical" will look for /block_powered_vertical.json
     */
    public static ModelFile partialNamedBaseModel(final DataGenContext<?, ?> ctx, final RegistrateBlockstateProvider prov, final String name,
                                                  final String... suffix) {
        final StringBuilder subPath = new StringBuilder("/block");
        for (final String suf : suffix)
            if (!suf.isEmpty())
                subPath.append("_").append(suf);
        final String location = "block/" + name + subPath;
        return prov.models()
                .getExistingFile(CreateBitsnBobs.asResource(location));
    }


    /**
     * Generate item model inheriting from a seperate model in
     * models/block/folders[0]/folders[1]/.../item.json "_" will be replaced by the
     * item name
     */
    public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> customBlockItemModel(
            final String name, final String... folders) {
        return (c, p) -> {
            String path = "block";
            for (final String string : folders)
                path += "/" + ("_".equals(string) ? name : string);
            p.withExistingParent(c.getName(), CreateBitsnBobs.asResource(path));
        };
    }

}
