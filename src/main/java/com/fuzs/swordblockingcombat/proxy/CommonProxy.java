package com.fuzs.swordblockingcombat.proxy;

import com.fuzs.swordblockingcombat.common.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.CombatFoodHandler;
import com.fuzs.swordblockingcombat.common.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class CommonProxy {

    public void onPreInit() {

        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        MinecraftForge.EVENT_BUS.register(new ClassicCombatHandler());
        MinecraftForge.EVENT_BUS.register(new CombatFoodHandler());
        ItemBlockingHelper.sync();
    }

}