package tools;

import org.apache.mina.common.*;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.BaseIoSession;

import java.net.SocketAddress;

/**
 * Represents a mock version of an IOSession to use a MapleClient instance
 * without an active connection (faekchar, etc).
 * <p>
 * Most methods return void, or when they return something, null. Therefore,
 * this class is mostly undocumented, due to the fact that each and every
 * function does squat.
 *
 * @version 1.0
 * @since Revision 518
 */
public class MockIOSession extends BaseIoSession {
    @Override
    protected void updateTrafficMask() {
    }

    @Override
    public IoSessionConfig getConfig() {
        return null;
    }

    @Override
    public IoFilterChain getFilterChain() {
        return null;
    }

    @Override
    public IoHandler getHandler() {
        return null;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public IoService getService() {
        return null;
    }

    @Override
    public SocketAddress getServiceAddress() {
        return null;
    }

    @Override
    public IoServiceConfig getServiceConfig() {
        return null;
    }

    @Override
    public TransportType getTransportType() {
        return null;
    }

    @Override
    public CloseFuture close() {
        return null;
    }

    @Override
    protected void close0() {
    }

    @Override
    public WriteFuture write(Object message, SocketAddress remoteAddress) {
        return null;
    }

    /**
     * "Fake writes" a packet to the client, only running the OnSend event of
     * the packet.
     */
    @Override
    public WriteFuture write(Object message) {
        return null;
    }

    @Override
    protected void write0(WriteRequest writeRequest) {
    }
}