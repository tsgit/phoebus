/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.epics.pva.server;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import org.epics.pva.PVASettings;
import org.epics.pva.data.PVADouble;
import org.epics.pva.data.PVAInt;
import org.epics.pva.data.PVAString;
import org.epics.pva.data.PVAStructure;
import org.epics.pva.data.nt.PVATimeStamp;

/** PVA Server Demo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ServerDemo
{
    public static void main(String[] args) throws Exception
    {
        LogManager.getLogManager().readConfiguration(PVASettings.class.getResourceAsStream("/logging.properties"));

        // Create PVA Server
        final PVAServer server = new PVAServer();

        // Create data structures to serve
        final PVATimeStamp time = new PVATimeStamp();
        final PVAStructure data = new PVAStructure("demo", "demo_t",
                                                   new PVADouble("value", 3.13),
                                                   new PVAString("tag",   "Hello!"),
                                                   time);
        time.set(Instant.now());

        // Create PVs
        final ServerPV pv = server.createPV("demo", data);
        final ServerPV pv2 = server.createPV("demo2", data);

        // Update PVs
        for (int i=0; i<30000; ++i)
        {
            TimeUnit.SECONDS.sleep(1);

            // Update the data, tell server that it changed.
            // Server figures out what changed.
            final PVADouble value = data.get("value");
            value.set(value.get() + 1);
            time.set(Instant.now());

            pv.update(data);
            pv2.update(data);
        }

        // Note that updated data type must match the originally served data.
        // Cannot change the structure layout for existing PV.
        try
        {
            pv.update(new PVAStructure("xx", "xxx", new PVAInt("xx", 47)));
        }
        catch (Exception ex)
        {
            // Expected
            if (! ex.getMessage().toLowerCase().contains("incompatibl"))
                throw ex;
        }

        // Close server (real world server tends to run forever, though)
        server.close();
    }
}
