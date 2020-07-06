package net.nighthawkempires.souls.config.registry;

import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.core.settings.SettingsModel;
import net.nighthawkempires.souls.config.ConfigModel;

import java.util.Map;

public interface ConfigRegistry extends Registry<SettingsModel> {

    default SettingsModel fromDataSection(String key, DataSection data) {
        if (key.equalsIgnoreCase("souls_config"))
            return new ConfigModel(data);
        return null;
    }

    default ConfigModel getConfig() {
        return (ConfigModel) fromKey("souls_config").orElseGet(() -> register(new ConfigModel()));
    }

    @Deprecated
    Map<String, SettingsModel> getRegisteredData();

    default boolean configExists() {
        return fromKey("souls_config").isPresent();
    }
}
