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
final class ZoneData {

    Object data;
    final short sectorWidth = 2048, sectorHeight = 2048;
    Sector[][] sectors = new Sector[1][1];
    final Size zoneSize = new Size();
    final UnorderedList<BaseSprite> spritesToDelete = new UnorderedList<BaseSprite>();
    long zoneTime = 0;
    long zoneCycles = 0;
    final UserListenerCatalog userListeners = new UserListenerCatalog();
    private String zoneName;
    private boolean paused = false;

    /**
     * Initializes the ZoneData according to what it already stores.
     */
    final void init() {
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                sectors[i][n] = new Sector(i * sectorWidth,
                        n * sectorHeight);
                sectors[i][n].data = this;
                sectors[i][n].init();
            }
        }
    }

    /**
     * Sets the name of the Zone this data represents to the name given.
     * @param name the new Zone name
     */
    final void setName(final String name) {
        zoneName = name;
    }

    /**
     *
     * @return the name of the Zone this data represents
     */
    final String getName() {
        return zoneName;
    }

    final void addSprite(final BaseSprite sprite) {
        registerZoneObject(sprite);
        final Sector tl = getSectorOf(sprite.getX(), sprite.getY());
        tl.addSprite(sprite);
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
    final void addUserListener(final User user, final BaseSprite sprite) {
        userListeners.add(user, sprite);
    }

    /**
     * The given sprite will no longer receive input given by the given User.
     * @param user
     * @param sprite
     */
    final void removeUserListener(final User user, final BaseSprite sprite) {
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
     * @return true if the associated Zone is paused, false otherwise
     */
    final boolean paused() {
        return paused;
    }

    /**
     * Gets the Sector of the specified point in the Zone.
     * @param x the x coordinate of point in the Sector
     * @param y the y coordinate of point in the Sector
     * @return Sector of specified point
     */
    final Sector getSectorOf(final int x, final int y) {
        return sectors[x >> 11][y >> 11];
    }

    /**
     * Gets the Sector of the specified point in the Zone, makes sure the indices
     * are safe with a slight overhead.
     * @param x the x coordinate of point in the Sector
     * @param y the y coordinate of point in the Sector
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

        return sectors[x >> 11][y >> 11];
    }

    /**
     * Gets the width of Sectors in this ZoneData's Zone.
     * @return the width of Sectors in this Zone
     */
    final int sectorWidth() {
        return sectorWidth;
    }

    /**
     * Gets the height of Sectors in this ZoneData's Zone.
     * @return the height of Sectors in this Zone
     */
    final int sectorHeight() {
        return sectorHeight;
    }

    /**
     * Gets the width of this ZoneData's Zone.
     * @return zone width
     */
    final int getZoneWidth() {
        return zoneSize.width;
    }

    /**
     * Gets the height of this ZoneData's Zone.
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
        userListeners.trimLists();
    }

    /**
     * Registers the given ZoneObject with this ZoneObject's Zone.
     * A ZoneObject can only be a member of one Zone at a time.
     * @param object
     */
    final void registerZoneObject(final ZoneObject object) {
        object.data = this;
        object.internalInit();
    }
}
