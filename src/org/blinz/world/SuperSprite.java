/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009  Blinz <gtalent2@gmail.com>
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
 *
 * @author Blinz
 */
public abstract class SuperSprite extends Sprite {

   private final ArrayList<BaseSprite> subSprites = new ArrayList<BaseSprite>();

    @Override
    public void delete() {
        super.delete();
        if (subSprites != null) {
            for (int i = 0; i < subSprites.size(); i++) {
                subSprites.get(i).delete();
            }
        }
    }

    /**
     * Adds the given sprite to this SuperSprite. Also adds the sprite to the Zone
     * and calls its initialization method.
     * @param sprite
     */
    protected void addSubSprite(BaseSprite sprite) {
        getData().addSprite(sprite);
        subSprites.add(sprite);
        if (sprite instanceof CollidableSprite) {
            getData().collidableObjects.add((CollidableSprite) sprite);
        }
    }

    /**
     * Removes the given sprite from this SuperSprite and the Zone.
     * @param sprite
     */
    protected void removeSubSprite(BaseSprite sprite) {
        subSprites.remove(sprite);
        sprite.delete();
    }
}
