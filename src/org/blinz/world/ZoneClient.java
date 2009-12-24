/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009  Blinz <gtalent2@gmail.com>
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
package org.blinz.world;

import java.util.Vector;

/**
 *
 * @author Blinz
 */
class ZoneClient {

    private static final Vector<ZoneClient> zoneClients = new Vector<ZoneClient>();
    private int zoneID, clientID;

    private ZoneClient(int zoneID, int clientID) {
        this.zoneID = zoneID;
        this.clientID = clientID;
    }

    ZoneClient fetchClient(int zoneID, int clientID) {
        //if clientID is not local
        for (int i = 0; i < zoneClients.size(); i++) {
            ZoneClient c = zoneClients.get(i);
            if (c.zoneID == zoneID && c.clientID == clientID) {
                return c;
            }
        }
        return new ZoneClient(zoneID, clientID);
    }

}
