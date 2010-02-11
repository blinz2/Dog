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
final class SpriteList {

    private final ArrayList<BaseSprite> sprites = new ArrayList<BaseSprite>();

    /**
     *
     * @param i the index of the sprite to be retrieved
     * @return the sprite at the given location
     */
    final BaseSprite get(int i) {
            return sprites.get(i);
    }

    /**
     * Adds the given sprite to this SpriteList.
     * @param sprite added to this SpriteList
     */
    final void add(BaseSprite sprite) {
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
    final BaseSprite remove(int i) {
        synchronized (sprites) {
            return sprites.set(i, sprites.remove(sprites.size() - 1));
        }
    }

    /**
     * Removes the given sprite from the list if present.
     * @param sprite is removed from the list if present.
     */
    final void remove(BaseSprite sprite) {
        synchronized (sprites) {
            for (int i = 0; i < sprites.size(); i++) {
                if (sprites.get(i) == sprite) {
                    remove(i);
                    break;
                }
            }
        }
    }

    /**
     *
     * @return the number of elements in this list.
     */
    final int size() {
        return sprites.size();
    }
}
