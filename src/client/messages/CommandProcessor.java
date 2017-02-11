/*
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.messages;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.*;
import constants.ServerConstants.CommandType;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import tools.FileoutputUtil;
import tools.Logger;

import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CommandProcessor {

    private final static HashMap<String, CommandObject> commands = new HashMap<String, CommandObject>();
    private final static HashMap<Integer, ArrayList<String>> commandList = new HashMap<Integer, ArrayList<String>>();

    static {

        Class<?>[] CommandFiles = {
                PlayerCommand.class, DonatorCommand.class, InternCommand.class, GMCommand.class, AdminCommand.class, SuperGMCommand.class
        };

        for (Class<?> clasz : CommandFiles) {
            try {
                PlayerGMRank rankNeeded = (PlayerGMRank) clasz.getMethod("getPlayerLevelRequired", new Class<?>[]{}).invoke(null, (Object[]) null);
                Class<?>[] a = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<>();
                for (Class<?> c : a) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true; // Enable all coded commands by default.
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject((CommandExecute) o, rankNeeded.getLevel()));
                                if (rankNeeded.getCommandPrefix() != PlayerGMRank.GM.getCommandPrefix() && rankNeeded.getCommandPrefix() != PlayerGMRank.NORMAL.getCommandPrefix()) { //add it again for GM
                                    commands.put("!" + c.getSimpleName().toLowerCase(), new CommandObject((CommandExecute) o, PlayerGMRank.GM.getLevel()));
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(rankNeeded.getLevel(), cL);
            } catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            }
        }
    }

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().print(6, msg);
                break;
            case TRADE:
                c.getPlayer().print(-2, "Error : " + msg);
                break;
        }

    }

    public static void dropHelp(MapleClient c) {
        final StringBuilder sb = new StringBuilder("Command List: ");
        for (int i = 0; i <= c.getPlayer().getGMLevel(); i++) {
            if (commandList.containsKey(i)) {
                for (String s : commandList.get(i)) {
                    sb.append(s);
                    sb.append(" ");
                }
            }
        }
        c.getPlayer().print(6, sb.toString());
    }

    public static boolean processCommand(MapleClient c, String line, CommandType type) {
        Logger.println("Executing command %s as character %s", line, c.getPlayer().getName());
        if (line.contains(PlayerGMRank.NORMAL.getCommandPrefix() + "" + PlayerGMRank.NORMAL.getCommandPrefix()) || line.contains(PlayerGMRank.DONATOR.getCommandPrefix() + "" + PlayerGMRank.DONATOR.getCommandPrefix()) || line.contains(PlayerGMRank.INTERN.getCommandPrefix() + "" + PlayerGMRank.INTERN.getCommandPrefix()) || line.contains(PlayerGMRank.GM.getCommandPrefix() + "" + PlayerGMRank.GM.getCommandPrefix()) || line.contains(PlayerGMRank.SUPERGM.getCommandPrefix() + "" + PlayerGMRank.SUPERGM.getCommandPrefix()) || line.contains(PlayerGMRank.ADMIN.getCommandPrefix() + "" + PlayerGMRank.ADMIN.getCommandPrefix())) {
            //players often like to do @@@@@ so why wouldn't i let them?
            return false;
        }
        if (line.charAt(0) == PlayerGMRank.NORMAL.getCommandPrefix() || (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel() && line.charAt(0) == PlayerGMRank.DONATOR.getCommandPrefix())) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject co = commands.get(splitted[0]);
            if (co == null || co.getType() != type) {
                sendDisplayMessage(c, "That player command does not exist.", type);
                return true;
            }
            try {
                int ret = co.execute(c, splitted);
                if (ret == 0 && c.getPlayer().getId() == 1)//Failure
                    Logger.println("Failed to execute command");
                else if (ret > 0 && c.getPlayer().getId() == 1)
                    Logger.println("Command executed successfully");
            } catch (Exception e) {
                sendDisplayMessage(c, "There was an error.", type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, "Error: " + e, type);
                    FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                }
            }
            return true;
        }

        if (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel()) {
            if (line.charAt(0) == '`' && c.getPlayer().getGMLevel() > 2) {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.broadcastGMMessage(tools.packet.CField.multiChat("[Game Master Chat] " + c.getPlayer().getName(), line.substring(1), 6));
                }
                return true;
            }
            if (line.charAt(0) == PlayerGMRank.DONATOR.getCommandPrefix() || line.charAt(0) == PlayerGMRank.INTERN.getCommandPrefix() || line.charAt(0) == PlayerGMRank.GM.getCommandPrefix() || line.charAt(0) == PlayerGMRank.SUPERGM.getCommandPrefix() || line.charAt(0) == PlayerGMRank.SUDO.getCommandPrefix()) { // Redundant for now, but in case we change symbols later. This will become extensible.
                String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();

                CommandObject co = commands.get(splitted[0]);
                if (co == null) {
                    if (splitted[0].equals(line.charAt(0) + "help")) {
                        dropHelp(c);
                        return true;
                    }
                    sendDisplayMessage(c, "That command does not exist.", type);
                    return true;
                }
                if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                    int ret = 0;
                    try {
                        ret = co.execute(c, splitted);
                    } catch (ArrayIndexOutOfBoundsException x) {
                        sendDisplayMessage(c, "The command was not used properly: " + x, type);
                    } catch (Exception e) {
                        FileoutputUtil.outputFileError(FileoutputUtil.CommandEx_Log, e);
                    }
                    if (ret > 0 && c.getPlayer() != null) { // Incase d/c after command or something
                        if (c.getPlayer().getId() == 1)//GLaDOS
                            Logger.println("Command executed successfully");
                        else if (c.getPlayer().isGM())
                            logCommandToDB(c.getPlayer(), line, "gmlog");
                        else
                            logCommandToDB(c.getPlayer(), line, "internlog");
                    }
                } else {
                    sendDisplayMessage(c, "You do not have the privileges to use that command.", type);
                }
                return true;
            }
        }
        return false;
    }

    private static void logCommandToDB(MapleCharacter player, String command, String table) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO " + table + " (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, command);
            ps.setInt(3, player.getMap().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
            ex.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
            }
        }
    }
}