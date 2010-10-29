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

import java.util.HashMap;
import java.util.ArrayList;

/**
 * The root class for many objects relating to Zone.
 * @author Blinz
 */
public abstract class ZoneObject {

    /**
     * Used to help handle multiple initialization steps for ZoneObjects.
     */
    abstract class InitStep {

        private int orderPlacement = 0;

        /**
         * Gets the place in the order of InitSteps that this will be executed.
         * @return the place in the order of InitSteps that this will be executed
         */
        final int getOrderPlacement() {
            return orderPlacement;
        }

        /**
         * Sets the place in the order of InitSteps that this will be executed.
         * @param orderPlacement the place in the order of InitSteps that this will be executed
         */
        final void setOrderPlacement(final int orderPlacement) {
            this.orderPlacement = orderPlacement;
        }

        /**
         * Runs this InitStep.
         */
        abstract void run();
    }

    /**
     * Used to call the primary initialization method of class.
     */
    final class MainInit extends InitStep {

        @Override
        void run() {
            internalInit();
        }
    }
    /**
     * Used to track the initialization steps for different classes.
     */
    private final HashMap<String, ArrayList<InitStep>> initTable = new HashMap<String, ArrayList<InitStep>>();
    private final static Zone defaultZone = new Zone();
    private final static ZoneData defaultData = defaultZone.getData();
    ZoneData data = defaultData;

    /**
     * Gets the Zone that this ZoneObject is associated with.
     * @return the Zone that this ZoneObject is associated with
     */
    public final Zone getZone() {
        return data.getZone();
    }

    /**
     * Gets a long representing the number of cycles the zone has gone through.
     * @return a long representing the number of cycles the zone has gone through
     */
    protected final long zoneCycles() {
        return getData().zoneCycles;
    }

    /**
     * Gets the shared data object of the Zone that this is a part of.
     * @return the shared data object of the Zone that this is a part of.
     */
    protected final Object getSharedZoneData() {
        return getData().data;
    }

    /**
     * Gets the data for the Zone of this ZoneObject.
     *
     * ZoneData contains such items as a master list of Sectors, Sector size, zoneTime,
     * and numerous other pieces of useful data.
     *
     * @return ZoneData
     */
    final ZoneData getData() {
        return data;
    }

    /**
     * Sets the ZoneData object for all ZoneObjects in the Zone to access without any further
     * memory penalties from additional pointers.
     * @param zoneID the ID for the Zone
     * @param zoneData the ZoneData object
     */
    final void setZoneData(final ZoneData zoneData) {
        data = zoneData;
    }

    /**
     * Drops its association with its Zone.
     * @param zone the Zone to drop
     */
    void dropZone(final Zone zone) {
        if (data.getZone() == zone) {
            data = null;
        }
    }

    /**
     * Initializes this ZoneObject for use.
     */
    void internalInit() {
        init();
    }

    /**
     * An abstract initialization method allowing users of this API to specify
     * what should be done when the object joins its Zone.
     */
    abstract void init();
}
