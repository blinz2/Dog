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
 * An InputEvent generated with a mouse operation.
 * @author Blinz
 */
public class MouseEvent extends InputEvent {

    private int buttonID;
    private int x, y;

    /**
     *
     * @param user the User associated with this InputEvent
     * @param buttonID the button operated
     * @param cursorX the x coordinate of the cursor at the time of the event
     * @param cursorY the y coordinate of the cursor at the time of the event
     */
    public MouseEvent(final User user, final int buttonID, final int cursorX, final int cursorY) {
        super(user);
        x = cursorX;
        y = cursorY;
        this.buttonID = buttonID;
    }

    /**
     * Gets the numerical identification of the mouse button.
     * @return the button associated with this event.
     */
    public final int getButton() {
        return buttonID;
    }

    /**
     * Gets the x coordinate of the cursor at the time of this event
     * @return the x coordinate of the cursor at the time of this event
     */
    public final int cursorX() {
        return x;
    }

    /**
     * Gets the y coordinate of the cursor at the time of this event.
     * @return the y coordinate of the cursor at the time of this event
     */
    public final int cursorY() {
        return y;
    }
}
