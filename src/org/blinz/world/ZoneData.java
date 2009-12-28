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

import java.util.ArrayList;
import org.blinz.util.Size;

/**
 * ZoneData contains such items as a master list of Sectors, Sector size, zoneTime,
 * and numerous other pieces of useful data.
 * @author Blinz
 */
class ZoneData {

    SharedZoneData data;
    final Size sectorSize = new Size(2048, 2048);
    Sector[][] sectors = new Sector[1][1];
    final Size zoneSize = new Size();
    final ArrayList<BaseSprite> sectorlessSprites = new ArrayList<BaseSprite>();
    final ArrayList<BaseSprite> updatingSprites = new ArrayList<BaseSprite>();
    final ArrayList<CollidableSprite> collidableObjects = new ArrayList<CollidableSprite>();
    final ArrayList<BaseSprite> spritesToDelete = new ArrayList<BaseSprite>();
    long zoneTime = 0;
    long zoneCycles = 0;
    byte id;

    /**
     * Initializes the ZoneData according to what it already stores.
     * @param zoneID
     */
    final void init(final byte zoneID) {
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                sectors[i][n] = new Sector(i * sectorSize.width,
                        n * sectorSize.height);
                sectors[i][n].zoneID = zoneID;
                sectors[i][n].init();
            }
        }
        id = zoneID;
    }

    protected void addSprite(BaseSprite sprite) {
        registerZoneObject(sprite);

        Sector tl = getSectorOf(sprite.getX(), sprite.getY());
        Sector br = getSectorOf(sprite.getX() + sprite.getWidth(),
                sprite.getY() + sprite.getHeight());
        tl.addIntersectingSprite(sprite);
        tl.addSprite(sprite);
        if (tl != br) {
            br.addIntersectingSprite(sprite);

            //if the following condition evaluates to false the only way which br
            //could have gone outside the area is if the bottom right corner is
            //in the sector bellow the primary, thus there is no question whether
            //or not the sprite should be added to bl
            Sector tr = getSectorOf(sprite.getX() + sprite.getWidth(), sprite.getY());
            if (tr != tl) {
                tr.addIntersectingSprite(sprite);
                //bl
                getSectorOf(sprite.getX(), sprite.getY() + sprite.getHeight()).addIntersectingSprite(sprite);
            }
        }
    }

    /**
     * Returns the Sector of the specified point in the Zone.
     * @param x
     * @param y
     * @return Sector of specified point
     */
    final Sector getSectorOf(int x, int y) {
        return sectors[x / sectorSize.width][y / sectorSize.height];
    }

    /**
     * Returns the Sector of the specified point in the Zone, makes sure the indices
     * are safe with a slight overhead.
     * @param x
     * @param y
     * @return Sector of specified point
     */
    final Sector getSectorOfSafe(int x, int y) {
        if (x < 0) {
            x = 0;
        } else if (x > zoneSize.width) {
            x = zoneSize.width;
        }

        if (y < 0) {
            y = 0;
        } else if (y > zoneSize.height) {
            y = zoneSize.height;
        }

        return sectors[x / sectorSize.width][y / sectorSize.height];
    }

    /**
     * Returns the width of Sectors in this ZoneData's Zone.
     * @return int
     */
    final int sectorWidth() {
        return sectorSize.width;
    }

    /**
     * Returns the height of Sectors in this ZoneData's Zone.
     * @return int
     */
    final int sectorHeight() {
        return sectorSize.height;
    }

    /**
     * Returns the width of this ZoneData's Zone.
     * @return zone width
     */
    final int getZoneWidth() {
        return zoneSize.width;
    }

    /**
     * Returns the height of this ZoneData's Zone.
     * @return zone height
     */
    final int getZoneHeight() {
        return zoneSize.height;
    }

    /**
     * Trims excessively large lists.
     */
    final void trimLists() {
        updatingSprites.trimToSize();
        collidableObjects.trimToSize();
        sectorlessSprites.trimToSize();
        spritesToDelete.trimToSize();
    }

    /**
     * Registers the given ZoneObject with this ZoneObject's Zone.
     * A ZoneObject can only be a member of one Zone at a time.
     * @param object
     */
    final void registerZoneObject(ZoneObject object) {
        object.zoneID = id;
        object.init();
    }
}