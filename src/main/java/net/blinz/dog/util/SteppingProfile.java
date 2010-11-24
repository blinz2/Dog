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

import java.util.ArrayList;

/**
 * An object detailing how to initialize a given object class.
 * @author Blinz
 */
public final class SteppingProfile<E> {

    private final ArrayList<Step<E>> steps = new ArrayList<Step<E>>();

    /**
     * Adds the given InitStep to this InitProfile.
     * @param step the InitStep to be added
     */
    public final void addStep(final Step<E> step) {
        steps.add(step);
    }

    /**
     * Runs the Steps listed for the class of the given object.
     * @param object the object to be processed
     */
    public final void run(final E object) {
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).run(object);
        }
    }
}
