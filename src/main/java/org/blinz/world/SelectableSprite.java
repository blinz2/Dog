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
package org.blinz.world;

import org.blinz.util.Bounds;
import org.blinz.util.User;
import org.blinz.graphics.Graphics;

/**
 * Used to allow ZoneObservers to denote selection when a Sprite is click on.
 * @author Blinz
 */
public interface SelectableSprite {

    /**
     * Called to draw on top of the primary draw method when this sprite is
     * selected.
     * @param Graphics object with which to draw the selection indicator.
     */
    public void drawSelectionIndicator(final Graphics gfx, final Bounds bounds);

    /**
     * Called when the sprite is selected from a ZoneObserver.
     * @param user the User that selected this sprite
     */
    public void select(final User user);

    /**
     * Called when the sprite loses selected status.
     * @param user the User that deselected this sprite
     */
    public void deselect(final User user);
}
