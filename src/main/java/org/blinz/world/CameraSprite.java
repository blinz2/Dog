/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009-2010  Blinz <gtalent2@gmail.com>
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
import org.blinz.graphics.Graphics;
import org.blinz.input.MouseListener;
import org.blinz.util.Bounds;

/**
 *
 * @author Blinz
 */
class CameraSprite implements MouseListener {

    private short useCount = 1;
    private BaseSprite sprite;
    private boolean isSelected = false;

    CameraSprite(final BaseSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void buttonClick(int buttonNumber, int numberOfClicks, int cursorX, int cursorY) {
        if (sprite instanceof MouseListener) {
            MouseListener listener = (MouseListener) sprite;
            listener.buttonClick(buttonNumber, numberOfClicks, cursorX, cursorY);
        }
    }

    @Override
    public void buttonPress(int buttonNumber, int cursorX, int cursorY) {
        if (sprite instanceof MouseListener) {
            MouseListener listener = (MouseListener) sprite;
            listener.buttonPress(buttonNumber, cursorX, cursorY);
        }
    }

    @Override
    public void buttonRelease(int buttonNumber, int cursorX, int cursorY) {
        if (sprite instanceof MouseListener) {
            MouseListener listener = (MouseListener) sprite;
            listener.buttonRelease(buttonNumber, cursorX, cursorY);
        }
    }

    /**
     * Increments the number of Sectors in the Camera that have this CameraSprite's
     * sprite.
     */
    final void incrementUseCount() {
        useCount++;
    }

    /**
     * Decrements the number of Sectors in the Camera that have this CameraSprite's
     * sprite.
     */
    final void decrementUseCount() {
        useCount--;
    }

    /**
     * Returns the number of Sector's in the Camera that have this CameraSprite's
     * sprite.
     * @return int
     */
    final int getUsageCount() {
        return useCount;
    }

    final BaseSprite getSprite() {
        return sprite;
    }

    final int getX() {
        return sprite.getX();
    }

    final int getY() {
        return sprite.getY();
    }

    final float getLayer() {
        return sprite.getLayer();
    }

    final int getWidth() {
        return sprite.getWidth();
    }

    final int getHeight() {
        return sprite.getHeight();
    }

    final void draw(final Graphics graphics, final Bounds bounds) {
        sprite.draw(graphics, bounds);
        if (sprite instanceof SelectibleSprite)
	    ((SelectibleSprite) sprite).drawSelectionIndicator(graphics);
    }

    boolean isMouseListener() {
        return sprite instanceof MouseListener;
    }

    /**
     * If the sprite this represents is a SelectibleSprite this calls select.
     * @param user the User that selected this sprite
     */
    final void select(final User user) {
        if (sprite instanceof SelectibleSprite) {
            ((SelectibleSprite) sprite).select(user);
            isSelected = true;
        }
    }

    /**
     * If the sprite this represents is a SelectibleSprite this calls deselect.
     * @param user the User that deselected this sprite
     */
    final void deselect(final User user) {
        if (sprite instanceof SelectibleSprite) {
            ((SelectibleSprite) sprite).deselect(user);
            isSelected = false;
        }
    }

    boolean isSelected() {
        return isSelected;
    }
}
