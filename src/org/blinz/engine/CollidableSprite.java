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
package org.blinz.engine;

import org.blinz.util.Bounds;

/**
 *
 * @author Blinz
 */
public interface CollidableSprite {

    public abstract void collisionReaction(Sprite sprite);

    public abstract void topBumperCollisionReaction(Sprite sprite);

    public abstract void bottumBumperCollisionReaction(Sprite sprite);

    public abstract void leftBumperCollisionReaction(Sprite sprite);

    public abstract void rightBumperCollisionReaction(Sprite sprite);

    public abstract Bounds frontBumper();

    public abstract Bounds backBumper();

    public abstract Bounds leftBumper();

    public abstract Bounds rightBumper();

}
