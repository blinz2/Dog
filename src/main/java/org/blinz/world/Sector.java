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

import java.util.ArrayList;
import org.blinz.util.Bounds;
import java.util.Vector;

/**
 *
 * @author Blinz
 */
final class Sector extends ZoneObject {

    Sector topNeighbor, bottomNeighbor, rightNeighbor, leftNeighbor;
    private final UnorderedList<UpdatingSprite> updatingSprites = new UnorderedList<UpdatingSprite>();
    private final Vector<UpdatingSprite> updatingSpritesToAdd = new Vector<UpdatingSprite>();
    private final Vector<UpdatingSprite> updatingSpritesToRemove = new Vector<UpdatingSprite>();
    private final UnorderedList<BaseSprite> memberSprites = new UnorderedList<BaseSprite>();
    private final Vector<BaseSprite> addedSprites = new Vector<BaseSprite>();
    private final Vector<BaseSprite> removedSprites = new Vector<BaseSprite>();
    private final UnorderedList<CollidableSprite> collidibleSprites = new UnorderedList<CollidableSprite>();
    private final Bounds bounds = new Bounds();

    /**
     * Sector constructer.
     * @param x x coordinate of this Sector
     * @param y y coordinate of this Sector
     */
    Sector(final int x, final int y) {
	bounds.setPosition(x, y);
    }

    @Override
    public String toString() {
	return bounds.x + ", " + bounds.y;
    }

    @Override
    void init() {
	bounds.setSize(getData().sectorWidth(), getData().sectorHeight());
	findNeighbors();
    }

    /**
     * Gets all the sprites added to this sector in the current cycle.
     * @return all the sprites added to this sector in the current cycle
     */
    final Vector<BaseSprite> getAddedSprites() {
	return addedSprites;
    }

    /**
     * Gets all the sprites removed from this sector in the current cycle.
     * @return all the sprites removed from this sector in the current cycle
     */
    final Vector<BaseSprite> getRemovedSprites() {
	return removedSprites;
    }

    /**
     * Updates the sprites in this Sector.
     */
    final void update() {
	for (int i = 0; i < updatingSprites.size(); i++) {
	    updatingSprites.get(i).update();
	}
    }

    /**
     * Takes care of modifications made during the update.
     */
    final void postUpdate() {
	manageUpdatingSprites();
	addedSprites.clear();
	removedSprites.clear();
    }

    /**
     * Gets the list of sprites in this Sector. This is the actual list that the
     * Sector relies on, so don't access it during the Sector update phases or
     * modify it.
     * @return the list of sprites in this Sector
     */
    final UnorderedList<BaseSprite> getSprites() {
	return memberSprites;
    }

    /**
     * Indicates whether or not the given point might intersect some of the sprites
     * in this Sector.
     * @param bounds
     * @return true if it is within the possible range of sprites, false otherwise
     */
    final boolean withinSpriteRange(final Bounds bounds) {
	return bounds.intersects(this.bounds.x, this.bounds.y,
		2 * this.bounds.width, 2 * this.bounds.height);
    }

    /**
     * Returns the x position of this Sector in the array.
     * @return
     */
    final int getXIndex() {
	return bounds.getX() / getData().sectorWidth;
    }

    /**
     * Returns the y position of this Sector in the array.
     * @return
     */
    final int getYIndex() {
	return bounds.getY() / getData().sectorHeight;
    }

    /**
     * Returns the x location of this Sector.
     * @return x
     */
    final int getX() {
	return bounds.getX();
    }

    /**
     * Returns the y location of this Sector.
     * @return y
     */
    final int getY() {
	return bounds.getY();
    }

    /**
     * Returns the width of Sectors in this Zone.
     * @return width of this Sector
     */
    final int getWidth() {
	return getData().sectorWidth;
    }

    /**
     * Returns the height of Sectors in this Zone.
     * @return height of this Sector
     */
    final int getHeight() {
	return getData().sectorHeight;
    }

    /**
     * Adds given sprite to this Sector.
     * @param sprite
     */
    final void addSprite(final BaseSprite sprite) {
	if (sprite instanceof UpdatingSprite) {
	    updatingSpritesToAdd.add((UpdatingSprite) sprite);
	}
	if (sprite instanceof CollidableSprite) {
	    collidibleSprites.add((CollidableSprite) sprite);
	}
	memberSprites.add(sprite);
	addedSprites.add(sprite);
    }

    /**
     * Removes the given sprite from this sector.
     * @param sprite the sprite to remove
     * @param destination the Sector the given sprite is going to
     */
    final void removeSprite(final BaseSprite sprite, final Sector destination) {
	if (sprite instanceof UpdatingSprite) {
	    updatingSpritesToRemove.add((UpdatingSprite) sprite);
	}
	if (sprite instanceof CollidableSprite) {
	    synchronized (collidibleSprites) {
		collidibleSprites.remove((CollidableSprite) sprite);
	    }
	}
	memberSprites.remove(sprite);
    }

    final void checkCollisionsFor(final CollidableSprite sprite) {
	synchronized (collidibleSprites) {
	    for (int i = 0; i < collidibleSprites.size(); i++) {
		BaseSprite s1 = (BaseSprite) sprite, s2 = (BaseSprite) collidibleSprites.get(i);
		if ((Math.abs(s1.getLayer() - s2.getLayer()) < 1)
			&& Bounds.intersects(s1.getX(), s1.getY(), s1.getWidth(), s1.getHeight(),
			s2.getX(), s2.getY(), s2.getWidth(), s2.getHeight())) {
		    if (s1 != s2) {
			((CollidableSprite) s1).collide(s2);
			((CollidableSprite) s2).collide(s1);
		    }
		}
	    }
	}
    }

    /**
     * Removes the given UpdatingSprite now. For use only by the main update process
     * in Zone.
     * @param sprite
     */
    synchronized final void deleteUpdatingSprite(final UpdatingSprite sprite) {
	if (updatingSprites.remove(sprite)) {
	    updatingSpritesToAdd.remove(sprite);
	}
    }

    /**
     *
     * @param x
     * @param y
     * @return true if given point is within the bounds of this Sector, false otherwise
     */
    final boolean contains(final int x, final int y) {
	return bounds.contains(x, y);
    }

    /**
     * Returns true if the specified bounds intersects with the bounds of this Sector,
     * false if it does not.
     * @param bounds
     * @return true if the specified coordinates intersect this Sector
     */
    final boolean intersects(final Bounds bounds) {
	return this.bounds.intersects(bounds);
    }

    /**
     * Returns true if the specified bounds intersects with the bounds of this Sector,
     * false if it does not.
     * @param x
     * @param y
     * @param width
     * @param height
     * @return true if the specified coordinates intersect this Sector
     */
    final boolean intersects(final int x, final int y, final int width, final int height) {
	return bounds.intersects(x, y, width, height);
    }

    final void findNeighbors() {
	int ix = bounds.getX() / getData().sectorWidth;
	int iy = bounds.getY() / getData().sectorHeight;

	if (ix > 0) {
	    leftNeighbor = getData().sectors[ix - 1][iy];
	}
	if (iy > 0) {
	    topNeighbor = getData().sectors[ix][iy - 1];
	}
	if (ix < getData().sectors.length - 1) {
	    rightNeighbor = getData().sectors[ix + 1][iy];
	}
	if (iy < getData().sectors[ix].length - 1) {
	    bottomNeighbor = getData().sectors[ix][iy + 1];
	}
    }

    /**
     * Trims the size of lists.
     */
    final void trimLists() {
	updatingSpritesToAdd.trimToSize();
	updatingSpritesToRemove.trimToSize();
	addedSprites.trimToSize();
    }

    /**
     * Manages sprites on the updating sprites list.
     */
    private final void manageUpdatingSprites() {
	for (int i = updatingSpritesToRemove.size() - 1; i > -1; i--) {
	    updatingSprites.remove(updatingSpritesToRemove.remove(i));
	}
	for (int i = updatingSpritesToAdd.size() - 1; i > -1; i--) {
	    updatingSprites.add(updatingSpritesToAdd.remove(i));
	}
    }
}
