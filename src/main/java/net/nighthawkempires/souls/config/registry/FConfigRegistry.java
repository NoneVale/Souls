package net.nighthawkempires.souls.config.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.core.settings.SettingsModel;

import java.util.Map;

public class FConfigRegistry extends AbstractFileRegistry<SettingsModel> implements ConfigRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FConfigRegistry() {
        super("empires", SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, SettingsModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
