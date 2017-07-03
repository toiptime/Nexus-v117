
package handling.login;

import client.MapleClient;
import handling.channel.ChannelServer;
import server.Timer.PingTimer;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;

import java.util.Map;
import java.util.Map.Entry;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm() && !c.isLocalhost()) {
            c.getSession().write(CWvsContext.serverNotice(1, "Project Nexus is currently on maintenanance. Therefore, only administrator are able to login.\r\nProject Nexus Maintenance :\r\nRevision : 0.0.1\r\nMaintenance From : DATE\r\nMaintenance To : DATE\r\n\r\nWhat's New For Revision 0.0.1?\r\nNew Rolling Feature\r\nText 1\r\nText 2\r\nText 3"));
            c.getSession().write(LoginPacket.getLoginFailed(7));
            return;
        }

        if (System.currentTimeMillis() - lastUpdate > 600000) { // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) { // In an unfortunate event that client logged in before load
                lastUpdate = 0;
                c.getSession().write(LoginPacket.getLoginFailed(7));
                return;
            }
            final double loadFactor = 1200 / ((double) LoginServer.getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }

        if (c.finishLogin() == 0) {
            c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
            //start temp fix

            //CharLoginHandler.ServerStatusRequest(c);
            //c.setWorld(0);
            //c.setChannel(Randomizer.nextInt(ChannelServer.getAllInstances().size()) + 1);
            //c.getSession().write(LoginPacket.getCharList(c.getSecondPassword() != null, c.loadCharacters(0), c.getCharacterSlots()));

            //end temp fix

            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {

                public void run() {
                    c.getSession().close();
                }
            }, 10 * 60 * 10000));
        } else {
            c.getSession().write(LoginPacket.getLoginFailed(7));
            return;
        }
    }
}