package com.midnightdoggo19.openheart;

import net.fabricmc.api.ClientModInitializer;

public class OpenHeartClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LocateBlockCommand.register();
    }
}
