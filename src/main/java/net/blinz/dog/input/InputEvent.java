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
package net.blinz.dog.input;

import net.blinz.dog.util.User;

/**
 * A super class for events generated for user input.
 * @author Blinz
 */
public abstract class InputEvent {

    private User user;

    /**
     * Constructor
     * @param user the User associated with this InputEvent
     */
    protected InputEvent(final User user) {
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
