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

import java.util.Vector;

/**
 * An unordered list with special optimizations for handling unordered items more
 * efficiently than a bare generic List would.
 * @author Blinz
 */
final class UnorderedList<E> {

    private final Vector<E> elements = new Vector<E>();

    /**
     * Indicates whether or not this list is empty.
     * @return true if the list is empty, false otherwise
     */
    final boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Clears all elements from the list.
     */
    final void clear() {
        elements.clear();
    }

    /**
     *
     * @param i the index of the sprite to be retrieved
     * @return the sprite at the given location
     */
    final E get(final int i) {
        return elements.get(i);
    }

    /**
     * Adds the given sprite to this SpriteList.
     * @param element added to this SpriteList
     */
    final void add(final E element) {
        synchronized (elements) {
            elements.add(element);
        }
    }
    
    /**
     * Removes the sprite at the given location by moving the sprite at the end
     * of list to the specified location.
     * @param i the index of the sprite to be removed
     * @return the sprite removed
     */
    final E remove(final int i) {
        if (elements.size() > i) {
            synchronized (elements) {
                final E e = elements.set(i, elements.get(elements.size() - 1));
                elements.remove(elements.size() - 1);
                return e;
            }
        } else {
            return null;
        }
    }

    /**
     * Removes the given sprite from the list if present.
     * @param element is removed from the list if present
     */
    final boolean remove(final E element) {
        synchronized (elements) {
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i) == element) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the number of items in the list.
     * @return the number of elements in this list
     */
    final int size() {
        return elements.size();
    }

    /**
     * Moves the elements of this list to an array just big enough for them all.
     */
    final void trimToSize() {
        elements.trimToSize();
    }
}
