package com.example.echoesofthepast.event;

import com.example.echoesofthepast.entity.GrievingSoul;
import com.example.echoesofthepast.entity.ModEntities;
import com.example.echoesofthepast.item.ModItems;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = "echoesofthepast")
public class ModEventHandlers {

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.COMRADES_REMAINS);
        }
    }
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DEAD_COMRADE.get(), Zombie.createAttributes().build());

        event.put(ModEntities.TOMB_GUARDIAN.get(),
                Zombie.createAttributes()
                        .add(Attributes.MAX_HEALTH, 200.0D)
                        .build());

        event.put(ModEntities.GRIEVING_SOUL.get(), GrievingSoul.createAttributes().build());
    }

}