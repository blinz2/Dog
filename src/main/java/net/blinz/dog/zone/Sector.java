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

import java.util.Vector;
import net.blinz.core.util.Bounds;

/**
 * An object used to represent a portion of a Zone, allowing the Zone to be divided
 * up for efficient and scalable parallelization and collision detection.
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
     * Constructor
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
    public void init() {
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
        if (!getData().isClient() && !getData().isServer()) {
            for (int i = 0; i < updatingSprites.size(); i++) {
                updatingSprites.get(i).update();
            }
        } else if (getData().isServer()) {
            for (int i = 0; i < updatingSprites.size(); i++) {
                updatingSprites.get(i).update();
                updatingSprites.get(i).serverUpdate();
            }
        } else if (getData().isClient()) {
            for (int i = 0; i < updatingSprites.size(); i++) {
                updatingSprites.get(i).update();
                updatingSprites.get(i).clientUpdate();
            }
        } else {
            for (int i = 0; i < updatingSprites.size(); i++) {
                updatingSprites.get(i).update();
                updatingSprites.get(i).clientUpdate();
                updatingSprites.get(i).serverUpdate();
            }
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
     * Gets the x position of this Sector in the array.
     * @return the x position of this Sector in the array
     */
    final int getXIndex() {
        return bounds.getX() / getData().getSectorSize();
    }

    /**
     * Gets the y position of this Sector in the array.
     * @return the y position of this Sector in the array
     */
    final int getYIndex() {
        return bounds.getY() / getData().getSectorSize();
    }

    /**
     * Gets the x location of this Sector.
     * @return x the x location of this Sector
     */
    final int getX() {
        return bounds.getX();
    }

    /**
     * Gets the y location of this Sector.
     * @return y the y location of this Sector
     */
    final int getY() {
        return bounds.getY();
    }

    /**
     * Gets the width of Sectors in this Zone.
     * @return width of this Sector
     */
    final int getWidth() {
        return getData().getSectorSize();
    }

    /**
     * Gets the height of Sectors in this Zone.
     * @return height of this Sector
     */
    final int getHeight() {
        return getData().getSectorSize();
    }

    /**
     * Adds given sprite to this Sector.
     * @param sprite the sprite to be added to this Sector
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
     */
    final void removeSprite(final BaseSprite sprite) {
        if (sprite instanceof UpdatingSprite) {
            updatingSpritesToRemove.add((UpdatingSprite) sprite);
        }
        if (sprite instanceof CollidableSprite) {
            synchronized (collidibleSprites) {
                collidibleSprites.remove((CollidableSprite) sprite);
            }
        }
        memberSprites.remove(sprite);
        removedSprites.add(sprite);
    }

    /**
     * Runs a the given CollidableSprite against other CollidableSprites in this
     * Sector to find collisions.
     * @param sprite the CollidableSprite for which to check for collisions
     */
    final void checkCollisionsFor(final CollidableSprite sprite) {
        synchronized (collidibleSprites) {
            for (int i = 0; i < collidibleSprites.size(); i++) {
                final BaseSprite s1 = (BaseSprite) sprite, s2 = (BaseSprite) collidibleSprites.get(i);
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
     * @param sprite the UpdatingSprite to be removed
     */
    synchronized final void deleteUpdatingSprite(final UpdatingSprite sprite) {
        if (!updatingSprites.remove(sprite)) {
            updatingSpritesToAdd.remove(sprite);
        }
    }

    /**
     * Indicates whether or not the given coordinates are within the bounds of this Sector.
     * @param x the x coordinate to check for
     * @param y the y coordinate to check for
     * @return true if given point is within the bounds of this Sector, false otherwise
     */
    final boolean contains(final int x, final int y) {
        return bounds.contains(x, y);
    }

    /**
     * Indicates whether or not the specified bounds intersect this Sector.
     * @param bounds a Bounds object representing the bounds to check for
     * @return true if the specified coordinates intersect this Sector
     */
    final boolean intersects(final Bounds bounds) {
        return this.bounds.intersects(bounds);
    }

    /**
     * Indicates whether or not the specified bounds intersect this Sector.
     * @param x the x coordinate of the bounds to check for
     * @param y the y coordinate of the bounds to check for
     * @param width the width of the bounds to check for
     * @param height the height of the bounds to check for
     * @return true if the specified coordinates intersect this Sector
     */
    final boolean intersects(final int x, final int y, final int width, final int height) {
        return bounds.intersects(x, y, width, height);
    }

    /**
     * Finds and assigns the neighbor attributes for this Sector.
     */
    final void findNeighbors() {
        int ix = bounds.getX() / getData().getSectorSize();
        int iy = bounds.getY() / getData().getSectorSize();

        if (ix > 0) {
            leftNeighbor = getData().getSectorOf(bounds.x - 1, bounds.y);
        }
        if (iy > 0) {
            topNeighbor = getData().getSectorOf(bounds.x, bounds.y - 1);
        }
        if (ix < getData().sectors.length - 1) {
            rightNeighbor = getData().getSectorOf(bounds.x + 1, bounds.y);
        }
        if (iy < getData().sectors[ix].length - 1) {
            bottomNeighbor = getData().getSectorOf(bounds.x, bounds.y + 1);
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
            final UpdatingSprite s = updatingSpritesToRemove.remove(i);
            if (!updatingSprites.remove(s)) {
                updatingSpritesToAdd.remove(s);
            }
        }
        for (int i = updatingSpritesToAdd.size() - 1; i > -1; i--) {
            updatingSprites.add(updatingSpritesToAdd.remove(i));
        }
    }
}
