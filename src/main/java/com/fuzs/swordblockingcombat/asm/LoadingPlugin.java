package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(SwordBlockingCombat.NAME)
@IFMLLoadingPlugin.TransformerExclusions("com.fuzs.swordblockingcombat.asm")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class LoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ "com.fuzs.swordblockingcombat.asm.ClassTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}