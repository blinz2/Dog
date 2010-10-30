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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.blinz.core.util.HierarchicalStepProcessor;
import net.blinz.core.util.Step;
import net.blinz.core.util.TopLevelMethod;

/**
 * The root class for many objects relating to Zone.
 * @author Blinz
 */
public abstract class ZoneObject {

    /**
     * Used to help handle multiple initialization steps for ZoneObjects.
     * @author Blinz
     */
    public static abstract class InitStep<E extends ZoneObject> extends Step<E> {
    }

    /**
     * Used to call the primary initialization method of class.
     */
    final static class MainInit extends InitStep<ZoneObject> {

        @Override
        public void run(final ZoneObject e) {
            e.init();
        }
    }
    private final static HierarchicalStepProcessor init = new HierarchicalStepProcessor();
    private final static Zone defaultZone = new Zone();
    private final static ZoneData defaultData = defaultZone.getData();
    /**
     * The ZoneData that this ZoneObject holds.
     */
    ZoneData data = defaultData;

    static {
        try {
            init.addTopLevelMethod(new TopLevelMethod<ZoneObject>(ZoneObject.class.getDeclaredMethod("init")) {

                @Override
                public void run(ZoneObject object) {
                    object.init();
                }
            });
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ZoneObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ZoneObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds the given InitStep for the given class.
     * @param clss the class to add the InitStep for
     * @param step the InitStep to add
     */
    public final static void addInitStep(final Class clss, final InitStep step) {
        init.addStep(clss, step);
    }

    /**
     * Gets the Zone that this ZoneObject is associated with.
     * @return the Zone that this ZoneObject is associated with
     */
    public final Zone getZone() {
        return data.getZone();
    }

    /**
     * An abstract initialization method allowing users of this API to specify
     * what should be done when the object joins its Zone.
     */
    public abstract void init();

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
     * Creates an InitProfile for the given class.
     * @param object the ZoneObject to initialize
     */
    final static void runInitializations(final ZoneObject object) {
        init.getProfile(object.getClass());
    }
}
