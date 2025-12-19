package com.kipti.bnb.foundation;

import com.kipti.bnb.content.horizontal_chute.HorizontalChuteBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        HorizontalChuteBlockEntity.registerCapabilities(event);
    }

}
