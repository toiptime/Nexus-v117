package client.messages.commands;

import client.*;
import client.anticheat.CheatingOffense;
import client.inventory.*;
import client.messages.CommandProcessorUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.*;
import server.Timer;
import server.Timer.*;
import server.life.*;
import server.maps.*;
import server.quest.MapleQuest;
import tools.HexTool;
import tools.MockIOSession;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;

import java.awt.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class SuperGMCommand {

    public static String chatcolor;

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.SUPERGM;
    }

    public static class MapChangeTimer extends CommandExecute {

        @Override
        public int execute(final MapleClient c, String splitted[]) {
            final int map = Integer.parseInt(splitted[1]);
            final int nextmap = Integer.parseInt(splitted[2]);
            final int time = Integer.parseInt(splitted[3]);
            c.getChannelServer().getMapFactory().getMap(map).broadcastMessage(CField.getClock(time));
            c.getChannelServer().getMapFactory().getMap(map).startMapEffect("You will be moved out of the map when the timer ends.", 5120041);
            EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (MapleCharacter mch : c.getChannelServer().getMapFactory().getMap(map).getCharacters()) {
                        if (mch == null) {
                            return;
                        } else {
                            mch.changeMap(nextmap, 0);
                        }
                    }
                }
            }, time * 1000); // Seconds
            return 1;
        }
    }

    //Exists in Intern
    /*public static class Mute extends CommandExecute {

     @Override
     public int execute(MapleClient c, String splitted[]) {
     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
     victim.canTalk(false);

     victim.print(6, c.getPlayer().getName().toString() + " has muted you");
     c.getPlayer().print(5, victim.getName().toString() + " has been muted");
     return 1;
     }
     }

     public static class UnMute extends CommandExecute {

     @Override
     public int execute(MapleClient c, String splitted[]) {
     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
     victim.canTalk(true);

     victim.print(6, c.getPlayer().getName().toString() + " has unmuted you");
     c.getPlayer().print(5, victim.getName().toString() + " has been unmuted");
     return 1;
     }
     }

     public static class MuteMap extends CommandExecute {

     @Override
     public int execute(MapleClient c, String splitted[]) {
     for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
     chr.canTalk(false);
     }
     c.getPlayer().print(6, "The map has been muted");
     return 1;
     }
     }

     public static class UnMuteMap extends CommandExecute {

     @Override
     public int execute(MapleClient c, String splitted[]) {
     for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
     chr.canTalk(true);
     }
     c.getPlayer().print(6, "The map has been unmuted");
     return 1;
     }
     }
     */
    public static class setname extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] !setname [current] [new]");
                return 0;
            }
            if (victim == null) {
                c.getPlayer().print(0, "Could not find the player.");
                return 0;
            }
            if (c.getPlayer().getGMLevel() < 5 && !victim.isGM()) {
                c.getPlayer().print(6, "Only an administrator can change player's name.");
                return 0;
            }
            victim.setName(splitted[2]);
            victim.getClient().getSession().close();
            victim.getClient().disconnect(true, false);
            return 1;
        }
    }

    public static class Popup extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (splitted.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StringUtil.joinStringFrom(splitted, 1));
                    mch.print(1, sb.toString());
                } else {
                    c.getPlayer().print(6, "[Syntax] !popup [message]");
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class saveall extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                mch.saveToDB(false, false);
            }
            c.getPlayer().print(6, "All characters data has been saved!");
            return 1;
        }
    }

    public static class SaveAndroids extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                mch.getAndroid().saveToDb();
                mch.print(0, "All androids data has been saved!");
            }
            return 1;
        }
    }

    public static class HellB extends HellBan {
    }

    public static class HellBan extends Ban {

        public HellBan() {
            hellban = true;
        }
    }

    public static class UnHellB extends UnHellBan {
    }

    public static class UnHellBan extends UnBan {

        public UnHellBan() {
            hellban = true;
        }
    }

    public static class UnB extends UnBan {
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            if (hellban) {
                return "UnHellBan";
            } else {
                return "UnBan";
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] !" + getCommand() + " [name]");
                return 0;
            }
            byte ret;
            if (hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            } else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().print(6, "[" + getCommand() + "] SQL error.");
                return 0;
            } else if (ret == -1) {
                c.getPlayer().print(6, "[" + getCommand() + "] The character does not exist.");
                return 0;
            } else {
                c.getPlayer().print(6, "[" + getCommand() + "] Successfully unbanned!");

            }
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().print(6, "[Unban IP] SQL error.");
            } else if (ret_ == -1) {
                c.getPlayer().print(6, "[Unban IP] The character does not exist.");
            } else if (ret_ == 0) {
                c.getPlayer().print(6, "[Unban IP] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().print(6, "[Unban IP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret_ == 2) {
                c.getPlayer().print(6, "[Unban IP] Both IP and Macs were unbanned.");
            }
            return ret_ > 0 ? 1 : 0;
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] !unbanip [name]");
                return 0;
            }
            byte ret = MapleClient.unbanIPMacs(splitted[1]);
            if (ret == -2) {
                c.getPlayer().print(6, "[Unban IP] SQL error.");
            } else if (ret == -1) {
                c.getPlayer().print(6, "[Unban IP] The character does not exist.");
            } else if (ret == 0) {
                c.getPlayer().print(6, "[Unban IP] No IP or Mac with that character exists!");
            } else if (ret == 1) {
                c.getPlayer().print(6, "[Unban IP] IP / Mac -- one of them was found and unbanned.");
            } else if (ret == 2) {
                c.getPlayer().print(6, "[Unban IP] Both IP and Macs were unbanned.");
            }
            if (ret > 0) {
                return 1;
            }
            return 0;
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            victim.changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Item, MapleInventoryType> eqs = new HashMap<Item, MapleInventoryType>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("all")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("equipped")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).newList()) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("equip")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("use")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("setup")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("etc")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("cash")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                c.getPlayer().print(6, "[Syntax] !unlockinv [all / equipped / equip / use / setup / etc / cash]");
            }

            for (Entry<Item, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return 1;
        }
    }

    public static class TempBanIP extends TempBan {

        public TempBanIP() {
            ipBan = true;
        }
    }

    public static class BanIP extends Ban {

        public BanIP() {
            ipBan = true;
        }
    }

    public static class TempBan extends CommandExecute {

        protected boolean ipBan = false;
        private String[] types = {"Advertise", "Spam", "Troll", "Scam", "Disrespect", "Flame", "Misuse", "Racist", "Hack"};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) {
                c.getPlayer().print(6, "[Syntax] !tempban [name] [reasons] [hours]");
                StringBuilder s = new StringBuilder("Temporarily Ban Reasons: ");
                for (int i = 0; i < types.length; i++) {
                    s.append(i + 1).append(" - ").append(types[i]).append(", ");
                }
                c.getPlayer().print(6, s.toString());
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int reason = Integer.parseInt(splitted[2]);
            final int numHour = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, numHour);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null || reason < 0 || reason >= types.length) {
                c.getPlayer().print(6, "Unable to find character or reason was not valid, use the command !tempban to see reasons");
                return 0;
            }
            victim.tempban("Temporarily banned by " + c.getPlayer().getName() + " for " + types[reason] + " reason", cal, reason, ipBan);
            c.getPlayer().print(6, "The character " + splitted[1] + " has been successfully temporarily banned till " + df.format(cal.getTime()));
            return 1;
        }
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false, ipBan = false;

        private String getCommand() {
            if (hellban) {
                return "HellBan";
            } else {
                return "Ban";
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(5, "[Syntax] !" + getCommand() + " [name] [reason]");
                return 0;
            }
            if (splitted.length < 5) {
                c.getPlayer().print(5, "Please, enter the full ban reason otherwise the banned person will be auto un-banned.");
                return 0;
            }
            StringBuilder sb = new StringBuilder();
            if (hellban) {
                sb/*.append("Banned ").append(splitted[1]).append(": ")*/.append(StringUtil.joinStringFrom(splitted, 2));
            } else {
                sb/*.append(c.getPlayer().getName()).append(" banned ").append(splitted[1]).append(": ")*/.append(StringUtil.joinStringFrom(splitted, 2));
            }
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) {
                    //sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), hellban || ipBan, false, hellban)) {
                        c.getPlayer().print(6, "[" + getCommand() + "] Successfully banned " + splitted[1] + ".");
                        return 1;
                    } else {
                        c.getPlayer().print(6, "[" + getCommand() + "] Failed to ban.");
                        return 0;
                    }
                } else {
                    c.getPlayer().print(6, "[" + getCommand() + "] May not ban Game Master...");
                    return 1;
                }
            } else {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), hellban)) {
                    c.getPlayer().print(6, "[" + getCommand() + "] Successfully offline banned " + splitted[1] + ".");
                    return 1;
                } else {
                    c.getPlayer().print(6, "[" + getCommand() + "] Failed to ban " + splitted[1]);
                    return 0;
                }
            }
        }
    }

    public static class Spy extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted[1].equalsIgnoreCase("qwerty")) {
                NPCScriptManager.getInstance().start(c, 1033110, null);
            }
            return 1;
        }
    }

    public static class Drop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().print(5, itemId + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return 1;
        }
    }

    public static class Marry extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "Need [name] [itemid]");
                return 0;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().print(6, "Invalid Item ID.");
            } else {
                MapleCharacter fff = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (fff == null) {
                    c.getPlayer().print(6, "Player must be online");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId, ringID[i]);
                            if (eq == null) {
                                c.getPlayer().print(6, "Invalid Item ID.");
                                return 0;
                            }
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].print(6, "Successfully married with " + chrz[i == 0 ? 1 : 0].getName());
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 1;
        }
    }

    public static class Vac extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isHidden()) {
                c.getPlayer().print(6, "You can only vac monsters while in hide mode.");
                return 0;
            } else {
                for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                    final MapleMonster monster = (MapleMonster) mmo;
                    c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, -1, 0, monster.getObjectId(), monster.getTruePosition(), c.getPlayer().getLastRes()));
                    monster.setPosition(c.getPlayer().getPosition());
                }
            }
            return 1;
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
    }

    public static class speakchannel extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return 1;
        }
    }

    public static class Monitor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().print(5, "Not monitoring " + target.getName() + " anymore.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().print(5, "Monitoring " + target.getName() + ".");
                }
            } else {
                c.getPlayer().print(5, "Target not found on the current channel.");
                return 0;
            }
            return 1;
        }
    }

    public static class ResetOther extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forfeit(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]));
            return 1;
        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length > 4 ? splitted[4] : null);
            return 1;
        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    c.getPlayer().print(6, i + ": " + tstring);
                }
            }
            return 1;
        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                throw new IllegalArgumentException();
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().print(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().print(6, elem.toString());
            }
            return 1;
        }
    }

    public static class offense extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().print(6, "Offense " + splitted[1] + " not found");
            }
            return 1;
        }
    }

    public static class togglemega extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().print(6, "Megaphone state : " + (c.getChannelServer().getMegaphoneMuteState() ? "Enabled" : "Disabled"));
            return 1;
        }
    }

    public static class SReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(splitted[1])), Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            c.getPlayer().getMap().spawnReactorOnGroundBelow(reactor, new Point(c.getPlayer().getTruePosition().x, c.getPlayer().getTruePosition().y - 20));
            return 1;
        }
    }

    public static class ClearSquads extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final Collection<MapleSquad> squadz = new ArrayList<MapleSquad>(c.getChannelServer().getAllSquads().values());
            for (MapleSquad squads : squadz) {
                squads.clear();
            }
            return 1;
        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class HitAll extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().print(6, "Map does not exist");
                return 0;
            }
            int damage = Integer.parseInt(splitted[1]);
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                mob.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class HitMonster extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            int damage = Integer.parseInt(splitted[1]);
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[2])) {
                    map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                    mob.damage(c.getPlayer(), damage, false);
                }
            }
            return 1;
        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
    }

    public static class KillAllDrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                //&& !splitted[0].equals("!killmonster") && !splitted[0].equals("!hitmonster") && !splitted[0].equals("!hitmonsterbyoid") && !splitted[0].equals("!killmonsterbyoid")) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().print(6, "Map does not exist");
                return 0;
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class KillAllExp extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                //&& !splitted[0].equals("!killmonster") && !splitted[0].equals("!hitmonster") && !splitted[0].equals("!hitmonsterbyoid") && !splitted[0].equals("!killmonsterbyoid")) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().print(6, "Map does not exist");
                return 0;
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                mob.damage(c.getPlayer(), mob.getHp(), false);
            }
            return 1;
        }
    }

    public static class NPC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().print(6, "You have entered an invalid NPC ID");
                return 0;
            }
            return 1;
        }
    }

    public static class PNPC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().print(6, "!pnpc [npcid]");
                return 0;
            }
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                try {
                    Connection con = (Connection) DatabaseConnection.getConnection();
                    try (PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().print(6, "Failed to save NPC to the database");
                }
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
                c.getPlayer().print(6, "Please do not reload this map or else the NPC will disappear till the next restart.");
            } else {
                c.getPlayer().print(6, "You have entered an invalid Npc-Id");
                return 0;
            }
            return 1;
        }
    }

    public static class PMOB extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(6, "!pmob [mobid] [mobTime]");
                return 0;
            }
            int mobid = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            MapleMonster npc;
            try {
                npc = MapleLifeFactory.getMonster(mobid);
            } catch (RuntimeException e) {
                c.getPlayer().print(5, "Error: " + e.getMessage());
                return 0;
            }
            if (npc != null) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                try {
                    Connection con = (Connection) DatabaseConnection.getConnection();
                    try (PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid, mobtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, mobid);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "m");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.setInt(12, mobTime);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().print(6, "Failed to save NPC to the database");
                }
                c.getPlayer().getMap().addMonsterSpawn(npc, mobTime, (byte) -1, null);
                c.getPlayer().print(6, "Please do not reload this map or else the MOB will disappear till the next restart.");
            } else {
                c.getPlayer().print(6, "You have entered an invalid Mob ID");
                return 0;
            }
            return 1;
        }
    }

    public static class PlayerNpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //try {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "!playernpc [IGN] [NPC ID]");
                return 0;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().print(6, victim.getName() + " does not exist");
                return 0;
            }
            c.getPlayer().print(6, "Making Player NPC...");
            MapleClient cs = new MapleClient(null, null, new MockIOSession());
            MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
            PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
            npc.addToServer();
            c.getPlayer().print(6, "Done");
            /*  } catch (Exception e) {
             c.getPlayer().print(6, "NPC failed... : " + e.getMessage());
             e.printStackTrace();
             }*/
            return 1;
        }
    }

    //DestroyPlayerNpc is not working properly
 /*   public static class DestroyPlayerNPC extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     try {
     c.getPlayer().print(6, "Destroying playerNPC...");
     final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
     if (npc instanceof PlayerNPC) {
     ((PlayerNPC) npc).destroy(true);
     c.getPlayer().print(6, "Done");
     } else {
     c.getPlayer().print(6, "!destroypnpc [objectid]");
     }
     } catch (Exception e) {
     c.getPlayer().print(6, "NPC failed... : " + e.getMessage());
     e.printStackTrace();
     }
     return 1;
     }
     }*/
    public static class ServerMessage extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.setServerMessage(outputMessage);
            }
            return 1;
        }
    }

    //Exists in InternCommands
    /*public static class Spawn extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     final int mid = Integer.parseInt(splitted[1]);
     final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
     Integer level = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lvl");
     Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
     Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
     Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
     Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

     MapleMonster onemob;
     try {
     onemob = MapleLifeFactory.getMonster(mid);
     } catch (RuntimeException e) {
     c.getPlayer().print(5, "Error: " + e.getMessage());
     return 0;
     }
     if (onemob == null) {
     c.getPlayer().print(5, "Mob does not exist");
     return 0;
     }
     long newhp = 0;
     int newexp = 0;
     if (hp != null) {
     newhp = hp.longValue();
     } else if (php != null) {
     newhp = (long) (onemob.getMobMaxHp() * (php.doubleValue() / 100));
     } else {
     newhp = onemob.getMobMaxHp();
     }
     if (exp != null) {
     newexp = exp.intValue();
     } else if (pexp != null) {
     newexp = (int) (onemob.getMobExp() * (pexp.doubleValue() / 100));
     } else {
     newexp = onemob.getMobExp();
     }
     if (newhp < 1) {
     newhp = 1;
     }

     final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
     for (int i = 0; i < num; i++) {
     MapleMonster mob = MapleLifeFactory.getMonster(mid);
     mob.setHp(newhp);
     if (level != null) {
     mob.changeLevel(level.intValue(), false);
     } else {
     mob.setOverrideStats(overrideStats);
     }
     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
     }
     return 1;
     }
     }*/
    public static class PS extends CommandExecute {

        protected static StringBuilder builder = new StringBuilder();

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (builder.length() > 1) {
                c.getSession().write(CField.getPacketFromHexString(builder.toString()));
                builder = new StringBuilder();
            } else {
                c.getPlayer().print(6, "Please enter packet data!");
            }
            return 1;
        }
    }

    public static class APS extends PS {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                builder.append(StringUtil.joinStringFrom(splitted, 1));
                c.getPlayer().print(6, "String is now: " + builder.toString());
            } else {
                c.getPlayer().print(6, "Please enter packet data!");
            }
            return 1;
        }
    }

    public static class CPS extends PS {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            builder = new StringBuilder();
            return 1;
        }
    }

    public static class P extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                c.getSession().write(CField.getPacketFromHexString(StringUtil.joinStringFrom(splitted, 1)));
            } else {
                c.getPlayer().print(6, "Please enter packet data!");
            }
            return 1;
        }
    }

    public static class Packet extends P {
    }

    public static class PTS extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                try {
                    c.getSession().getHandler().messageReceived(c.getSession(), CField.getPacketFromHexString(StringUtil.joinStringFrom(splitted, 1)));
                } catch (Exception e) {
                    c.getPlayer().print(6, "Error: " + e);
                }
            } else {
                c.getPlayer().print(6, "Please enter packet data!");
            }
            return 1;
        }
    }
    /*MapleMap oldMap = c.getPlayer().getMap();
     MapleMap newMap = c.getChannelServer().getMapFactory().getMap(player.getMapId(), true, true, true, true, true);
     for (MapleCharacter ch : oldMap.getCharacters()) {
     ch.changeMap(newMap);
     }
     oldMap = null;
     c.getPlayer().getMap().respawn();*/

    /**
     *
     */
    public static class ReloadMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap oldmap = c.getPlayer().getMap();
            MapleMap newmap = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId(), true, true, true);
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                chr.changeMap(newmap);
            }
            oldmap = null;
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
    }
    /*  public static class ReloadMap extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     final int mapId = Integer.parseInt(splitted[1]);
     for (ChannelServer cserv : ChannelServer.getAllInstances()) {
     if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
     c.getPlayer().print(5, "There exists characters on channel " + cserv.getChannel());
     return 0;
     }
     }
     for (ChannelServer cserv : ChannelServer.getAllInstances()) {
     if (cserv.getMapFactory().isMapLoaded(mapId)) {
     cserv.getMapFactory().removeMap(mapId);
     }
     }
     return 1;
     }
     }*/

    public static class Respawn extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
    }

    public abstract static class TestTimer extends CommandExecute {

        protected Timer toTest = null;

        @Override
        public int execute(final MapleClient c, String[] splitted) {
            final int sec = Integer.parseInt(splitted[1]);
            c.getPlayer().print(5, "Message will pop up in " + sec + " seconds.");
            c.getPlayer().print(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Maximum: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
            final long oldMillis = System.currentTimeMillis();
            toTest.schedule(new Runnable() {
                public void run() {
                    c.getPlayer().print(5, "Message has popped up in " + ((System.currentTimeMillis() - oldMillis) / 1000) + " seconds, expected was " + sec + " seconds");
                    c.getPlayer().print(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Maximum: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
                }
            }, sec * 1000);
            return 1;
        }
    }

    public static class TestEventTimer extends TestTimer {

        public TestEventTimer() {
            toTest = EventTimer.getInstance();
        }
    }

    public static class TestCloneTimer extends TestTimer {

        public TestCloneTimer() {
            toTest = CloneTimer.getInstance();
        }
    }

    public static class TestEtcTimer extends TestTimer {

        public TestEtcTimer() {
            toTest = EtcTimer.getInstance();
        }
    }

    public static class TestMapTimer extends TestTimer {

        public TestMapTimer() {
            toTest = MapTimer.getInstance();
        }
    }

    public static class TestWorldTimer extends TestTimer {

        public TestWorldTimer() {
            toTest = WorldTimer.getInstance();
        }
    }

    public static class TestBuffTimer extends TestTimer {

        public TestBuffTimer() {
            toTest = BuffTimer.getInstance();
        }
    }

    public static class Crash extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().getSession().write(HexTool.getByteArrayFromHexString("1A 00")); // give_buff with no data :D
                return 1;
            } else {
                c.getPlayer().print(6, "The victim does not exist.");
                return 0;
            }
        }
    }

    public static class ReloadIPMonitor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleServerHandler.reloadLoggedIPs();
            return 1;
        }
    }

    public static class AddIPMonitor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleServerHandler.addIP(splitted[1]);
            return 1;
        }
    }

    public static class FillBook extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (int e : MapleItemInformationProvider.getInstance().getMonsterBook().keySet()) {
                c.getPlayer().getMonsterBook().getCards().put(e, 2);
            }
            c.getPlayer().getMonsterBook().changed();
            c.getPlayer().print(5, "Done.");
            return 1;
        }
    }

    public static class ListBook extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final List<Entry<Integer, Integer>> mbList = new ArrayList<Entry<Integer, Integer>>(MapleItemInformationProvider.getInstance().getMonsterBook().entrySet());
            Collections.sort(mbList, new BookComparator());
            final int page = Integer.parseInt(splitted[1]);
            for (int e = (page * 8); e < Math.min(mbList.size(), (page + 1) * 8); e++) {
                c.getPlayer().print(6, e + ": " + mbList.get(e).getKey() + " - " + mbList.get(e).getValue());
            }

            return 0;
        }

        public static class BookComparator implements Comparator<Entry<Integer, Integer>>, Serializable {

            @Override
            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return 1;
                } else if (o1.getValue() == o2.getValue()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    }

    public static class Subcategory extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSubcategory(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class MaxMesos extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return 1;
        }
    }

    public static class Mesos extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class MesosPerson extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.gainMeso(Integer.parseInt(splitted[2]), true);
            return 1;
        }
    }

    public static class giftnx extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 3) {
                c.getPlayer().print(5, "[Syntax] !giftnx [player] [amount]");
                return 0;
            }
            victim.modifyCSPoints(1, Integer.parseInt(splitted[2]), true);
            int NXamount = Integer.parseInt(splitted[2].toString());
            victim.print(5, "[NX Notice] " + c.getPlayer().getName().toString() + " has gifted you with " + NXamount + " NX");
            c.getPlayer().print(6, "[NX Notice] You have gifted " + victim.getName().toString() + " " + NXamount + " NX");
            return 1;
        }
    }

    public static class giftmaplepoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(5, "[Syntax] !giftmaplepoint [name] [amount]");
                return 0;
            }
            c.getPlayer().modifyCSPoints(2, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class giftvotepoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !giftvotepoint [name] [amount]");
                return 0;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().print(6, "Make sure they are in the correct channel");
            } else {
                chrs.setVPoints(chrs.getVPoints() + Integer.parseInt(splitted[2]));
                c.getPlayer().print(6, splitted[1] + " have " + chrs.getVPoints() + " vote points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }

    public static class giftjumpingpoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !giftjumpingpoint [name] [amount]");
                return 0;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().print(6, "Make sure they are in the correct channel");
            } else {
                chrs.addJQPoints(Integer.parseInt(splitted[2]));
                c.getPlayer().print(6, splitted[1] + " have " + chrs.getJQPoints() + " jumping quest points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }

    public static class giftdonorpoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !giftdonorpoint [name] [amount]");
                return 0;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().print(6, "Make sure they are in the correct channel");
            } else {
                chrs.setPoints(chrs.getPoints() + Integer.parseInt(splitted[2]));
                c.getPlayer().print(6, splitted[1] + " have " + chrs.getPoints() + " donor points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }

    public static class giftrarepoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !giftrarepoint [name] [amount]");
                return 0;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().print(6, "Make sure they are in the correct channel");
            } else {
                chrs.gainRareNX(Integer.parseInt(splitted[2]));
                c.getPlayer().print(6, splitted[1] + " have " + chrs.getRareNX() + " rare points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }

    public static class gaindonorpoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(5, "[Syntax] !gaindonorpoint [amount]");
                return 0;
            }
            c.getPlayer().setPoints(c.getPlayer().getPoints() + Integer.parseInt(splitted[1]));
            c.getPlayer().print(5, "Donor point has been added to your account successfully.");
            return 1;
        }
    }

    public static class gainrarepoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(5, "[Syntax] !gainrarepoint [amount]");
                return 0;
            }
            c.getPlayer().gainRareNX(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class gainvotepoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(5, "[Syntax] !gainvotepoint [amount]");
                return 0;
            }
            c.getPlayer().setVPoints(c.getPlayer().getVPoints() + Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return 1;
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            return 1;
        }
    }

    public static class ReloadPortal extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            PortalScriptManager.getInstance().clearScripts();
            return 1;
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().clear();
            return 1;
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return 1;
        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return 1;
        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return 1;
        }
    }

    public static class FHReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).forceHitReactor(Byte.parseByte(splitted[2]));
            return 1;
        }
    }

    public static class DReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return 1;
        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class ResetReactor extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetReactors();
            return 1;
        }
    }

    public static class SendNote extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length >= 2) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                c.getPlayer().sendNote(victim.getName(), text);
            } else {
                c.getPlayer().print(6, "[Syntax] !sendnote [name] [message]");
                return 0;
            }
            return 1;
        }
    }

    public static class SendAllNote extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                c.getPlayer().print(6, "[Syntax] !sendallnote [message]");
                return 0;
            }
            return 1;
        }
    }

    public static class BuffSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItemEX extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class cancelSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelBuff(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class MapBuffSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItemEX extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapItemSize extends CommandExecute { // Test

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Number Of Items: " + MapleItemInformationProvider.getInstance().getAllItems().size());
            return 0;
        }
    }

    public static class StartRace extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (!c.getChannelServer().getRace() && !!c.getChannelServer().getWaiting()) {
                c.getChannelServer().setWaiting(true);
                c.getChannelServer().setWaitingTime(Integer.parseInt(splitted[1])); // Replace with time in minutes you want to wait.
                c.getChannelServer().raceCountdown();
                c.getChannelServer().broadcastMessage(CWvsContext.serverNotice(6, "The Race event will begin soon! Please head to Henesys!"));
                c.getChannelServer().broadcastMessage(CWvsContext.serverNotice(6, "Use @race to join and @eventrules to see the rules and regulations of this event."));
            } else {
                c.getPlayer().print(0, "A race is still in progress.");
                return 0;
            }
            return 1;
        }
    }

    public static class openUIOption extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(CField.UIPacket.openUIOption(Integer.parseInt(splitted[1]), 9010000));
            return 1;
        }
    }

    public static class openUIWindow extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(CField.UIPacket.openUI(Integer.parseInt(splitted[1])));
            return 1;
        }
    }
}