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
package net.blinz.dog.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Blinz
 */
public final class HierarchicalStepProcessor<E> {

    /**
     * Used to track the initialization steps for different classes.
     */
    private final HashMap<Class, ArrayList<Step<E>>> initTable = new HashMap<Class, ArrayList<Step<E>>>();
    private final HashMap<Class, SteppingProfile<E>> initProfiles = new HashMap<Class, SteppingProfile<E>>();
    private final ArrayList<TopLevelMethod<E>> topLevelMethods = new ArrayList<TopLevelMethod<E>>();

    /**
     * Adds a TopLevelMethod to this HierarchicalStepProcessor to be factored into
     * all execution profiles.
     *
     * Use of this Step results in the method being called as if it were called from
     * the reference directly, but it is called with the Steps of the last class to
     * implement it.
     *
     * @param clss the class to add the Step for
     * @param step the Step to add
     */
    public final void addTopLevelMethod(final TopLevelMethod<E> step) {
        topLevelMethods.add(step);
    }

    /**
     * Adds the given Step for the given class.
     * @param clss the class to add the Step for
     * @param step the Step to add
     */
    public final void addStep(final Class clss, final Step<E> step) {
        ArrayList<Step<E>> steps = initTable.get(clss);
        if (steps == null) {
            steps = new ArrayList<Step<E>>();
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
     * Adds the given SteppingProfile to this HierarchicalStepProcessor.
     * @param clss the Class to associate with the given Profile
     * @param profile the SteppingProfile to add
     */
    public final SteppingProfile<E> addProfile(final Class clss, final SteppingProfile<E> profile) {
        synchronized (initProfiles) {
            if (!initProfiles.containsKey(clss)) {
                initProfiles.put(clss, profile);
                return profile;
            }
            return initProfiles.get(clss);
        }
    }

    /**
     * Gets a list of the steps for the given Class.
     * @param clss the Class of the steps you want
     * @return the steps for the given Class
     */
    public final ArrayList<Step<E>> getSteps(final Class clss) {
        return initTable.get(clss);
    }

    /**
     * Gets a SteppingProfile associated with the given Class.
     * @param clss the Class for which you need a Stepping profile
     * @return a SteppingProfile associated with the given Class if there is one, null otherwise
     */
    public final SteppingProfile<E> getProfile(final Class clss) {
        SteppingProfile profile = initProfiles.get(clss);
        if (profile == null) {
            synchronized (initProfiles) {
                if (!initProfiles.containsKey(clss)) {
                    initProfiles.put(clss, profile);
                } else {
                    profile = initProfiles.get(clss);
                }
            }
        }
        return profile;
    }

    /**
     * Runs the Steps assigned for the given object.
     * @param object the object run Steps on
     */
    final void run(final E object) {
        final Class cl = object.getClass();
        SteppingProfile<E> profile = getProfile(cl);
        if (profile == null) {
            addProfile(cl, profile);
        }
        generateProfile(profile = new SteppingProfile(), cl, (ArrayList<TopLevelMethod<E>>) topLevelMethods.clone());

        profile.run(object);
    }

    /**
     * Generates a SteppingProfile for the given Class.
     * @param profile the profile to generate
     * @param level the level in the class hierarchy that this method is on
     * @param topLevels the TopLevelMethods that still haven't found their homes
     */
    private final void generateProfile(final SteppingProfile<E> profile, final Class level, final ArrayList<TopLevelMethod<E>> topLevels) {
        if (level == Object.class) {
            return;
        }
        ArrayList<TopLevelMethod<? extends E>> topLevelsToAdd = null;
        for (int i = 0; i < topLevelsToAdd.size(); i++) {
            try {
                final Method current = level.getDeclaredMethod(topLevels.get(i).getMethod().getName());
                if (current != null) {
                    if (topLevelsToAdd == null) {
                        topLevelsToAdd = new ArrayList<TopLevelMethod<? extends E>>();
                    }
                    topLevelsToAdd.add(topLevels.remove(i));
                }
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
                Logger.getLogger(HierarchicalStepProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        generateProfile(profile, level.getSuperclass(), topLevels);

        final ArrayList<Step<E>> steps = getSteps(level);
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                profile.addStep(steps.get(i));
            }
        }
    }
}
