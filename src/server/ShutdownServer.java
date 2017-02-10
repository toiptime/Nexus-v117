package server;

import constants.ServerConstants;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import server.Timer.*;
import tools.Logger;
import tools.packet.CWvsContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;

public class ShutdownServer implements ShutdownServerMBean {

    public static ShutdownServer instance;

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ShutdownServer();
            mBeanServer.registerMBean(instance, new ObjectName("server:type=ShutdownServer"));
        } catch (Exception e) {
            Logger.println("Error registering Shutdown MBean");
            e.printStackTrace();
        }
    }

    public static ShutdownServer getInstance() {
        return instance;
    }

    public int mode = 0;

    public void shutdown() {// Can execute twice
        run();
    }

    @Override
    public void run() {
        if (mode == 0) {
            int ret = 0;
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "Project Nexus World is going to Shutdown soon. Please kindly log off now for the meantime."));
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.setShutdown();
                cs.setServerMessage("Project Nexus World is going to Shutdown soon. Please kindly log off now for the meantime.");
                ret += cs.closeAllMerchant();
            }
            /*AtomicInteger FinishedThreads = new AtomicInteger(0);
            HiredMerchantSave.Execute(this);
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShutdownServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            while (FinishedThreads.incrementAndGet() != HiredMerchantSave.NumSavingThreads) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ShutdownServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }*/
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
            Logger.println("Shutdown phase one complete. Hired Merchants saved: " + ret);
            mode++;
        } else if (mode == 1) {
            mode++;
            Logger.println("Second Shutdown commencing...");
            try {
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "Project Nexus World is going to Shutdown now. Please kindly log off now for the meantime."));
                Integer[] chs =  ChannelServer.getAllInstance().toArray(new Integer[0]);

                for (int i : chs) {
                    try {
                        ChannelServer cs = ChannelServer.getInstance(i);
                        synchronized (this) {
                            cs.shutdown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                LoginServer.shutdown();
                CashShopServer.shutdown();
                DatabaseConnection.closeAll();
            } catch (SQLException e) {
                System.err.println("THROW" + e);
            }
            WorldTimer.getInstance().stop();
            MapTimer.getInstance().stop();
            BuffTimer.getInstance().stop();
            CloneTimer.getInstance().stop();
            EventTimer.getInstance().stop();
            EtcTimer.getInstance().stop();
            PingTimer.getInstance().stop();

            Logger.println("Shutdown phase two complete");
            try{
                Thread.sleep(5000);
            }catch(Exception e) {
                //shutdown
            }
            if (ServerConstants.getOS().equals(ServerConstants.OperatingSystems.UNIX)) {
                try {
                    Runtime.getRuntime().exec("killall -9 java");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.exit(0); // Not sure if this is really needed for ChannelServer
        }
    }
}