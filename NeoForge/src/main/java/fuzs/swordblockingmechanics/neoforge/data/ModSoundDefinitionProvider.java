package fuzs.swordblockingmechanics.neoforge.data;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.client.data.v3.sounds.AbstractSoundProvider;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.common.init.ModRegistry;

public class ModSoundDefinitionProvider extends AbstractSoundProvider {

    public ModSoundDefinitionProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void registerSounds() {
        this.add(ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.value(),
                sound(SwordBlockingMechanics.id("item/sword/block1")).volume(0.8),
                sound(SwordBlockingMechanics.id("item/sword/block2")).volume(0.8));
    }
}
