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

import org.blinz.util.User;

/**
 * Used to allow ZoneObservers to denote selection when a Sprite is click on.
 * @author Blinz
 */
public interface SelectibleSprite {

    public abstract void drawSelectionIndicator();

    /**
     * Called when the sprite is selected from a ZoneObserver.
     * @param user the User that selected this sprite
     */
    public void select(User user);

    /**
     * Called when the sprite loses selected status.
     * @param user the User that deselected this sprite
     */
    public void deselect(User user);
}
