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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.blinz.input.ClickEvent;
import org.blinz.input.KeyEvent;
import org.blinz.input.MouseEvent;
import org.blinz.input.MouseWheelEvent;
import org.blinz.util.User;

/**
 * Contains lists sprites listening for input from specific Users.
 * @author Blinz
 */
final class UserListenerCatalog {

    /**
     * Contains a list of sprites that listen to the input of a certain User.
     * @author Blinz
     */
    final class UserListenerList {

        /**
         * Determines when the lists need to be trimmed.
         */
        private long timeOfLastClean = System.currentTimeMillis();
        /**
         * Used for when the Zone is paused.
         */
        private final Vector<BaseSprite> dummyList = new Vector<BaseSprite>();
        /**
         * The primary sprite list.
         */
        private final Vector<BaseSprite> sprites = new Vector<BaseSprite>();
        private Vector<BaseSprite> inputSprites;
        private final Vector<ClickEvent> buttonClicks = new Vector<ClickEvent>();
        private final Vector<MouseEvent> buttonPresses = new Vector<MouseEvent>();
        private final Vector<MouseEvent> buttonReleases = new Vector<MouseEvent>();
        private final Vector<KeyEvent> keyPresses = new Vector<KeyEvent>();
        private final Vector<KeyEvent> keyReleases = new Vector<KeyEvent>();
        private final Vector<KeyEvent> keyTypes = new Vector<KeyEvent>();
        private final Vector<MouseWheelEvent> wheelScrolls = new Vector<MouseWheelEvent>();
        /**
         * Count of how many Cameras accessing this UserListenerList.
         */
        private int cameraCount = 0;

        public final void buttonClick(final ClickEvent e) {
            buttonClicks.add(e);
        }

        public final void buttonPress(final MouseEvent e) {
            buttonPresses.add(e);
        }

        public final void buttonRelease(final MouseEvent e) {
            buttonReleases.add(e);
        }

        public final void keyPressed(final KeyEvent e) {
            keyPresses.add(e);
        }

        public final void keyReleased(final KeyEvent e) {
            keyReleases.add(e);
        }

        public final void keyTyped(final KeyEvent e) {
            keyTypes.add(e);
        }

        public final void wheelScroll(final MouseWheelEvent e) {
            wheelScrolls.add(e);
        }

        final void trimLists() {
            sprites.trimToSize();
            buttonClicks.trimToSize();
            buttonPresses.trimToSize();
            buttonReleases.trimToSize();
            inputSprites.trimToSize();
            keyPresses.trimToSize();
            keyReleases.trimToSize();
            keyTypes.trimToSize();
            wheelScrolls.trimToSize();
        }

        private final void init() {
            inputSprites = paused ? dummyList : sprites;
        }

        /**
         * Suspend reception of user input to the sprites.
         */
        private final void pause() {
            inputSprites = dummyList;
        }

        /**
         * Resume reception of user input to the sprites.
         */
        private final void unpause() {
            inputSprites = sprites;
        }

        /**
         *
         * @return true if this list has no Cameras or sprites, false otherwise.
         */
        private final boolean dead() {
            return cameraCount == 0 && inputSprites.isEmpty();
        }

        /**
         * Adds the given sprite to this list.
         * For use by UserListenerCatalog.
         * @param sprite
         */
        private final void add(final BaseSprite sprite) {
            inputSprites.add(sprite);
        }

        /**
         * Removes the given sprite from this list.
         * For use by UserListenerCatalog.
         * @param sprite
         */
        private final void remove(final BaseSprite sprite) {
            inputSprites.remove(sprite);
        }

        /**
         * Updates this UserListenerCatalog, telling all sprites about relevant input.
         */
        private final void update() {

            for (int n = 0; n < buttonClicks.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).buttonClicked(buttonClicks.get(i));
                }
            }
            for (int n = 0; n < buttonPresses.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).buttonPressed(buttonPresses.get(i));
                }
            }
            for (int n = 0; n < buttonReleases.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).buttonReleased(buttonReleases.get(i));
                }
            }
            for (int n = 0; n < keyPresses.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).keyPressed(keyPresses.get(i));
                }
            }
            for (int n = 0; n < keyReleases.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).keyReleased(keyReleases.get(i));
                }
            }
            for (int n = 0; n < keyTypes.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).keyTyped(keyTypes.get(i));
                }
            }
            for (int n = 0; n < wheelScrolls.size(); n++) {
                for (int i = 0; i < inputSprites.size(); i++) {
                    inputSprites.get(i).mouseWheelScroll(wheelScrolls.get(i));
                }
            }
        }
    }

    /**
     * Internal class of objects used for clean addition and removal of User:sprite pairs.
     */
    private final class Pair {

        private User user;
        private BaseSprite sprite;

        private Pair(User user, BaseSprite sprite) {
            this.user = user;
            this.sprite = sprite;
        }
    }
    private boolean paused = false;
    private final Hashtable<User, UserListenerList> userListeners = new Hashtable<User, UserListenerList>();
    private final Vector<Pair> toRemove = new Vector<Pair>();
    private final Vector<Pair> toAdd = new Vector<Pair>();

    protected final void update() {
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
        while ((current = nextToAdd()) != null) {
            UserListenerList list;
            do {
                list = userListeners.get(current.user);
                if (list == null) {
                    list = fetchList(current.user);
                }
                list.add(current.sprite);
            } while (!userListeners.contains(list));
        }

        final Enumeration<UserListenerList> list = userListeners.elements();
        while (list.hasMoreElements()) {
            list.nextElement().update();
        }
    }

    /**
     * Trims the lists under this object and its subordinates down to size.
     */
    final void trimLists() {
        final Enumeration<UserListenerList> list = userListeners.elements();
        while (list.hasMoreElements()) {
            list.nextElement().trimLists();
        }
    }

    /**
     * 
     * @param user
     * @return the list of sprites listening to the given User
     */
    final UserListenerList get(final User user) {
        return userListeners.get(user);
    }

    /**
     * Checks out the list associated with the given User.
     * checkIn should be called when the list is no longer needed.
     *
     * @param user the User who's list is to be returned
     * @return the sprite list associated with the given User
     */
    final UserListenerList checkOut(final User user) {
        UserListenerList list;
        do {
            list = get(user);
            if (list == null) {
                list = fetchList(user);
            }
            list.cameraCount++;
        } while (!userListeners.containsKey(user));
        return list;
    }

    /**
     * Used by Zone to pause sprites recieving of user input while the Zone is paused.
     */
    final void pause() {
        paused = true;
        Enumeration<UserListenerList> list = userListeners.elements();
        while (list.hasMoreElements()) {
            list.nextElement().pause();
        }
    }

    /**
     * Used by Zone to unpause sprites recieving of user input while the Zone is paused.
     */
    final void unpause() {
        paused = false;
        Enumeration<UserListenerList> list = userListeners.elements();
        while (list.hasMoreElements()) {
            list.nextElement().unpause();
        }
    }

    /**
     * Checks in UserListenerList for the given User.
     * @param user
     */
    final void checkIn(final User user) {
        UserListenerList list = userListeners.get(user);
        list.cameraCount--;
        if (list.dead()) {
            userListeners.remove(user);
        }
    }

    /**
     * Adds the given sprite to the list for the given User.
     * @param user
     * @param sprite
     */
    final void add(final User user, final BaseSprite sprite) {
        toAdd.add(new Pair(user, sprite));
    }

    /**
     * Adds the given sprite to the list for the given User.
     * @param user
     * @param sprite
     */
    final void remove(final User user, final BaseSprite sprite) {
        toRemove.add(new Pair(user, sprite));
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
     * Adds a sprite list for the given User if it does not exist, returns the existing
     * one if it does.
     * @param user
     */
    private final synchronized UserListenerList fetchList(final User user) {
        if (!userListeners.contains(user)) {
            UserListenerList list = new UserListenerList();
            userListeners.put(user, list);
            list.init();
            return list;
        } else {
            return userListeners.get(user);
        }
    }
}
