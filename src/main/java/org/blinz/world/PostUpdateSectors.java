/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blinz.world;

import java.util.ArrayList;
import org.blinz.util.concurrency.SynchronizedTask;

/**
 * Invokes the postUpdate method for the associated Sectors.
 * @author Blinz
 */
public class PostUpdateSectors extends SynchronizedTask {

    private Sector[] sectors;

    /**
     * Constructor
     * @param sectors list of the Sectors it is to process.
     */
    PostUpdateSectors(final ArrayList<Sector> sectors) {
        this.sectors = sectors.toArray(new Sector[sectors.size()]);
    }

    /**
     * Invokes the post update methods for the associated Sectors.
     */
    @Override
    protected final void run() {
        for (int i = 0; i < sectors.length; i++) {
            sectors[i].postUpdate();
        }
    }
}
