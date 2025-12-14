package com.kipti.bnb.registry;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.foundation.generation.PonderLevelSource;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

public class BnbRegisterEvent {

    public static void onRegisterEvent(RegisterEvent event) {
        event.register(Registries.CHUNK_GENERATOR, registryEvent -> {
            registryEvent.register(CreateBitsnBobs.asResource("ponderous_planes"), PonderLevelSource.CODEC);
            });
    }

}
