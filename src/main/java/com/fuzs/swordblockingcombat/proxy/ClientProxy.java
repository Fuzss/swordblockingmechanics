package com.fuzs.swordblockingcombat.proxy;

import com.fuzs.swordblockingcombat.client.handler.NoCooldownHandler;
import com.fuzs.swordblockingcombat.client.handler.RenderBlockingHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInit() {

        super.onPreInit();
        MinecraftForge.EVENT_BUS.register(new RenderBlockingHandler());
        MinecraftForge.EVENT_BUS.register(new NoCooldownHandler());
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
    }

}
