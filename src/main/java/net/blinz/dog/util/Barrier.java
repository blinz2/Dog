/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009  Blinz <gtalent2@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
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
package net.blinz.dog.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A barrier Task to halt execuetion until all threads have reached this Task.
 * @author Blinz
 */
public final class Barrier extends Task {

    CyclicBarrier barrier;

    @Override
    boolean init(TaskExecuter process) {
        if (super.init(process)) {
            barrier = new CyclicBarrier(process.getThreadCount());
            return true;
        }
        return false;
    }

    @Override
    void drop() {
        barrier = null;
    }

    @Override
    protected void run() {
        try {
            barrier.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Barrier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(Barrier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
