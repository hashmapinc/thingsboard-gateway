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

import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells;
import com.hashmapinc.tempus.witsml.api.WitsmlClient;
import com.hashmapinc.tempus.witsml.api.WitsmlVersion;
import com.hashmapinc.tempus.witsml.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.gateway.extensions.witsml.conf.WitsmlServerConfiguration;
import org.thingsboard.gateway.service.gateway.GatewayService;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;

@Slf4j
public class Witsml1411ServerMonitor extends AbstractWitsmlServerMonitor {

    public Witsml1411ServerMonitor(GatewayService gateway, WitsmlServerConfiguration configuration) {
        super(gateway, configuration);
    }

    @Override
    protected WitsmlVersion getVersion() {
        return WitsmlVersion.VERSION_1411;
    }

    protected void scanForWells(){
        ObjWells wells = null;
        try {
            wells = client.getWellsAsObj();
        } catch (FileNotFoundException | RemoteException | JAXBException e) {
            log.error("Error executing get wells as obj: {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wells != null) {
            log.trace("Found {} wells.", wells.getWell().size());
            wells.getWell().forEach(well -> {
                log.trace("Well Name: [{}]", well.getName());
                log.trace("Well Legal: [{}]", well.getNameLegal());
                //PrintWellbores(c, well.getUid());
            });
        }
    }
}
