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

import java.util.ArrayList;
import org.blinz.util.Position;
import org.blinz.util.Position3D;
import org.blinz.util.Size;
import java.util.Stack;
import java.util.Vector;
import org.blinz.graphics.Graphics;
import org.blinz.util.Bounds;

/**
 * An object used to represent what is in a Cameras sight at a given cycle in the
 * execution of a Zone.
 * @author Blinz
 */
final class Scene {

    final Position translation = new Position();
    final Size size = new Size();
    private int spriteCount = 0;
    private final Stack<SceneSprite> containers = new Stack<SceneSprite>();
    private final UnorderedList<SceneSprite>[] layers = new UnorderedList[50];
    private boolean isLocked = false;
    private long lastCleanUpTime = System.currentTimeMillis();

    /**
     * Constructor
     */
    Scene() {
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new UnorderedList<SceneSprite>();
        }
    }

    /**
     * Adds the given CameraSprite to this Scene.
     * @param sprite the CameraSprite to be added to this Scene
     */
    final void add(final CameraSprite sprite) {
        if (!contains(sprite, (int) sprite.getLayer())) {
            layers[(int) sprite.getLayer()].add(fetchSceneSprite(sprite));
            spriteCount++;
        }
    }

    /**
     * Adds the CameraSprites in the given list of CameraSprites to this Scene.
     * @param sprites the list of CameraSprites to be added to this Scene
     */
    final void addAll(final Vector<CameraSprite> sprites) {
        for (final CameraSprite sprite : sprites) {
            add(sprite);
        }
    }

    /**
     * Indicates whether or not this Scene is currently locked.
     * @return true if the locking succeeded, false if it didn't
     */
    final boolean isLocked() {
        return isLocked;
    }

    /**
     * Attempts to lock this Scene.
     * @return true if the locking succeeded, false if it didn't
     */
    final synchronized boolean lock() {
        return !setLock(true);
    }

    /**
     * Unlocks this Scene.
     */
    final void unLock() {
        setLock(false);
    }

    /**
     * Draws this Scene to the screen.
     * @param graphics
     */
    final void draw(final Graphics graphics) {
        final Bounds bounds = new Bounds();

        for (final UnorderedList<SceneSprite> layer : layers) {
            for (int i = 0; i < layer.size(); i++) {
		final SceneSprite sprite = layer.get(i);
                bounds.setPosition(sprite.loc.x - translation.x,
                        sprite.loc.y - translation.y);
                bounds.setSize(sprite.sprite.getWidth(), sprite.sprite.getHeight());
                sprite.sprite.draw(graphics, bounds);
                containers.add(sprite);
            }
        }
    }

    /**
     * Preserves the SceneSprites so that excessive creation of them does not
     * exceed the rate at which they can be deleted. SceneSprites are periodically
     * deleted to keep a memory leak from becoming problematic over a long period
     * of time.
     */
    final void manageSceneSprites() {
        if (lastCleanUpTime < System.currentTimeMillis() - 10000) {
            if (containers.size() >= spriteCount) {
                for (int i = 0; i < spriteCount; i++) {
                    containers.remove(0);
                }
            }
            clear();
            lastCleanUpTime = System.currentTimeMillis();
 	    trimLists();
 	    } else {
            clear();
        }
    }

    /**
     * Sorts the sprites in the different layers according to the value after
     * the radix in their layer.
     */
    final void sortLayers() {
        for (int i = 0; i < layers.length; i++) {
            sortLayer(layers[i], 0, layers[i].size() - 1);
        }
    }

    /**
     * Clears all sprites from this Scene.
     */
    private final void clear() {
        for (final UnorderedList list : layers) {
            list.clear();
        }
        spriteCount = 0;
    }

    /**
     * Gets a SceneSprite containing the given CameraSprite.
     * @param sprite the Sprite for which a Container will be returned
     * @return a available SceneSprite representing the given CameraSprite
     */
    private final SceneSprite fetchSceneSprite(final CameraSprite sprite) {
        if (containers.empty()) {
            return new SceneSprite(sprite);
        } else {
            final SceneSprite sc = containers.pop();
            sc.sprite = sprite;
            sc.loc.setPosition(sprite.getX(), sprite.getY(), (int) sprite.getLayer());
            return sc;
        }
    }

    /**
     * Changes the locked status to that of the given value and returns the old
     * lock status.
     * @param lock the lock status of this Scene
     * @return boolean old lock status prior to this call
     */
    private final synchronized boolean setLock(final boolean lock) {
        boolean retval = isLocked;
        isLocked = lock;
        return retval;
    }

    /**
     * Indicates whether or not the given CameraSprite is in this Scene.
     * @param sprite the CameraSprite to check for
     * @return returns whether or not this Scene contains the given sprite
     */
    private final boolean contains(final CameraSprite sprite, final int layer) {
        for (int i = 0; i < layers[layer].size(); i++) {
            final SceneSprite s = layers[layer].get(i);
	    if (sprite == s.sprite) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts the given layer's sprites according the sub-layer data.
     * @param low the point on the list where the sorting will begin
     * @param high the point on the list where the sorting will end
     */
    private final void sortLayer(final UnorderedList<SceneSprite> layer, final int low, final int high) {
        if (low >= high) {
            return;
        }

        final SceneSprite pivot = layer.get(high);
        int pivotIndex = high;
        for (int i = low; i <= pivotIndex;) {
            if (layer.get(i).layer() > pivot.layer()) {
                final SceneSprite current = layer.get(i);
                layer.set(pivotIndex, current);
                layer.set(i, layer.get(pivotIndex - 1));
                layer.set(pivotIndex - 1, pivot);
                pivotIndex--;
            } else {
                i++;
            }
        }

        sortLayer(layer, low, pivotIndex - 1);
        sortLayer(layer, pivotIndex + 1, high);
    }
   
    /**
     * Trims the size of all lists down to their current size to recover memory.
     */
    private final void trimLists() {
	for (int i = 0; i < layers.length; i++) {
		layers[i].trimToSize();
  	}
    }

    /**
     * Holds important status information about the sprite it contains at the time
     * of the generation of a Scene.
     */
    private final class SceneSprite {

        private CameraSprite sprite;
        private final Position3D loc = new Position3D();

        /**
         * Constructor
         * @param sprite the Camera Sprite that this will represent
         */
        SceneSprite(final CameraSprite sprite) {
            this.sprite = sprite;
            loc.setPosition(sprite.getX(), sprite.getY(), (int) sprite.getLayer());
        }

        /**
         * Gets layer of the sprite in this SpriteContainer.
         * @return the layer of the sprite this SpriteContainer represents.
         */
        final float layer() {
            return loc.z;
        }
    }
}
