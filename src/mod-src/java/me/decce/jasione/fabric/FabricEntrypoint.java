package me.decce.jasione.fabric;

//? if fabric {
import me.decce.jasione.JasioneMod;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        JasioneMod.init();
    }
}
//?}
