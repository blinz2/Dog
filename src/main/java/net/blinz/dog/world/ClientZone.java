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
