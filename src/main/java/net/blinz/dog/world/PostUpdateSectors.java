/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2010  Blinz <gtalent2@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.blinz.dog.world;

import java.util.ArrayList;
import org.blinz.util.concurrency.SynchronizedTask;

/**
 * Invokes the postUpdate method for the associated Sectors.
 * @author Blinz
 */
class PostUpdateSectors extends SynchronizedTask {

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
