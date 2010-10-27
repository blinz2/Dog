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

import net.blinz.core.graphics.Graphics;
import net.blinz.dog.zone.Camera;

/**
 * A Screen class dedicated to drawing a Cameras.
 * @author Blinz
 */
public final class CameraScreen extends Screen {

    private Camera camera = new Camera();

    /**
     * Sets the Camera that this CameraScreen will draw.
     * @param camera the Camera for this CameraScreen to draw.
     */
    public final void setCamera(final Camera camera) {
        this.camera = camera;
    }

    /**
     * Gets the Camera that this draws.
     * @return the Camera that this draws
     */
    public final Camera getCamera() {
        return camera;
    }

    @Override
    protected void draw(final Graphics g) {
        super.draw(g);
        final Camera c = camera;
        if (c != null) {
            c.setSize(getWidth(), getHeight());
            c.draw(g);
        }
    }
}
