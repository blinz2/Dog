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

    private static ZoneData[] dataList = new ZoneData[128];

    static {
        dataList[0] = new ZoneData();
        dataList[0].zoneSize.setSize(dataList[0].sectorWidth(), dataList[0].sectorHeight());
        dataList[0].init((byte) 0);
    }
    byte zoneID = 0;

    /**
     * Runs the given CollidibleSprite against other CollidibleSprites to check
     * for collisions.
     * @param sprite
     */
    public final void checkCollisions(final CollidibleSprite sprite) {
        Sector tl = getData().getSectorOf(((BaseSprite) sprite).getX(), ((BaseSprite) sprite).getY());
        Sector br = getData().getSectorOf(((BaseSprite) sprite).getX() + ((BaseSprite) sprite).getWidth(),
                ((BaseSprite) sprite).getY() + ((BaseSprite) sprite).getHeight());
        tl.checkCollisionsFor(sprite);
        if (sprite instanceof UpdatingSprite) {
            tl.checkCollisionsFor(sprite);
        }
        if (tl != br) {
            br.checkCollisionsFor(sprite);
            Sector tr = getData().getSectorOf(((BaseSprite) sprite).getX() + ((BaseSprite) sprite).getWidth(),
                    ((BaseSprite) sprite).getY());
            if (tr != br && tl != tr) {
                getData().getSectorOf(((BaseSprite) sprite).getX(),
                        ((BaseSprite) sprite).getY() + ((BaseSprite) sprite).getHeight()).checkCollisionsFor(sprite);
                tr.checkCollisionsFor(sprite);
            }
        }
    }

    protected final Object getMyZoneData() {
        return getData().data;
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
        return dataList[zoneID];
    }

    final static void setZoneData(final int zoneID, ZoneData zoneData) {
        dataList[zoneID] = zoneData;
    }

    void internalInit() {
        init();
    }

    abstract void init();
}
