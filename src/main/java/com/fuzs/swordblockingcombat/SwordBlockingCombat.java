package com.fuzs.swordblockingcombat;

import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import com.fuzs.swordblockingcombat.proxy.CommonProxy;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = SwordBlockingCombat.MODID,
        name = SwordBlockingCombat.NAME,
        version = SwordBlockingCombat.VERSION,
        acceptedMinecraftVersions = SwordBlockingCombat.RANGE,
        dependencies = SwordBlockingCombat.DEPENDENCIES,
        certificateFingerprint = SwordBlockingCombat.FINGERPRINT
)
@Mod.EventBusSubscriber(modid = SwordBlockingCombat.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class SwordBlockingCombat {

    public static final String MODID = "swordblockingcombat";
    public static final String NAME = "Sword Blocking Combat";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12.1, 1.12.2]";
    public static final String DEPENDENCIES = "required-after:forge@[14.22.1,)";
    public static final String CLIENT_PROXY_CLASS = "com.fuzs.swordblockingcombat.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.fuzs.swordblockingcombat.proxy.ServerProxy";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(SwordBlockingCombat.NAME);

    @SidedProxy(clientSide = SwordBlockingCombat.CLIENT_PROXY_CLASS, serverSide = SwordBlockingCombat.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent evt) {
        proxy.onPreInit();
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent evt) {
        proxy.onPostInit();
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent evt) {
        LOGGER.warn("Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {

        if (evt.getModID().equals(SwordBlockingCombat.MODID)) {

            ConfigManager.sync(SwordBlockingCombat.MODID, Config.Type.INSTANCE);
            ItemBlockingHelper.sync();
        }

    }

}
