/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009-2010  Blinz <gtalent2@gmail.com>
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

/**
 *
 * @author Blinz
 */
abstract class ZoneObject {

    private static final class DataHolder {

        static ZoneData[] dataList = new ZoneData[128];

        DataHolder() {
            dataList[0] = new ZoneData();
            dataList[0].zoneSize.setSize(dataList[0].sectorWidth(), dataList[0].sectorHeight());
            dataList[0].init((byte) 0);
        }
    }
    byte zoneID = 0;

    protected final Object getMyZoneData() {
        return getData().data;
    }

    final void setZoneData(int zoneID, ZoneData zoneData) {
        DataHolder.dataList[zoneID] = zoneData;
    }

    /**
     * Returns the data for the Zone of this ZoneObject.
     *
     * ZoneData contains such items as a master list of Sectors, Sector size, zoneTime,
     * and numerous other pieces of useful data.
     *
     * @return ZoneData
     */
    final ZoneData getData() {
        return DataHolder.dataList[zoneID];
    }

    abstract void init();
}
