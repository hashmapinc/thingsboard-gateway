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
package org.thingsboard.gateway.extensions.witsml.monitors;

import com.hashmapinc.tempus.witsml.api.WitsmlClient;
import com.hashmapinc.tempus.witsml.api.WitsmlVersion;
import com.hashmapinc.tempus.witsml.client.Client;
import org.thingsboard.gateway.extensions.witsml.conf.WitsmlServerConfiguration;
import org.thingsboard.gateway.service.gateway.GatewayService;

public abstract class AbstractWitsmlServerMonitor implements WitsmlServerMonitor{
    private static String BASE_URL_TEMPLATE = "https://%s:%d";

    protected final GatewayService gateway;
    protected final WitsmlServerConfiguration configuration;
    protected WitsmlClient client;

    public AbstractWitsmlServerMonitor(GatewayService gateway, WitsmlServerConfiguration configuration) {
        this.gateway = gateway;
        this.configuration = configuration;
    }


    @Override
    public void connect() {
        String host = configuration.getHost();
        host = host.trim();
        if (host.contains("://")) {
            host = host.substring(host.lastIndexOf('/') + 1, host.length());
        }
        String url = String.format(
                BASE_URL_TEMPLATE,
                host,
                configuration.getPort());
        client = new Client(url);
        client.setUserName(configuration.getUsername());
        client.setPassword(configuration.getPassword());
        client.setVersion(getVersion());
        client.connect();
        scanForWells();
    }

    protected abstract WitsmlVersion getVersion();
    protected abstract void scanForWells();
}
