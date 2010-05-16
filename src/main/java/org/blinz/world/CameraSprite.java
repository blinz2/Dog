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

import org.blinz.util.User;
import org.blinz.graphics.Graphics;
import org.blinz.input.MouseListener;
import org.blinz.util.Bounds;

/**
 *
 * @author Blinz
 */
class CameraSprite implements MouseListener {

    private Sector sector;
    private BaseSprite sprite;
    private boolean isSelected = false;

    CameraSprite(final BaseSprite sprite) {
	this.sprite = sprite;
    }

    @Override
    public void buttonClick(final int buttonNumber, final int numberOfClicks, final int cursorX, final int cursorY) {
	if (sprite instanceof MouseListener) {
	    MouseListener listener = (MouseListener) sprite;
	    listener.buttonClick(buttonNumber, numberOfClicks, cursorX, cursorY);
	}
    }

    @Override
    public void buttonPress(final int buttonNumber, final int cursorX, final int cursorY) {
	if (sprite instanceof MouseListener) {
	    MouseListener listener = (MouseListener) sprite;
	    listener.buttonPress(buttonNumber, cursorX, cursorY);
	}
    }

    @Override
    public void buttonRelease(final int buttonNumber, final int cursorX, final int cursorY) {
	if (sprite instanceof MouseListener) {
	    MouseListener listener = (MouseListener) sprite;
	    listener.buttonRelease(buttonNumber, cursorX, cursorY);
	}
    }

    /**
     * Sets the Sector that the sprite this CameraSprite represents belongs to.
     * @param sector the Sector that the sprite this CameraSprite represents belongs to
     */
    final void setSector(final Sector sector) {
	this.sector = sector;
    }

    /**
     * Gets the Sector that the sprite this CameraSprite represents belongs to.
     * @return the Sector that the sprite this CameraSprite represents belongs to
     */
    final Sector getSector() {
	return sector;
    }

    /**
     *
     * @return true if the contained sprite is selectable, false otherwise
     */
    final boolean isSelectable() {
	return sprite instanceof SelectableSprite;
    }

    /**
     * Gets the sprite this CameraSprite represents.
     * @return the sprite this CameraSprite represents.
     */
    final BaseSprite getSprite() {
	return sprite;
    }

    /**
     * Gets the x coordinate of the sprite this represents
     * @return the x coordinate of the sprite this represents
     */
    final int getX() {
	return sprite.getX();
    }

    /**
     * Gets the y coordinate of the sprite this represents
     * @return the y coordinate of the sprite this represents
     */
    final int getY() {
	return sprite.getY();
    }

    /**
     * Gets the layer of the sprite this represents
     * @return the layer of the sprite this represents
     */
    final float getLayer() {
	return sprite.getLayer();
    }

    /**
     * Gets the width of the sprite this represents
     * @return the width of the sprite this represents
     */
    final int getWidth() {
	return sprite.getWidth();
    }

    /**
     * Gets the height of the sprite this represents
     * @return the height of the sprite this represents
     */
    final int getHeight() {
	return sprite.getHeight();
    }

    final void draw(final Graphics graphics, final Bounds bounds) {
	sprite.draw(graphics, bounds);
	if (sprite instanceof SelectableSprite && isSelected()) {
	    ((SelectableSprite) sprite).drawSelectionIndicator(graphics, bounds);
	}
    }

    boolean isMouseListener() {
	return sprite instanceof MouseListener;
    }

    /**
     * If the sprite this represents is a SelectibleSprite this calls select.
     * @param user the User that selected this sprite
     */
    final void select(final User user) {
	if (sprite instanceof SelectableSprite) {
	    ((SelectableSprite) sprite).select(user);
	    isSelected = true;
	}
    }

    /**
     * If the sprite this represents is a SelectibleSprite this calls deselect.
     * @param user the User that deselected this sprite
     */
    final void deselect(final User user) {
	if (sprite instanceof SelectableSprite) {
	    ((SelectableSprite) sprite).deselect(user);
	    isSelected = false;
	}
    }

    boolean isSelected() {
	return isSelected;
    }
}
