package com.midnightdoggo19.openheart;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenHeart implements ModInitializer {
	public static final String MOD_ID = "open-heart";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Open Heart!");
	}
}