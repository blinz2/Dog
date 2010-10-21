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

import net.blinz.core.util.Bounds;
import net.blinz.core.util.Position3D;
import net.blinz.core.util.Size;

/**
 * A generic implementation of most of BaseSprites features for quick use when
 * optimization is not possible or not an issue.
 * @author Blinz
 */
public abstract class Sprite extends BaseSprite {

    private int x = 0, y = 0;
    private float layer = 0;
    private short width = 1, height = 1;

    /**
     * Gets the Size of this Sprite.
     * @return Size object representing the sprite's size
     */
    @Override
    public final Size getSize() {
        return new Size(width, height);
    }

    /**
     * Gets the width of this Sprite.
     * @return width of Sprite
     */
    @Override
    public final int getWidth() {
        return width;
    }

    /**
     * Gets the height of this Sprite.
     * @return height of Sprite
     */
    @Override
    public final int getHeight() {
        return height;
    }

    /**
     * Gets a Position3D object representing this Sprites top left corner and layer.
     * @return a Position3D object representing the Sprite's location and layer
     */
    @Override
    public final Position3D getPosition() {
        return new Position3D(x, y, (int) layer);
    }

    /**
     * Gets the x coordinate of this Sprite.
     * @return the x coordinate of this Sprite
     */
    @Override
    public final int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of this Sprite
     * @return the y coordinate of this Sprite
     */
    @Override
    public final int getY() {
        return y;
    }

    /**
     * Gets the layer of this Sprite, which indicates its place the draw order of
     * the sprites in the Zone.
     * @return the layer of this Sprite.
     */
    @Override
    public final float getLayer() {
        return layer;
    }

    /**
     * Gets a Bounds object representing the size and location of this Sprite.
     * @return a Bounds object representing the size and location of this Sprite
     */
    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, width, height);
    }

    /**
     * Moves this sprite down the specified distance.
     * @param distance the distance this is to move
     */
    public final void moveDown(final int distance) {
        setY(y + distance);
    }

    /**
     * Moves this sprite up the specified distance.
     * @param distance the distance this is to move
     */
    public final void moveUp(final int distance) {
        setY(y - distance);
    }

    /**
     * Moves this sprite right the specified distance.
     * @param distance the distance this is to move
     */
    public final void moveRight(final int distance) {
        setX(x + distance);
    }

    /**
     * Moves this sprite left the specified distance.
     * @param distance the distance this is to move
     */
    public final void moveLeft(final int distance) {
        setX(x - distance);
    }

    /**
     * Updates the width of this Sprite.
     * NOT for manual invocation.
     * @param width the new width of this Sprite
     */
    @Override
    protected final void updateWidth(final int width) {
        this.width = (short) width;
    }

    /**
     * Updates the height of this Sprite.
     * NOT for manual invocation.
     * @param height the new height of this Sprite
     */
    @Override
    protected final void updateHeight(final int height) {
        this.height = (short) height;
    }

    /**
     * Updates the x of this Sprite.
     * NOT for manual invocation.
     * @param x the new x coordinate of this Sprite
     */
    @Override
    protected final void updateX(final int x) {
        this.x = x;
    }

    /**
     * Updates the y of this Sprite.
     * NOT for manual invocation.
     * @param y the new y coordinate of this Sprite
     */
    @Override
    protected final void updateY(final int y) {
        this.y = y;
    }

    /**
     * Updates the layer of this Sprite.
     * NOT for manual invocation.
     * @param layer the new layer coordinate of this Sprite
     */
    @Override
    protected final void updateLayer(final float layer) {
        this.layer = layer;
    }
}
