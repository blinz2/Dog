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
import org.blinz.util.Bounds;
import org.blinz.world.SelectableSprite.Selection;

/**
 *
 * @author Blinz
 */
class CameraSprite {

    private Sector sector;
    private BaseSprite sprite;
    private boolean isSelected = false;

    /**
     * Constructor
     * @param sprite the sprite that this CameraSprite represents
     * @param sector the sector that the sprite that this represents is in
     */
    CameraSprite(final BaseSprite sprite, final Sector sector) {
        this.sprite = sprite;
        this.sector = sector;
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

    /**
     * If the sprite this represents is a SelectibleSprite this calls select.
     * @param user the User that selected this sprite
     * @return whether or not the sprite accepted selection
     */
    final SelectableSprite.Selection select(final User user) {
        if (sprite instanceof SelectableSprite) {
            final SelectableSprite.Selection s = ((SelectableSprite) sprite).select(user);
            if (s == Selection.ACCEPT) {
                isSelected = true;
            }
            return s;
        }
        return Selection.REJECT_CONTINUE;
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

    /**
     * Indicates whether or not this sprite is selected by its Camera.
     * @return true if this CameraSprite is selected by its Camera, false otherwise
     */
    boolean isSelected() {
        return isSelected;
    }
}
