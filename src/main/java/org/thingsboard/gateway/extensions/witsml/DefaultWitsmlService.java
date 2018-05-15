/**
 * Copyright © 2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.gateway.extensions.witsml;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.gateway.extensions.ExtensionUpdate;
import org.thingsboard.gateway.extensions.witsml.conf.WitsmlConfiguration;
import org.thingsboard.gateway.extensions.witsml.monitors.Witsml1411ServerMonitor;
import org.thingsboard.gateway.extensions.witsml.monitors.WitsmlServerMonitor;
import org.thingsboard.gateway.service.conf.TbExtensionConfiguration;
import org.thingsboard.gateway.service.gateway.GatewayService;
import org.thingsboard.gateway.util.ConfigurationTools;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DefaultWitsmlService extends ExtensionUpdate implements WitsmlService{
    private final GatewayService gateway;
    private TbExtensionConfiguration currentConfiguration;
    private List<WitsmlServerMonitor> monitors;

    public DefaultWitsmlService(GatewayService gateway) {
        this.gateway = gateway;
    }

    @Override
    public TbExtensionConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    @Override
    public void init(TbExtensionConfiguration configurationNode, Boolean isRemote) throws Exception {
        currentConfiguration = configurationNode;
        WitsmlConfiguration configuration;
        try {
            if(isRemote) {
                configuration = ConfigurationTools.readConfiguration(configurationNode.getConfiguration(), WitsmlConfiguration.class);
            } else {
                configuration = ConfigurationTools.readFileConfiguration(configurationNode.getExtensionConfiguration(), WitsmlConfiguration.class);
            }
        } catch (Exception e) {
            log.error("WITSML service configuration failed!", gateway.getTenantLabel(), e);
            gateway.onConfigurationError(e, currentConfiguration);
            throw e;
        }

        try {
            monitors = configuration.getServers().stream().map(c -> {
                if(c.getVersion().equalsIgnoreCase("v1411"))
                    return new Witsml1411ServerMonitor(gateway, c);
                else
                    return new Witsml1411ServerMonitor(gateway, c);
            }).collect(Collectors.toList());
            for (WitsmlServerMonitor monitor : monitors) {
                monitor.connect();
            }
        } catch (Exception e) {
            log.error("Witsml service initialization failed!", gateway.getTenantLabel(), e);
            gateway.onConfigurationError(e, currentConfiguration);
            throw e;
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
