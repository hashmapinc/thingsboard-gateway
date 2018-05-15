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

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells;
import com.hashmapinc.tempus.WitsmlObjects.v1311.WellStatus;
import com.hashmapinc.tempus.witsml.api.WitsmlVersion;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.gateway.extensions.witsml.conf.WitsmlServerConfiguration;
import org.thingsboard.gateway.service.gateway.GatewayService;

@Slf4j
public class Witsml1311ServerMonitor extends AbstractWitsmlServerMonitor{

    public Witsml1311ServerMonitor(GatewayService gateway, WitsmlServerConfiguration configuration) {
        super(gateway, configuration);
    }

    @Override
    protected WitsmlVersion getVersion() {
        return WitsmlVersion.VERSION_1311;
    }

    @Override
    protected void scanForWells() {
        String wellsXml;
        com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells wells = null;
        try {
            wellsXml = client.getWells();
            wells = WitsmlMarshal.deserialize(wellsXml, ObjWells.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wells != null) {
            log.trace("Found {} wells.", wells.getWell().size());
            wells.getWell().forEach(well -> {
                log.trace("Well Name: [{}]", well.getName());
                log.trace("Well Legal: [{}]", well.getNameLegal());
                WellStatus statusWell = well.getStatusWell();
                //PrintWellbores(c, well.getUid());
            });
        }
    }
}
