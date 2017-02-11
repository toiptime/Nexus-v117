package tools;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static final int PORT = 626;
    private static IoAcceptor acceptor;

    static {
        startListeningServer();
    }

    /**
     * @param m    Message to send
     * @param args Format options
     */
    public static void print(Object m, Object... args) {
        String x = String.format(m.toString(), args);
        System.out.print(x);
    }

    public static void println(Object m, Object... args) {
        println(Error.INFO, m.toString(), args);
    }

    public static void println(Error e, Object m, Object... args) {
        m = String.format(m.toString(), args);

        if (e == Error.FATAL)
            logErrorToFile(m.toString());

        System.out.println(m);
    }

    private static void logErrorToFile(String m) {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");

        String path = "logs/" + dateFormat.format(today) + ".txt";

        try {
            FileWriter fw = new FileWriter(path, true);
            fw.write(timeFormat.format(today) + " : " + m + "\n");
            fw.close();
        } catch (Exception e) {
            println("Error logging to file: " + e);
        }
    }

    public static void startListeningServer(){
        acceptor = new SocketAcceptor();
        final SocketAcceptorConfig acceptor_config = new SocketAcceptorConfig();
        acceptor_config.getSessionConfig().setTcpNoDelay(true);
        acceptor_config.setDisconnectOnUnbind(true);
        acceptor_config.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        //Connection collection here

        try {
            acceptor.bind(new InetSocketAddress(PORT), new MapleServerHandler(), acceptor_config);
            println("Log Server online");
        } catch (IOException e) {
            print("Binding to logging server port %s failed :: %s", PORT, e.getMessage());
        }
    }

    public static void broadcastPacket(){
    }
}