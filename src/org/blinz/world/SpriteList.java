/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2010  Blinz <gtalent2@gmail.com>
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

import java.util.ArrayList;

/**
 * A sprite list with special optimizations for handling sprites more efficiently
 * than a bare generic List would.
 * @author Blinz
 */
final class SpriteList <E> {

    private final ArrayList<E> sprites = new ArrayList<E>();

    /**
     *
     * @param i the index of the sprite to be retrieved
     * @return the sprite at the given location
     */
    final E get(int i) {
        return sprites.get(i);
    }

    /**
     * Adds the given sprite to this SpriteList.
     * @param sprite added to this SpriteList
     */
    final void add(E sprite) {
        synchronized (sprites) {
            sprites.add(sprite);
        }
    }

    /**
     * Removes the sprite at the given location by moving the sprite at the end
     * of list to the specified location.
     * @param i the index of the sprite to be removed
     * @return the sprite removed
     */
    final E remove(int i) {
        if (sprites.size() > 0) {
            synchronized (sprites) {
                return sprites.set(i, sprites.remove(sprites.size() - 1));
            }
        } else {
            return null;
        }
    }

    /**
     * Removes the given sprite from the list if present.
     * @param sprite is removed from the list if present.
     */
    final boolean remove(E sprite) {
        synchronized (sprites) {
            for (int i = 0; i < sprites.size(); i++) {
                if (sprites.get(i) == sprite) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return the number of elements in this list.
     */
    final int size() {
        return sprites.size();
    }

    /**
     * Moves the elements of this list to an array just big enough for them all.
     */
    final void trimToSize() {
        sprites.trimToSize();
    }
}
