package net.shirojr.boatism.sound.instance.custom;

import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.sound.instance.SoundInstanceState;

public class EngineSubmergedSoundInstance extends BoatismSoundInstance implements SoundInstanceState {
    public EngineSubmergedSoundInstance(BoatEngineEntity boatEngineEntity) {
        super(boatEngineEntity, BoatismSounds.BOAT_ENGINE_UNDERWATER);
    }

    @Override
    public boolean canPlay() {
        return super.canPlay() && boatEngineEntity.isSubmerged();
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public boolean isMainSound() {
        return true;
    }
}