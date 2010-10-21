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
package net.blinz.dog.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import net.blinz.core.util.Position;

/**
 * A local Zone to represent the perspective of the user from their position on
 * the server.
 * @author Blinz
 */
class ClientZone extends Zone {

    private final Position location = new Position();
    private final Socket socket = new Socket();
    private BufferedReader netIn;

    /**
     * Constructor
     * @param address a SocketAddress representing the server and port of Zone to connect to
     * @throws IOException if unable to connect to the server
     */
    ClientZone(final SocketAddress address) throws IOException {
        socket.connect(address);
        netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    protected void init() {
    }

    @Override
    protected void update() {
    }
}
