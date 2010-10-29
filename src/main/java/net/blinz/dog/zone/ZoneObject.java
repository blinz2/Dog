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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The root class for many objects relating to Zone.
 * @author Blinz
 */
public abstract class ZoneObject {

    /**
     * Used to help handle multiple initialization steps for ZoneObjects.
     * @author Blinz
     */
    public static abstract class InitStep<E extends ZoneObject> {

        /**
         * Runs this InitStep.
         * @param object the object to run initialization on
         */
        protected abstract void run(E object);
    }

    /**
     * Used to call the primary initialization method of class.
     */
    final static class MainInit extends InitStep {

        @Override
        protected void run(final ZoneObject e) {
            e.init();
        }
    }

    /**
     * An object detailing how to initialize a given object class.
     */
    private final static class InitProfile {

        private Class cl;
        private final ArrayList<InitStep> steps = new ArrayList<InitStep>();

        /**
         * Adds the given InitStep to this InitProfile.
         * @param step the InitStep to be added
         */
        private void addStep(final InitStep step) {
            steps.add(step);
        }

        /**
         * Runs the InitSteps listed for the class of the given ZoneObject.
         * @param object the ZoneObject to be initialized
         */
        private final void init(final ZoneObject object) {
            for (int i = 0; i < steps.size(); i++) {
                steps.get(i).run(object);
            }
        }
    }
    /**
     * Used to track the initialization steps for different classes.
     */
    private final static HashMap<Class, ArrayList<InitStep>> initTable = new HashMap<Class, ArrayList<InitStep>>();
    private final static HashMap<Class, InitProfile> initProfiles = new HashMap<Class, InitProfile>();
    private final static MainInit main = new MainInit();
    private final static Zone defaultZone = new Zone();
    private final static ZoneData defaultData = defaultZone.getData();
    /**
     * The ZoneData that this ZoneObject holds.
     */
    ZoneData data = defaultData;

    static {
        initTable.put(ZoneObject.class, new ArrayList<InitStep>());
    }

    /**
     * Adds the given InitStep for the given class.
     * @param clss the class to add the InitStep for
     * @param step the InitStep to add
     */
    public final static void addInitStep(final Class clss, final InitStep step) {
        ArrayList<InitStep> steps = initTable.get(clss);
        if (steps == null) {
            steps = new ArrayList<InitStep>();
            synchronized (initTable) {
                if (!initTable.containsKey(clss)) {
                    initTable.put(clss, steps);
                } else {
                    steps = initTable.get(clss);
                }
            }
        }
        synchronized (steps) {
            steps.add(step);
        }
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
        final Class cl = object.getClass();
        InitProfile profile = initProfiles.get(cl);
        if (profile == null) {
            generateInitProfile(profile = new InitProfile(), cl, false);
            synchronized (initProfiles) {
                if (!initProfiles.containsKey(cl)) {
                    initProfiles.put(cl, profile);
                } else {
                    profile = initProfiles.get(cl);
                }
            }
        }
        profile.init(object);
    }

    /**
     * Calls the initialization methods for the given ZoneObject.
     * @param profile the profile to generate
     * @param level the level in the class hierarchy that this method is on
     * @param mainFound whether or not the main init method has been found yet
     */
    private final static void generateInitProfile(final InitProfile profile, final Class level, boolean mainFound) {
        if (level == ZoneObject.class.getSuperclass()) {
            return;
        }

        generateInitProfile(profile, level.getSuperclass(), mainFound);

        final ArrayList<InitStep> steps = initTable.get(level);
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                profile.addStep(steps.get(i));
            }
        }

        if (!mainFound) {
            try {
                level.getDeclaredMethod("init");
                profile.addStep(main);
                mainFound = true;
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
                Logger.getLogger(ZoneObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
