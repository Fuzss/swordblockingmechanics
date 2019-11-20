package com.fuzs.swordblockingcombat;

import com.fuzs.swordblockingcombat.handler.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.handler.RenderBlockingHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = SwordBlockingCombat.MODID,
        name = SwordBlockingCombat.NAME,
        version = SwordBlockingCombat.VERSION,
        acceptedMinecraftVersions = SwordBlockingCombat.RANGE,
        certificateFingerprint = SwordBlockingCombat.FINGERPRINT
)
@SuppressWarnings({"WeakerAccess", "unused"})
public class SwordBlockingCombat {

    public static final String MODID = "swordblockingcombat";
    public static final String NAME = "Sword Blocking Combat";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.11.2, 1.12.2]";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(SwordBlockingCombat.NAME);

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        MinecraftForge.EVENT_BUS.register(new RenderBlockingHandler());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent evt) {
        LOGGER.warn("Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

}
