/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2010  Blinz <gtalent2@gmail.com>
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

import org.blinz.util.concurrency.SynchronizedTask;

/**
 * An interface for adding objects to Zones to be updated once each cycle with
 * the Zone.
 * @author Blinz
 */
public interface UpdatingObject {

    public void update();
}

/**
 * The SynchronizedTask for handling UpdatingObjects.
 * @author Blinz
 */
final class Updater extends SynchronizedTask {

    private UpdatingObject updatingObject;

    /**
     * Constructor
     * @param updatinObject the UpdatingObject that this will update.
     */
    Updater(UpdatingObject updatinObject) {
        this.updatingObject = updatinObject;
    }

    @Override
    protected void run() {
        updatingObject.update();
    }

    /**
     * Gets the UpdatingObject associated with this Updater.
     * @return the UpdatingObject associated with this Updater
     */
    final UpdatingObject getUpdatingObject() {
        return updatingObject;
    }
}
