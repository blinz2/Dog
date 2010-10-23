/*
 * Dog - A project for making highly scalable non-clustered game and simulation environments.
 * Copyright (C) 2010 BlinzProject <gtalent2@gmail.com>
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
package net.blinz.dog.graphics;

import net.blinz.core.graphics.Canvas;
import net.blinz.core.graphics.Display;
import net.blinz.core.graphics.Graphics;

/**
 * An implementation of the BlinzCore's Canvas class with predefined and auto-adjusting
 * screen sizes.
 * @author Blinz
 */
public class Screen extends Canvas {

    /**
     * Used to indicate the size and location of a Screen.
     */
    public enum ScreenType {

        FULL_SCREEN,
        LEFT_SCREEN,
        RIGHT_SCREEN,
        TOP_SCREEN,
        BOTTOM_SCREEN,
        TOP_LEFT_SCREEN,
        TOP_RIGHT_SCREEN,
        BOTTOM_LEFT_SCREEN,
        BOTTOM_RIGHT_SCREEN;
    }

    private ScreenType screenType = ScreenType.FULL_SCREEN;

    @Override
    protected void draw(final Graphics g) {
        adjustBounds();
    }

    /**
     * Sets the screen type out of a variety of types such as top left or bottom right.
     * @param screenType the form the Screen will take
     */
    public final void setScreenType(final ScreenType screenType) {
        this.screenType = screenType;
    }

    /**
     * Adjusts the bounds of this Screen according to its screen type.
     */
    protected final void adjustBounds() {
        switch (screenType) {
            case FULL_SCREEN:
                setBounds(0, 0, Display.getPaneWidth(), Display.getPaneHeight());
                break;
            case TOP_SCREEN:
                setBounds(0, Display.getPaneHeight() / 2,
                        Display.getPaneWidth(), Display.getPaneHeight() / 2);
                break;
            case BOTTOM_SCREEN:
                setBounds(0, 0, Display.getPaneWidth(), Display.getPaneHeight() / 2);
                break;
            case LEFT_SCREEN:
                setBounds(0, 0, Display.getPaneWidth() / 2, Display.getPaneHeight());
                break;
            case TOP_LEFT_SCREEN:
                setBounds(0, Display.getPaneHeight() / 2,
                        Display.getPaneWidth() / 2, Display.getPaneHeight() / 2);
                break;
            case TOP_RIGHT_SCREEN:
                setBounds(Display.getPaneWidth() / 2, Display.getPaneHeight() / 2,
                        Display.getPaneWidth() / 2, Display.getPaneHeight() / 2);
                break;
            case BOTTOM_LEFT_SCREEN:
                setBounds(0, 0, Display.getPaneWidth() / 2, Display.getPaneHeight() / 2);
                break;
            case BOTTOM_RIGHT_SCREEN:
                setBounds(Display.getPaneWidth() / 2, 0,
                        Display.getPaneWidth() / 2, Display.getPaneHeight() / 2);
                break;
        }
    }
}
