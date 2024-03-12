package org.vcell.restq.config;

import cbit.vcell.resource.PropertyLoader;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashSet;
import java.util.Set;

public class CDIVCellConfigProvider implements PropertyLoader.VCellConfigProvider {
    @Override
    public String getConfigValue(String propertyName) {
        return ConfigProvider.getConfig().getConfigValue(propertyName).getValue();
    }

    @Override
    public Set<String> getConfigNames() {
        HashSet<String> names = new HashSet<>();
        ConfigProvider.getConfig().getPropertyNames().forEach(names::add);
        return names;
    }

    @Override
    public void setConfigValue(String propertyName, String value) {
        System.setProperty(propertyName, value);
    }
}
