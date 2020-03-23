package com.fuzs.swordblockingcombat.proxy;

import com.fuzs.swordblockingcombat.common.handler.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.handler.CombatFoodHandler;
import com.fuzs.swordblockingcombat.common.handler.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

@SuppressWarnings("unused")
public class CommonProxy {

    public void onPreInit() {

        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        MinecraftForge.EVENT_BUS.register(new ClassicCombatHandler());
        if (!Loader.isModLoaded("applecore")) {

            MinecraftForge.EVENT_BUS.register(new CombatFoodHandler());
        }
    }

    public void onPostInit() {

        ItemBlockingHelper.sync();
    }

}