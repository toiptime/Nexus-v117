package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.handler.AutoRegister;
import handling.world.CharacterTransfer;
import handling.world.MapleMessengerCharacter;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Timer.EventTimer;
import tools.CPUSampler;
import tools.HexTool;
import tools.Logger;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class warpcashshop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            MapleClient client = chr.getClient();
            final ChannelServer ch = ChannelServer.getInstance(client.getChannel());

            chr.changeRemoval();

            if (chr.getMessenger() != null) {
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }
            PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
            PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
            ch.removePlayer(chr);
            client.updateLoginState(MapleClient.CHANGE_CHANNEL, client.getSessionIPAddress());
            chr.saveToDB(false, false);
            chr.getMap().removePlayer(chr);
            client.getSession().write(CField.getChannelChange(client, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
            client.setPlayer(null);
            client.setReceiving(false);
            return 1;
        }
    }

    public static class TestDirection extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(CField.UIPacket.getDirectionInfo(StringUtil.joinStringFrom(splitted, 5), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4])));
            return 1;
        }
    }

    public static class Packet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(HexTool.getByteArrayFromHexString(StringUtil.joinStringFrom(splitted, 1)));
            return 1;
        }
    }

    public static class stripall extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            ChannelServer cs = c.getChannelServer();
            for (MapleCharacter mchr : cs.getPlayerStorage().getAllCharacters()) {
                if (c.getPlayer().isGM()) {
                    continue;
                }
                MapleInventory equipped = mchr.getInventory(MapleInventoryType.EQUIPPED);
                MapleInventory equip = mchr.getInventory(MapleInventoryType.EQUIP);
                List<Short> ids = new ArrayList<>();
                for (Item item : equipped.newList()) {
                    ids.add(item.getPosition());
                }
                for (short id : ids) {
                    MapleInventoryManipulator.unequip(mchr.getClient(), id, equip.getNextFreeSlot());
                }
            }
            return 1;
        }
    }

    public static class Strip extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            MapleInventory equipped = victim.getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = victim.getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new ArrayList<Short>();
            for (Item item : equipped.newList()) {
                ids.add(item.getPosition());
            }
            for (short id : ids) {
                MapleInventoryManipulator.unequip(victim.getClient(), id, equip.getNextFreeSlot());
            }
            boolean notice = false;
            if (splitted.length > 1) {
                notice = true;
            }
            if (notice) {
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, victim.getName() + " has been stripped by " + c.getPlayer().getName()));
            }
            return 1;
        }
    }

    public static class mesosall extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted[1]), true);
                }
            }
            return 1;
        }
    }

    public static class ServerNotice extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //c.getChannelServer().broadcastMessage(CWvsContext.serverNotice(Integer.parseInt(splitted[1]), StringUtil.joinStringFrom(splitted, 2)));
            for (MapleCharacter all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.print(Integer.parseInt(splitted[1]), StringUtil.joinStringFrom(splitted, 2));
            }
            return 1;
        }
    }

    public static class hottime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().print(0, "[Syntax] !hottime [itemid]");
                return 0;
            }
            if (!MapleItemInformationProvider.getInstance().itemExists(Integer.parseInt(splitted[1]))) {
                c.getPlayer().print(0, "Item does not exists.");
                return 0;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainItem(Integer.parseInt(splitted[1]), 1);
                    mch.getClient().getSession().write(CField.NPCPacket.getNPCTalk(9010010, (byte) 0, "#bThanks for joining the Hot Time#k.\r\nYou have received #g#t" + Integer.parseInt(splitted[1]) + "##k.\r\n#rGo ahead and check your inventory now#k.", "00 00", (byte) 1, 9010010));
                }
            }
            Logger.println("Hot Time had been scheduled successfully.");
            return 1;
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() != c.getPlayer().getMapId()) {
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                }
            }
            return 1;
        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
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
                ts = EventTimer.getInstance().register(new Runnable() {
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
                String filename = "logs/odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().print(6, "The entered filename already exists, choose a different one.");
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

    public static class Sql extends CommandExecute {

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
}