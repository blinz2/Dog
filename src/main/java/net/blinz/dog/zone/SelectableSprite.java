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

import net.blinz.core.graphics.Graphics;
import net.blinz.core.util.Bounds;
import net.blinz.dog.util.User;

/**
 * Used to allow ZoneObservers to denote selection when a Sprite is click on.
 * @author Blinz
 */
public interface SelectableSprite {
    
    /**
     * Used to indicate how to handle being selected.
     */
    public enum SelectionResponse {

        /**
         * Accept the selection.
         */
        ACCEPT, 
        /**
         * Deny the selection but continue looking for another sprite to SelectableSprites lower down to select.
         */
        REJECT_CONTINUE,
        /**
         * Deny the selection and don't look for another sprite to select.
         */
        REJECT_STOP;
    }

    /**
     * Called to draw on top of the primary draw method when this sprite is
     * selected.
     * @param gfx Graphics object with which to draw the selection indicator.
     */
    public void drawSelectionIndicator(final Graphics gfx, final Bounds bounds);

    /**
     * Called when the sprite is selected from a ZoneObserver.
     * @param user the User that selected this sprite
     * @return indication of how to handle the selection
     */
    public SelectionResponse select(final User user);

    /**
     * Called when the sprite loses selected status.
     * @param user the User that deselected this sprite
     */
    public void deselect(final User user);
}
