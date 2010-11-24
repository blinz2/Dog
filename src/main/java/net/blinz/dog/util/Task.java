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

/**
 * A component of a process to be executed concurrently.
 * @author Blinz
 */
public abstract class Task {

    boolean moveOn = false;
    TaskExecuter taskProcessor;
    private boolean prepared = false;

    /**
     * Tells the TaskExecuter that this is the last round.
     */
    public final void stop() {
        taskProcessor.stop();
    }

    /**
     * Sets the move on flag for this Task telling all threads to skip over for
     * the rest of the duration of this loop.
     */
    final void setMoveOn() {
        this.moveOn = true;
        this.prepared = false;
    }

    /**
     * Initializes this Task for the TaskProcessor
     * @param threadCount
     * @return true if initialization succeeded, false otherwise.
     */
    synchronized boolean init(final TaskExecuter taskProcessor) {
        if (this.taskProcessor == null) {
            this.taskProcessor = taskProcessor;
            return true;
        }
        return false;
    }

    /**
     * Dissassociates this Task with its current TaskExecuter.
     */
    void drop() {
        taskProcessor = null;
    }

    /**
     * Indicates whether or not a thread should process this Task or move on.
     * @return if true the TaskProcessor should move on, otherwise continue on this Task
     */
    boolean moveOn() {
        return moveOn;
    }

    final void enter() {
        run();
        setMoveOn();
    }

    /**
     * Prepare the Task for execution.
     */
    void prepare() {
        moveOn = false;
        prepared = true;
    }

    /**
     *
     * @return true if this Task has already been prepared for its next execution, false otherwise
     */
    final boolean prepared() {
        return prepared;
    }

    protected abstract void run();
}
