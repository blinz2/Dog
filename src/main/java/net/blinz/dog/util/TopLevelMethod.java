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

/**
 * A Step type that calls the given method on the highest level object in the tree.
 *
 * Use of this Step results in the method being called as if it were called from
 * the reference directly, but it is called with the Steps of the last class to
 * implement it.
 *
 * @author Blinz
 */
public abstract class TopLevelMethod<E> extends Step<E> {

    private Method method;

    /**
     * Constructor
     * @param method the target method
     */
    public TopLevelMethod(final Method method) {
        this.method = method;
    }

    /**
     * Gets the target Method associated with this Step.
     * @return the target Method associated with this Step
     */
    public final Method getMethod() {
        return method;
    }
}
