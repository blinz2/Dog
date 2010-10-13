/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009 - 2010  Blinz <gtalent2@gmail.com>
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
package org.blinz.dog.world;

/**
 * An interface used for sprites that need to know if they've collided with other sprites.
 * @author Blinz
 */
public interface CollidableSprite {

    /**
     * Method called to notify the sprite that it has collided with another.
     * @param sprite
     */
    void collide(BaseSprite sprite);
}
