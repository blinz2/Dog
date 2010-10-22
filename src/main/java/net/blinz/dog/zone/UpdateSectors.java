/*
 * Dog - A project for making highly scalable non-clustered game and simulation environments.
 * Copyright (C) 2009-2010 BlinzProject <gtalent2@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.blinz.dog.zone;

import java.util.ArrayList;
import net.blinz.core.util.concurrency.SynchronizedTask;

/**
 * Updates the Sectors and sprites.
 * @author Blinz
 */
class UpdateSectors extends SynchronizedTask {

    private Sector[] sectors;

    /**
     * Constructor
     * @param sectors list of the Sectors it is to process.
     */
    UpdateSectors(final ArrayList<Sector> sectors) {
        this.sectors = sectors.toArray(new Sector[sectors.size()]);
    }

    /**
     * Invokes the update methods for the associated Sectors.
     */
    @Override
    protected final void run() {
        for (int i = 0; i < sectors.length; i++) {
            sectors[i].update();
        }
    }
}
