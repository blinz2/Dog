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

import net.blinz.core.util.Size;
import net.blinz.dog.util.User;

/**
 * ZoneData contains such items as a master list of Sectors, Sector size, zoneTime,
 * and numerous other pieces of useful data.
 * @author Blinz
 */
final class ZoneData {

    /**
     * A reference to data that users of the API can share amongst all their ZoneObjects.
     */
    Object data;
    private int sectorSize = 2048;
    private int sectorLookupShift = (int) (Math.log(sectorSize) / Math.log(2.0));
    /**
     * The Sector table used to look up Sectors.
     */
    Sector[][] sectors = new Sector[1][1];
    /**
     * The size of the Zone.
     */
    final Size zoneSize = new Size();
    /**
     * List of sprites that need to be removed from the Zone.
     */
    final UnorderedList<BaseSprite> spritesToDelete = new UnorderedList<BaseSprite>();
    /**
     * How many milliseconds that have passed since the start time of the Zone.
     */
    long zoneTime = 0;
    /**
     * How many iterations the Zone loop has been through.
     */
    long zoneCycles = 0;
    /**
     * A list of the User to sprite input associations in the Zone.
     */
    final UserListenerCatalog userListeners = new UserListenerCatalog();
    private String zoneName;
    private boolean paused = false;
    private boolean isClient = false, isServer = false;

    /**
     * Initializes the ZoneData according to what it already stores.
     */
    final void init() {
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                sectors[i][n] = new Sector(i * sectorSize,
                        n * sectorSize);
                sectors[i][n].data = this;
                sectors[i][n].init();
            }
        }
    }

    /**
     * Gets the size of this Zone's Sectors.
     * @return the size of this Zone's Sectors
     */
    final int getSectorSize() {
        return sectorSize;
    }

    /**
     * Sets the Sector size for the Zone.
     * The Sector size MUST be a power of 2.
     * @param size the new size of the Sectors, MUST BE A POWER OF 2
     */
    final void setSectorSize(final int size) {
        sectorSize = size;
        sectorLookupShift = (int) (Math.log(sectorSize) / Math.log(2.0));
    }

    /**
     * Sets whether or no this Zone will perform client specific operations.
     * @param isClient true if this is a client Zone, false otherwise
     */
    final void setClient(final boolean isClient) {
        this.isClient = isClient;
    }

    /**
     * Indicates whether or not this Zone is a client Zone and performs client
     * specific operations.
     * @return true if this Zone is a client, false otherwise
     */
    final boolean isClient() {
        return isClient;
    }

    /**
     * Sets whether or no this Zone will perform server specific operations.
     * @param isServer true if this is a server Zone, false otherwise
     */
    final void setServer(final boolean isServer) {
        this.isServer = isServer;
    }

    /**
     * Indicates whether or not this Zone is a server Zone and performs server
     * specific operations.
     * @return true if this Zone is a server, false otherwise
     */
    final boolean isServer() {
        return isServer;
    }

    /**
     * Sets the name of the Zone this data represents to the name given.
     * @param name the new Zone name
     */
    final void setName(final String name) {
        zoneName = name;
    }

    /**
     * Gets the name of the Zone.
     * @return the name of the Zone this data represents
     */
    final String getName() {
        return zoneName;
    }

    /**
     * Adds the given spite to the Zone that this ZoneData is for.
     * @param sprite the sprite to be added to the Zone
     */
    final void addSprite(final BaseSprite sprite) {
        registerZoneObject(sprite);
        final Sector tl = getSectorOf(sprite.getX(), sprite.getY());
        tl.addSprite(sprite);
    }

    /**
     * Get a reference to the sprites listening to input from users.
     * @return a reference to the sprites listening to input from users.
     */
    final UserListenerCatalog getUserListeners() {
        return userListeners;
    }

    /**
     * The given sprite will now receive input given by the given User.
     * @param user the User to listen for
     * @param sprite the sprite to listen
     */
    final void addUserListener(final User user, final BaseSprite sprite) {
        userListeners.add(user, sprite);
    }

    /**
     * The given sprite will no longer receive input given by the given User.
     * @param user the User to no longer listen for
     * @param sprite the sprite to no longer listen
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
     * Indicates whether or not the Zone of this ZoneData is closed.
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
        return sectors[x >> sectorLookupShift][y >> sectorLookupShift];
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

        return sectors[x >> sectorLookupShift][y >> sectorLookupShift];
    }

    /**
     * Gets the width of Sectors in this ZoneData's Zone.
     * @return the width of Sectors in this Zone
     */
    final int sectorWidth() {
        return sectorSize;
    }

    /**
     * Gets the height of Sectors in this ZoneData's Zone.
     * @return the height of Sectors in this Zone
     */
    final int sectorHeight() {
        return sectorSize;
    }

    /**
     * Gets the width of this ZoneData's Zone.
     * @return the width of the Zone 
     */
    final int getZoneWidth() {
        return zoneSize.width;
    }

    /**
     * Gets the height of this ZoneData's Zone.
     * @return the height of the Zone
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
     * @param object object holding data
     */
    final void registerZoneObject(final ZoneObject object) {
        object.data = this;
        object.internalInit();
    }
}
