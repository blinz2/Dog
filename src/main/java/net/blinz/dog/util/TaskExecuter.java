/*
 *  BlinzCore - core library of audio, video, and other essential classes.
 *  Copyright (C) 2009  BlinzProject <gtalent2@gmail.com>
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
 * A ParallelProcess that takes Tasks to execute in manners defined by the classes
 * the extend.
 * @author Blinz
 */
public final class TaskExecuter extends ParallelProcess {

    private final TaskList list = new TaskList();
    private final Barrier barrier = new Barrier();

    /**
     * Constructor
     * @param threads number of threads used for this TaskExecuter
     */
    public TaskExecuter(final int threads) {
        this("TaskExecuter", threads);
    }

    /**
     *
     * @param name the name of the thread group used for this TaskExecuter
     * @param threads number of threads used for this TaskExecuter
     */
    public TaskExecuter(final String name, final int threads) {
        super(name, threads);
        list.init(this);
        barrier.init(this);
    }

    /**
     * Adds the given Task to this TaskExecuter to be executed in the future.
     * @param task
     */
    public final void addTask(final Task task) {
        list.add(task);
    }

    /**
     * Removes the given Task and it will no longer be executed in the future.
     * @param task
     */
    public final void removeTask(final Task task) {
        list.remove(task);
    }

    /**
     * Gets the Task at the given place in the order of execution.
     * @param index the location of the desired Task in the order of execution
     * @return the Task at the given place in the order of execution
     */
    public final Task get(final int index) {
        return list.get(index);
    }

    /**
     * Removes and gets the Task at the given place in the order of execution.
     * @param index the location of the desired Task in the order of execution
     * @return the Task at the given place in the order of execution
     */
    public final Task remove(final int index) {
        return list.remove(index);
    }

    @Override
    protected final void update() {
        synchronized (this) {
            if (!list.prepared()) {
                list.prepare();
            }
        }
        list.enter();
    }
}
