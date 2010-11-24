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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains an update method that is executed by however many threads there are
 * processors on the system.
 *
 * If you don't know how many processors your system has run
 * "Runtime.getRuntime().availableProcessors();".
 *
 * @author Blinz
 */
public abstract class ParallelProcess {

    private ThreadRun[] threads;
    private boolean isRunning = true;
    private ThreadGroup group;

    /**
     * Constructor
     * @param threadName the name of the threads that will work in this ParallelProcess
     * @param threadCount the number of threads that will work in this ParallelProcess
     */
    public ParallelProcess(final String threadName, final int threadCount) {
        group = new ThreadGroup(threadName);
        threads = new ThreadRun[threadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ThreadRun(group);
        }
    }

    /**
     * Constructor
     * @param threadCount the number of threads that will work in this ParallelProcess
     */
    public ParallelProcess(final int threadCount) {
        this("Task", threadCount);
    }

    /**
     * Gets the number of threads dedicated to processing this TaskProcessor.
     * @return the number of threads dedicated to processing this TaskProcessor
     */
    public final int getThreadCount() {
        return threads.length;
    }

    /**
     * Starts this Task.
     */
    public final void start() {
        isRunning = true;
        for (final ThreadRun t : threads) {
            t.start();
        }
    }

    /**
     * Tells the threads to stop updating after the current cycle.
     */
    public final void stop() {
        isRunning = false;
    }

    /**
     * Indicates whether or not this ParallelProcess is currently running.
     * @return true if this ParallelProcess is running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Similar to Thread.join(), waits for all threads in this ParallelProcess to die.
     */
    public final void join() {
        for (final ThreadRun t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ParallelProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected abstract void update();

    private class ThreadRun extends Thread {

        ThreadRun(final ThreadGroup group) {
            super(group, group.getName());
        }

        @Override
        public void run() {
            while (isRunning) {
                update();
            }
        }
    }
}
