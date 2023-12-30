package net.shirojr.boatism.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.instance.SoundInstanceState;
import net.shirojr.boatism.sound.instance.custom.BoatismSoundInstance;
import net.shirojr.boatism.util.SoundInstanceIdentifier;

import java.util.ArrayList;
import java.util.List;

public class BoatismSoundManager {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<SoundInstanceEntry> activeSoundInstances = new ArrayList<>();

    public List<SoundInstanceEntry> getActiveSoundInstances() {
        return this.activeSoundInstances;
    }

    /**
     * Adds new SoundInstance to active sound instances.<br><br>
     * <h3>Excluded SoundInstance handling</h3><br>
     * This will only apply, if the new SoundInstance makes use of the {@linkplain SoundInstanceState} Interface.
     * New entries always have priority over already
     * existing entries. If entries have the same {@linkplain SoundInstanceIdentifier} the entry, which is already
     * in the list, will be removed.<br>
     * <h4>Criteria:</h4>
     * <ul>
     *     <li>if new SoundInstance is a main sound, it will stop all other currently running main sounds</li>
     *     <li>if new SoundInstance is manually excluding other sounds, those will be stopped as well</li>
     * </ul>
     *
     * @param soundInstanceIdentifier The identifying enum of the SoundInstance
     * @param soundInstance           the actual object, which will be passed to the client BoatismSoundManager
     */
    public void start(SoundInstanceIdentifier soundInstanceIdentifier, BoatismSoundInstance soundInstance) {
        //FIXME: don't use map, entries should be possible to play multiple times for multiple entities
        if (!(soundInstance instanceof SoundInstanceState state)) return;
        List<SoundInstanceEntry> unsupportedSoundInstances = new ArrayList<>();

        for (var activeInstance : this.activeSoundInstances) {
            if (!(activeInstance.instance instanceof SoundInstanceState activeInstanceState)) continue;
            if (state.isMainSound() && activeInstanceState.isMainSound())
                unsupportedSoundInstances.add(activeInstance);
            for (SoundInstanceIdentifier unsupportedInstance : state.unsupportedInstances()) {
                if (activeInstance.identifier.equals(unsupportedInstance))
                    unsupportedSoundInstances.add(activeInstance);
            }
        }
        unsupportedSoundInstances.forEach(this::stop);

        this.activeSoundInstances.add(new SoundInstanceEntry(soundInstanceIdentifier, soundInstance));
        this.client.getSoundManager().play(soundInstance);
    }

    public void stop(SoundInstanceEntry soundInstanceEntry) {
        for (SoundInstanceEntry entry : this.activeSoundInstances) {
            if (soundInstanceEntry.identifier != entry.identifier) continue;
            Identifier identifier = soundInstanceEntry.instance.getId();
            //this.client.getSoundManager().stopSounds(identifier, soundInstanceEntry.identifier.getCategory());
            this.client.getSoundManager().stop(soundInstanceEntry.instance);
        }
        removeEntriesFromList(List.of(soundInstanceEntry));
    }

    public void stopAllSoundInstancesForBoatEngineEntity(BoatEngineEntity boatEngine) {
        for (var entry : this.activeSoundInstances) {
            if (!entry.instance.getBoatEngineEntity().equals(boatEngine)) continue;
            client.getSoundManager().stop(entry.instance);
        }
        this.activeSoundInstances.clear();
    }

    private void removeEntriesFromList(List<SoundInstanceEntry> unsupportedSoundInstances) {
        for (var entry : unsupportedSoundInstances) {
            this.activeSoundInstances.remove(entry);
        }
    }

    private record SoundInstanceEntry(SoundInstanceIdentifier identifier, BoatismSoundInstance instance) {
    }
}