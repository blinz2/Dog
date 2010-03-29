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
package org.blinz.input;

import org.blinz.util.User;

/**
 * A super class for events generated for user input.
 * @author Blinz
 */
public abstract class InputEvent {

    private User user;

    /**
     *
     * @param user the User associated with this InputEvent
     */
    protected InputEvent(User user) {
        this.user = user;
    }

    /**
     * Gets the User that generated this event.
     * @return the User that generated this event.
     */
    public final User getUser() {
        return user;
    }
}
