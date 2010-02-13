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

import org.blinz.util.Size;
import org.blinz.util.User;

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
    final UnorderedList<BaseSprite> spritesToDelete = new UnorderedList<BaseSprite>();
    long zoneTime = 0;
    long zoneCycles = 0;
    byte id;
    final UserListenerCatalog userListeners = new UserListenerCatalog();
    private String zoneName;
    private boolean paused = false;

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

    /**
     * Sets the name of the Zone this data represents to the name given.
     * @param name the new Zone name
     */
    final void setName(String name) {
        zoneName = name;
    }

    /**
     *
     * @return the name of the Zone this data represents
     */
    final String getName() {
        return zoneName;
    }

    final void addSprite(BaseSprite sprite) {
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
     * Returns a reference to the sprites listening to input from users.
     * @return a reference to the sprites listening to input from users.
     */
    final UserListenerCatalog getUserListeners() {
        return userListeners;
    }

    /**
     * The given sprite will now recieve input given by the given User.
     * @param user
     * @param sprite
     */
    final void addUserListener(User user, BaseSprite sprite) {
        userListeners.add(user, sprite);
    }

    /**
     * The given sprite will no longer recieve input given by the given User.
     * @param user
     * @param sprite
     */
    final void removeUserListener(User user, BaseSprite sprite) {
        userListeners.remove(user, sprite);
    }

    /**
     * Pauses the zone.
     *
     * Note: This pause is not meant for short pauses, it will take at least 250
     * milliseconds before the Zone resumes execution even if resume() is called
     * immediately. Also pauses user input going to the sprites, but no the Zone.
     */
    final void pause() {
        paused = true;
        userListeners.pause();
    }

    /**
     * Unpauses the zone.
     */
    final void unpause() {
        paused = false;
        userListeners.unpause();
    }

    /**
     * 
     * @return true if the assocaiated Zone is paused, false otherwise
     */
    final boolean paused() {
        return paused;
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
        spritesToDelete.trimToSize();
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                sectors[i][n].trimLists();
            }
        }
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
