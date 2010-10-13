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
package net.blinz.dog.input;

import net.blinz.dog.util.User;

/**
 * Used to describe an event from a key to listener objects.
 * @author Blinz
 */
public class KeyEvent extends InputEvent {

    private int key;

    /**
     * Constructor
     * @param user user that generated this event
     * @param key key associated with this event
     */
    public KeyEvent(final User user, final int key) {
        super(user);
        this.key = key;
    }

    /**
     * Gets the key associated with this event.
     * @return the key associated with this event
     */
    public final int key() {
        return key;
    }
}
