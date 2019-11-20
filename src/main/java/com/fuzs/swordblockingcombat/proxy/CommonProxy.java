package com.fuzs.swordblockingcombat.proxy;

import com.fuzs.swordblockingcombat.handler.InitiateBlockHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class CommonProxy {

    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
    }

}