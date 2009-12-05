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
package org.blinz.engine;

import java.util.ArrayList;
import org.blinz.util.Position;
import org.blinz.util.Position3D;
import org.blinz.util.Size;
import java.util.Stack;
import java.util.Vector;
import org.blinz.graphics.Graphics;

/**
 *
 * @author Blinz
 */
final class Scene {

    final Position translation = new Position();
    final Size size = new Size();
    private int spriteCount = 0;
    private Stack<SpriteContainer> containers = new Stack<SpriteContainer>();
    private ArrayList<SpriteContainer>[] layers = new ArrayList[50];
    private boolean isLocked = false;
    private long lastCleanUpTime = System.currentTimeMillis();

    Scene() {
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new ArrayList<SpriteContainer>();
        }
    }

    final void add(CameraSprite sprite) {
        if (!contains(sprite, (int) sprite.getLayer())) {
            layers[(int) sprite.getLayer()].add(fetchContainer(sprite));
            spriteCount++;
        }
    }

    final void addAll(Vector<CameraSprite> sprites) {
        for (CameraSprite sprite : sprites) {
            add(sprite);
        }
    }

    final boolean isLocked() {
        return isLocked;
    }

    /**
     * Returns true if the locking succeeded, false if it didn't.
     * @return boolean
     */
    final synchronized boolean lock() {
        return !setLock(true);
    }

    final void unLock() {
        setLock(false);
    }

    /**
     * Draws this Scene to the screen.
     * @param graphics
     */
    final void draw(Graphics graphics) {
        Position spriteLoc = new Position();
        Size spriteSize = new Size();

        for (ArrayList<SpriteContainer> layer : layers) {
            for (SpriteContainer sprite : layer) {
                spriteLoc.setPosition(sprite.loc.x - translation.x,
                        sprite.loc.y - translation.y);
                spriteSize.setSize(sprite.sprite.getWidth(), sprite.sprite.getHeight());
                sprite.sprite.draw(graphics, spriteLoc, spriteSize);
                containers.add(sprite);
            }
        }

        spriteLoc = null;
        spriteSize = null;
    }

    /**
     * Preserves the SpriteContainers so that excessive creation of them does not
     * exceed the rate at which they can be deleted. SpriteContainers are periodically
     * deleted to keep a memory leak from becoming problematic over a long period
     * of time.
     */
    final void manageContainers() {
        if (lastCleanUpTime < System.currentTimeMillis() - 10000) {
            if (containers.size() >= spriteCount) {
                for (int i = 0; i < spriteCount; i++) {
                    containers.remove(0);
                }
            }
            clear();
            lastCleanUpTime = System.currentTimeMillis();
        } else {
            clear();
        }
    }

    /**
     * Sorts the sprites in the different layers according to the value after
     * the radix in their layer.
     */
    final void sortLayers() {
        for (ArrayList<SpriteContainer> layer : layers) {
            sortLayer(layer, 0, layer.size() - 1);
        }
    }

    /**
     * Clears all sprites from this Scene.
     */
    private final void clear() {
        for (ArrayList list : layers) {
            list.clear();
        }
        spriteCount = 0;
    }

    /**
     * Returns a SpriteContainer containing the given CameraSprite.
     * @param sprite
     * @return SpriteContainer
     */
    private final SpriteContainer fetchContainer(CameraSprite sprite) {
        if (containers.empty()) {
            return new SpriteContainer(sprite);
        } else {
            SpriteContainer sc = containers.pop();
            sc.sprite = sprite;
            sc.loc.setPosition(sprite.getX(), sprite.getY(), sprite.getLayer());
            return sc;
        }
    }

    /**
     * Changes the locked status to that of the given value and returns the old
     * lock status.
     * @param lock
     * @return boolean - old lock status prior to this call
     */
    private final synchronized boolean setLock(boolean lock) {
        boolean retval = isLocked;
        isLocked = lock;
        return retval;
    }

    /**
     * Returns true if this Scene contains the given sprite false otherwise.
     * @param sprite
     * @return returns whether or not this Scene contains the given sprite
     */
    private final boolean contains(CameraSprite sprite, int layer) {
        for (SpriteContainer s : layers[layer]) {
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
    private final void sortLayer(ArrayList<SpriteContainer> layer, int low, int high) {
        if (low >= high) {
            return;
        }

        final SpriteContainer pivot = layer.get(high);
        int pivotIndex = high;
        for (int i = low; i <= pivotIndex;) {
            if (layer.get(i).layer() > pivot.layer()) {
                final SpriteContainer current = layer.get(i);
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
     * Holds important status information about the sprite it contains at the time
     * of the generation of a Scene.
     */
    private class SpriteContainer {

        CameraSprite sprite;
        final Position3D loc = new Position3D();

        SpriteContainer(CameraSprite sprite) {
            this.sprite = sprite;
            loc.setPosition(sprite.getX(), sprite.getY(), sprite.getLayer());
        }

        /**
         *
         * @return the layer of the sprite this SpriteContainer represents.
         */
        float layer() {
            return loc.z;
        }
    }
}
