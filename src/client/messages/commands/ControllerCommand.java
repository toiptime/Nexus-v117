package client.messages.commands;

import client.MapleClient;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.world.World;
import server.Timer;
import tools.CPUSampler;
import tools.HexTool;
import tools.StringUtil;
import tools.packet.CWvsContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Itzik
 */
public class ControllerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.SUDO;
    }

    public static class Packet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                c.getSession().write(HexTool.getByteArrayFromHexString(StringUtil.joinStringFrom(splitted, 1)));
            } else {
                c.getPlayer().print(0, "Please enter packet data");
            }
            return 1;
        }
    }

    public static class ControlData extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                Connection con = (Connection) DatabaseConnection.getConnection();
                PreparedStatement ps = (PreparedStatement) con.prepareStatement(StringUtil.joinStringFrom(splitted, 1));
                ps.executeUpdate();
            } catch (SQLException e) {
                c.getPlayer().print(0, "Failed to execute SQL command.");
                return 0;
            }
            return 1;
        }
    }

    public static class Shutdown extends CommandExecute {

        protected static Thread t = null;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Shutting down...");
            if (t == null || !t.isAlive()) {
                t = new Thread(server.ShutdownServer.getInstance());
                server.ShutdownServer.getInstance().shutdown();
                t.start();
            } else {
                c.getPlayer().print(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }

    public static class ShutdownTime extends Shutdown {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().print(6, "Shutting down... in " + minutesLeft + " minutes");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(server.ShutdownServer.getInstance());
                ts = Timer.EventTimer.getInstance().register(new Runnable() {

                    public void run() {
                        if (minutesLeft == 0) {
                            server.ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "Project Nexus will shutdown in " + minutesLeft + " minutes. Please log off safely."));
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().print(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants"); // Or should we do Packages.constants etc.?
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return 1;
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().print(6, "The entered filename already exists, choose a different one");
                    return 0;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return 1;
        }
    }
}