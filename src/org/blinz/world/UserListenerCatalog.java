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
import org.blinz.input.ClickEvent;
import org.blinz.input.KeyEvent;
import org.blinz.input.KeyListener;
import org.blinz.input.MouseEvent;
import org.blinz.input.MouseListener;
import org.blinz.input.MouseWheelEvent;
import org.blinz.input.MouseWheelListener;
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
     class UserListenerList implements MouseListener, MouseWheelListener, KeyListener {

        private final Vector<BaseSprite> sprites = new Vector<BaseSprite>();
        private User user = null;
        /**
         * Count of how many Cameras accessing this UserListenerList.
         */
        private int cameraCount;

        @Override
        public void buttonClick(int buttonNumber, int clickCount, int cursorX, int cursorY) {
            ClickEvent e = new ClickEvent(user, buttonNumber, cursorX, cursorY, clickCount);

            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).buttonClicked(e);
            }
        }

        @Override
        public void buttonPress(int buttonNumber, int cursorX, int cursorY) {
            MouseEvent e = new MouseEvent(user, buttonNumber, cursorX, cursorY);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).buttonPressed(e);
            }
        }

        @Override
        public void buttonRelease(int buttonNumber, int cursorX, int cursorY) {
            MouseEvent e = new MouseEvent(user, buttonNumber, cursorX, cursorY);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).buttonReleased(e);
            }
        }

        @Override
        public void keyPressed(int key) {
            KeyEvent e = new KeyEvent(user, key);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).keyPressed(e);
            }
        }

        @Override
        public void keyReleased(int key) {
            KeyEvent e = new KeyEvent(user, key);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).keyReleased(e);
            }
        }

        @Override
        public void keyTyped(int key) {
            KeyEvent e = new KeyEvent(user, key);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).keyTyped(e);
            }
        }

        @Override
        public void wheelScroll(int number, int cursorX, int cursorY) {
            MouseWheelEvent e = new MouseWheelEvent(user, cursorX, cursorY, number);
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).mouseWheelScroll(e);
            }
        }

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

        private final void remove(BaseSprite sprite) {
            sprites.remove(sprite);
        }
    }

    /**
     * Internal class of objects used for clean addition and removal of User:sprite pairs.
     */
    private final class Pair {

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
            list.remove(current.sprite);
            if (list.dead()) {
                userListeners.remove(current.user);
            }
        }

        //add new pairs
        while ((current = nextToRemove()) != null) {
            UserListenerList list = userListeners.get(current.user);
            list.add(current.sprite);

        }
    }

    /**
     * 
     * @param user
     * @return the list of sprites listening to the given User
     */
    final UserListenerList get(User user) {
        return userListeners.get(user);
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

//    /**
//     * Increments the usage counter for the list associated with the User of the
//     * given Camera.
//     * @param camera
//     */
//    final void incrementUsageCount(Camera camera) {
//        editList((byte) 2, camera.getUser(), null);
//    }
//
//    /**
//     * Decrements the usage counter for the list associated with the User of the
//     * given Camera.
//     * @param camera
//     */
//    final void decrementUsageCount(Camera camera) {
//        editList((byte) 3, camera.getUser(), null);
//    }
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
}
