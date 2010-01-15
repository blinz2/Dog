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

import java.util.Hashtable;
import java.util.Vector;
import org.blinz.util.User;
import org.blinz.util.concurrency.SynchronizedTask;

/**
 * Contains lists sprites listening for input from specific Users.
 * @author Blinz
 */
class UserListenerCatalog extends SynchronizedTask {

    /**
     * Contains a list of sprites that listen to the input of a certain User.
     * @author Blinz
     */
    private class UserListenerList {

        private final Vector<BaseSprite> sprites = new Vector<BaseSprite>();
        /**
         * Count of how many Cameras accessing this UserListenerList.
         */
        private int cameraCount;

        /**
         *
         * @return true if this list has no Cameras or sprites, false otherwise.
         */
        private final boolean dead() {
            return cameraCount == 0 && sprites.isEmpty();
        }

        private final void add(BaseSprite sprite) {
            sprites.add(sprite);
        }
    }

    /**
     * Internal class of objects used for clean addition and removal of User:sprite pairs.
     */
    private class Pair {

        User user;
        BaseSprite sprite;

        private Pair(User user, BaseSprite sprite) {
            this.user = user;
            this.sprite = sprite;
        }
    }
    private final Hashtable<User, UserListenerList> userListeners = new Hashtable<User, UserListenerList>();
    private final Vector<Pair> toRemove = new Vector<Pair>();
    private final Vector<Pair> toAdd = new Vector<Pair>();

    @Override
    protected void run() {
        //remove old pairs
        Pair current = null;
        while ((current = nextToRemove()) != null) {
            UserListenerList list = userListeners.get(current.user);
            list.sprites.remove(current.sprite);
            if (list.dead()) {
                userListeners.remove(current.user);
            }
        }

        //block until finished

        //add new pairs
        while ((current = nextToRemove()) != null) {
            UserListenerList list = userListeners.get(current.user);
            list.sprites.add(current.sprite);

        }
    }

    /**
     * Adds the given sprite to the list for the given User.
     * @param user
     * @param sprite
     */
    final void add(User user, BaseSprite sprite) {
        toAdd.add(new Pair(user, sprite));
    }

    /**
     * Adds the given sprite to the list for the given User.
     * @param user
     * @param sprite
     */
    final void remove(User user, BaseSprite sprite) {
        toRemove.add(new Pair(user, sprite));
    }

    /**
     * Increments the usage counter for the list associated with the User of the
     * given Camera.
     * @param camera
     */
    final void incrementUsageCount(Camera camera) {
        editList((byte) 2, camera.getUser(), null);
    }

    /**
     * Decrements the usage counter for the list associated with the User of the
     * given Camera.
     * @param camera
     */
    final void decrementUsageCount(Camera camera) {
        editList((byte) 3, camera.getUser(), null);
    }

    /**
     * @return the next sprite to remove
     */
    private final Pair nextToRemove() {
        if (toRemove.isEmpty()) {
            return null;
        }
        return toRemove.remove(0);
    }

    /**
     * @return the next sprite to add
     */
    private final Pair nextToAdd() {
        if (toAdd.isEmpty()) {
            return null;
        }
        return toAdd.remove(0);
    }

    /**
     * Adds a sprite list for the given User.
     * @param user
     */
    private final void addList(User user) {
        synchronized (userListeners) {
            if (!userListeners.contains(user)) {
                userListeners.put(user, new UserListenerList());
            }
        }
    }

    /**
     * Removes the sprite list for the given User.
     * @param user
     */
    private final void removeList(User user) {
        synchronized (userListeners) {
            userListeners.remove(user);
        }
    }

    /**
     * 0: add list for User
     * 1: remove list for User
     * 2: increment list
     * 3: decrement list
     * 4: add sprite
     * 5: remove sprite
     * @param operation
     * @param user
     */
    private final synchronized void editList(byte operation, User user, BaseSprite sprite) {
        switch (operation) {
            case 0://add list for User
                if (!userListeners.contains(user)) {
                    userListeners.put(user, new UserListenerList());
                }
                break;
            case 1://remove list for User
                if (userListeners.get(user).dead()) {
                    userListeners.remove(user);
                }
                break;
            case 2://increment list
                userListeners.get(user).cameraCount++;
                break;
            case 3://decrement list
                userListeners.get(user).cameraCount--;
                break;
            case 4://add sprite
                userListeners.get(user).sprites.add(sprite);
                break;
            case 5://remove sprites
                UserListenerList l = userListeners.get(user);
                l.sprites.remove(sprite);
                if (l.dead()) {
                    userListeners.remove(user);
                }
        }
    }
}
