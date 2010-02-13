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

import org.blinz.util.Bounds;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Blinz
 */
final class Sector extends ZoneObject {

    Sector topNeighbor, bottomNeighbor, rightNeighbor, leftNeighbor;
    private final Vector<UpdatingSprite> updatingSprites = new Vector<UpdatingSprite>();
    private final Vector<UpdatingSprite> updatingSpritesToAdd = new Vector<UpdatingSprite>();
    private final ArrayList<UpdatingSprite> updatingSpritesToRemove = new ArrayList<UpdatingSprite>();
    /**
     * Sprites that intersect this Sector can be seen by observers viewing this
     * Sector from above.
     */
    private final SpriteList<BaseSprite> intersectingSprites = new SpriteList<BaseSprite>();
    private final Vector<Camera> observers = new Vector<Camera>();
    private final Bounds bounds = new Bounds();
    /**
     * The index of the next sprite to update.
     */
    private int nextSprite = 0;

    /**
     * Sector constructer.
     * @param x x coordinate of this Sectoer
     * @param y y coordinate of this Sectoer
     */
    Sector(int x, int y) {
        bounds.setPosition(x, y);
    }

    @Override
    void init() {
        findNeighbors();
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
        removeOldUpdatingSprites();
        addNewUpdatingSprites();
        nextSprite = 0;
    }

    /**
     * Returns the x position of this Sector in the array.
     * @return
     */
    final int getXIndex() {
        return bounds.getX() / getData().sectorSize.width;
    }

    /**
     * Returns the y position of this Sector in the array.
     * @return
     */
    final int getYIndex() {
        return bounds.getY() / getData().sectorSize.height;
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
        return getData().sectorSize.width;
    }

    /**
     * Returns the height of Sectors in this Zone.
     * @return height of this Sector
     */
    final int getHeight() {
        return getData().sectorSize.height;
    }

    /**
     * Adds given Camera to this Sector to recieve current and incoming sprites.
     * @param sprite
     */
    final void addCamera(Camera camera) {
        observers.add(camera);
        for (int i = 0; i < intersectingSprites.size(); i++) {
            camera.addSprite(intersectingSprites.get(i));
        }
    }

    /**
     * Removes the given Camera from this Sector.
     * @param observer
     */
    final void removeCamera(Camera observer) {
        observers.remove(observer);
        for (int i = 0; i < intersectingSprites.size(); i++) {
            observer.decrementSpriteUsage(intersectingSprites.get(i));
        }
    }

    /**
     * Adds given sprite to this Sector.
     * @param sprite
     */
    protected final void addSprite(BaseSprite sprite) {
        if (sprite instanceof UpdatingSprite) {
            updatingSpritesToAdd.add((UpdatingSprite) sprite);
        }
    }

    /**
     * Removes the given sprite from this sector.
     * @param sprite
     */
    final void removeSprite(BaseSprite sprite) {
        if (sprite instanceof UpdatingSprite) {
            updatingSpritesToRemove.add((UpdatingSprite) sprite);
        }
    }

    /**
     * Adds given sprite to the list of sprites intersecting this Sector.
     * @param sprite
     */
    final void addIntersectingSprite(BaseSprite sprite) {
        intersectingSprites.add(sprite);
        for (Camera observer : observers) {
            observer.addSprite(sprite);
        }
    }

    /**
     * Removes the given sprite from the list of sprites intersecting this Sector.
     * @param sprite
     */
    final void removeIntersectingSprite(BaseSprite sprite) {
        intersectingSprites.remove(sprite);
        for (Camera observer : observers) {
            observer.decrementSpriteUsage(sprite);
        }
    }

    /**
     * Removes the given sprite from the list of sprites intersecting this Sector.
     * @param sprite
     */
    synchronized final void deleteIntersectingSprite(BaseSprite sprite) {
        removeIntersectingSprite(sprite);
    }

    /**
     * Removes the given UpdatingSprite now. For use only by the main update process
     * in Zone.
     * @param sprite
     */
    synchronized final void deleteUpdatingSprite(UpdatingSprite sprite) {
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
    final boolean contains(int x, int y) {
        bounds.setSize(getData().sectorSize);
        return bounds.contains(x, y);
    }

    /**
     * Returns true if the specified bounds intersects with the bounds of this Sector,
     * false if it does not.
     * @param bounds
     * @return true if the specified coordinates intersect this Sector
     */
    final boolean intersects(Bounds bounds) {
        this.bounds.setSize(getData().sectorSize);
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
    final boolean intersects(int x, int y, int width, int height) {
        this.bounds.setSize(getData().sectorSize);
        return this.bounds.intersects(x, y, width, height);
    }

    final void findNeighbors() {
        int ix = bounds.getX() / getData().sectorSize.width;
        int iy = bounds.getY() / getData().sectorSize.height;

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
        intersectingSprites.trimToSize();
        observers.trimToSize();
    }

    /**
     * Adds sprites on the spritesToAdd list.
     */
    private final void addNewUpdatingSprites() {
        for (int i = updatingSpritesToAdd.size() - 1; i > -1; i--) {
            updatingSprites.add(updatingSpritesToAdd.remove(i));
        }
    }

    /**
     * Removes sprites on the spritesToRemove list.
     */
    private final void removeOldUpdatingSprites() {
        for (int i = updatingSpritesToRemove.size() - 1; i > -1; i--) {
            updatingSprites.remove(updatingSpritesToRemove.remove(i));
        }
    }

    private final synchronized int getNextUpdatingSpriteIndex() {
        return nextSprite++;
    }
}
