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
package handling.login.handler;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.Servers;
import constants.WorldConstants.TespiaServers;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.World;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Logger;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;
import tools.packet.PacketHelper;

import java.util.*;
import java.util.Map.Entry;

public class CharLoginHandler {

    private static final boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 5;
    }

    public static final void login(final LittleEndianAccessor slea, final MapleClient c) {
        String login = c.isLocalhost() ? "admin" : slea.readMapleAsciiString();
        String pwd = c.isLocalhost() ? "admin" : slea.readMapleAsciiString();
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = 0;
        if (AutoRegister.autoRegister && !AutoRegister.getAccountExists(login) && (!c.hasBannedIP() || !c.hasBannedMac())) {
            AutoRegister.createAccount(login, pwd, c.getSession().getRemoteAddress().toString());
            if (AutoRegister.success) {
                c.getSession().write(CWvsContext.serverNotice(1, "Your account has been successfully created!\r\nPlease login again to enter your new account."));
                c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                return;
            }
        } else {
            loginok = c.login(login, pwd, ipBan || macBan);
        }

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (!c.isGm() && !c.isLocalhost() && ServerConstants.USE_LOCALHOST) {
            c.getSession().write(CWvsContext.serverNotice(1, "Project Nexus is currently on maintenanance. Therefore, only administrator are able to login.\r\nProject Nexus Maintenance :\r\nRevision : 0.0.1\r\nMaintenance From : DATE\r\nMaintenance To : DATE\r\n\r\nWhat's New For Revision 0.0.1?\r\nNew Rolling Feature\r\nText 1\r\nText 2\r\nText 3"));
            c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
        }

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // This is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                if (loginok == 3) {
                    c.getSession().write(CWvsContext.serverNotice(1, c.showBanReason(login, true)));
                    c.getSession().write(LoginPacket.getLoginFailed(1)); // Shows no message, used for unstuck the login button
                } else {
                    c.getSession().write(LoginPacket.getLoginFailed(loginok));
                }
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.getSession().write(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            FileoutputUtil.logToFile("logs/Accounts.txt", "\r\nID: " + login + " Password: " + pwd);
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static final void ServerListRequest(final MapleClient c) {
        c.getSession().write(LoginPacket.changeBackground());
        for (Servers servers : Servers.values()) {
            if (Servers.getById(servers.getWorld()).show() && Servers.getById(servers.getWorld()) != null) {
                c.getSession().write(LoginPacket.getServerList(Servers.getById(servers.getWorld()).getWorld(), LoginServer.getLoad()));
            }
        }
        if (ServerConstants.TESPIA) {
            for (TespiaServers tespiaservers : TespiaServers.values()) {
                if (TespiaServers.getById(tespiaservers.getWorld()).show() && TespiaServers.getById(tespiaservers.getWorld()) != null) {
                    c.getSession().write(LoginPacket.getTespiaServerList(tespiaservers.getWorld(), LoginServer.getLoad()));
                }
            }
        }
        if (WorldConstants.gmserver >= 0 && (c.isGm() || c.isLocalhost()) && Servers.getById(WorldConstants.gmserver) != null) {
            c.getSession().write(LoginPacket.getServerList(WorldConstants.gmserver, LoginServer.getLoad()));
            //c.getSession().write(CWvsContext.serverNotice(1, "Hello my dear GMs!\r\n" + Servers.getById(WorldConstants.gmserver) + " is our current GM Server.\r\nIn windia, we can test new npcs, systems and operation codes!"));
        }
        c.getSession().write(LoginPacket.getEndOfServerList());
        c.getSession().write(LoginPacket.enableRecommended());
        if (Servers.recommended >= 0) {
            c.getSession().write(LoginPacket.sendRecommended(Servers.recommended, Servers.recommendedmsg));
        }
    }

    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        slea.readByte(); //2?
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        if (!World.isChannelAvailable(channel)/* || server != WorldConstants.defaultserver*/) {
            c.getSession().write(LoginPacket.getLoginFailed(10)); //cannot process so many
            return;
        }

        if (!Servers.getById(server).isAvailable() && !(c.isGm() && server == WorldConstants.gmserver)) {
            c.getSession().write(CWvsContext.serverNotice(1, "We are sorry, but " + WorldConstants.getNameById(server) + " is currently not available. \r\nPlease try another world."));
            c.getSession().write(LoginPacket.getLoginFailed(1)); // Shows no message, but it is used to unstuck
            return;
        }

        //Logger.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");

        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            c.getSession().write(LoginPacket.getSecondAuthSuccess(c));
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword(), chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }

    public static final void updateCCards(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() != 24 || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = slea.readInt();
            if ((!c.login_Auth(charId) && charId != 0) || ChannelServer.getInstance(c.getChannel()) == null/* || c.getWorld() != WorldConstants.defaultserver*/) { //TODO Multi World
                c.getSession().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static final void CheckCharName(final String name, final MapleClient c) {
        c.getSession().write(LoginPacket.charNameResponse(name,
                !(MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()))));
    }

    public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final String name = slea.readMapleAsciiString();
        final JobType jobType = JobType.getByType(slea.readInt()); // BIGBANG: 0 = Resistance, 1 = Adventurer, 2 = Cygnus, 3 = Aran, 4 = Evan, 5 = mercedes
        final short db = slea.readShort(); // Whether dual blade = 1 or adventurer = 0
        final byte gender = slea.readByte(); // ??idk corresponds with the thing in addCharStats
        byte skinColor = slea.readByte(); // 01
        int hairColor = 0;
        final byte unk2 = slea.readByte(); // 08
        final boolean mercedes = (jobType == JobType.Mercedes);
        final boolean mihile = (jobType == JobType.Mihile);
        final boolean demon = (jobType == JobType.Demon);
        final boolean phantom = (jobType == JobType.Phantom);
        final int face = slea.readInt();
        final int hair = slea.readInt();
        if (!mercedes && !demon && !phantom) { // Mercedes / demon dont need hair color since its already in the hair
            hairColor = slea.readInt();
            skinColor = (byte) slea.readInt();
        }
        final int demonMark = demon ? slea.readInt() : 0;
        final int top = slea.readInt();
        final int bottom = (mercedes || demon) ? 0 : slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();
        int shield = demon ? slea.readInt() : 0;

        if (jobType == JobType.Demon) {
            shield = slea.readInt();
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, demonMark) || (skinColor != 0 && skinColor != 13)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shield)) {
                return;
            }
        } else if (jobType == JobType.Mercedes) {
            shield = 1352000;
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0 && skinColor != 12)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, weapon)) {
                return;
            }

        } else if (jobType == JobType.Jett) { // Diff
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0)
                    || /*!LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, cape) || */ !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon)) {
                return;
            }

        } else if (jobType == JobType.Phantom) { // Diff 
            // Phantom doesn't need checking but hair because we add equips manually
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, ((hair / 10) * 10) + 3)) {
                return;
            }
            shield = 1352104; // Carte

        } else if (jobType == JobType.Mihile) { // Diff 
            // Doesn't need hair and face checking because he has only one and we set it manually
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, bottom)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 7, jobType.type, weapon)) {
                return;
            }
        } else {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, hairColor) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, skinColor)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, bottom)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 7, jobType.type, weapon)) {
                return;
            }
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(mihile ? 20169 : face);
        newchar.setHair(mihile ? 36033 : hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);
        newchar.setDemonMarking(demonMark);

        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        Item item;

        if (!phantom) {
            item = li.getEquipById(top);
            item.setPosition((byte) -5);
            equip.addFromDB(item);

            if (bottom > 0) { // Resistance have overall
                item = li.getEquipById(bottom);
                //-1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Overall | -7 Shoes | -9 Cape | -11 Weapon
                item.setPosition((byte) -6);
                equip.addFromDB(item);
            }

            item = li.getEquipById(shoes);
            item.setPosition((byte) -7);
            equip.addFromDB(item);

            item = li.getEquipById(weapon);
            item.setPosition((byte) -11);
            equip.addFromDB(item);

        } else if (phantom) {
            item = li.getEquipById(gender == 0 ? 1050222 : 1051272);
            item.setPosition((byte) -5);
            equip.addFromDB(item);
            item = li.getEquipById(gender == 0 ? 1070023 : 1071035);
            item.setPosition((byte) -7);
            equip.addFromDB(item);
            item = li.getEquipById(1102347);
            item.setPosition((byte) -9);
            equip.addFromDB(item);
            item = li.getEquipById(1352104);
            item.setPosition((byte) -10);
            equip.addFromDB(item);
            item = li.getEquipById(1362000);
            item.setPosition((byte) -11);
            equip.addFromDB(item);
        }
        if (shield > 0) {
            item = li.getEquipById(shield);
            item.setPosition((byte) -10);
            equip.addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));
        // Blue / red pots
        switch (jobType) {
            case Resistance: // Resistance
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case Adventurer: // Adventurer
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case Cygnus: // Cygnus
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null);
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case Aran: // Aran
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
            case Evan: // Evan
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
                break;
            case Mercedes: // Mercedes
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161079, (byte) 0, (short) 1, (byte) 0));
                final Map<Skill, SkillEntry> ss = new HashMap<>();
                ss.put(SkillFactory.getSkill(20021000), new SkillEntry((byte) 0, (byte) 0, -1));
                ss.put(SkillFactory.getSkill(20021001), new SkillEntry((byte) 0, (byte) 0, -1));
                ss.put(SkillFactory.getSkill(20020002), new SkillEntry((byte) 0, (byte) 0, -1));
                ss.put(SkillFactory.getSkill(20020022), new SkillEntry((byte) 1, (byte) 1, -1));
                ss.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
                ss.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
                ss.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
                ss.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
                ss.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
                ss.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
                newchar.changeSkillLevel_Skip(ss, false);
                break;
            case Demon: // Demon
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161054, (byte) 0, (short) 1, (byte) 0));
                final Map<Skill, SkillEntry> ss2 = new HashMap<>();
                ss2.put(SkillFactory.getSkill(30011000), new SkillEntry((byte) 0, (byte) 0, -1));
                ss2.put(SkillFactory.getSkill(30011001), new SkillEntry((byte) 0, (byte) 0, -1));
                ss2.put(SkillFactory.getSkill(30010002), new SkillEntry((byte) 0, (byte) 0, -1));
                ss2.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30010112), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30010022), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30011170), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30011169), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30011168), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30011167), new SkillEntry((byte) 1, (byte) 1, -1));
                ss2.put(SkillFactory.getSkill(30010166), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss2, false);
                break;
            case Phantom:
                final Map<Skill, SkillEntry> ss3 = new HashMap<>();
                ss3.put(SkillFactory.getSkill(20031203), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031204), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031205), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031206), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031207), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031208), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031209), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031210), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss3, false);
                break;
            case Jett:
                final Map<Skill, SkillEntry> ss4 = new HashMap<>();
                ss4.put(SkillFactory.getSkill(228), new SkillEntry((byte) 1, (byte) 1, -1));
                ss4.put(SkillFactory.getSkill(0001214), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss4, false);
                break;
            case Mihile:
                final Map<Skill, SkillEntry> ss5 = new HashMap<>();
                ss5.put(SkillFactory.getSkill(50001214), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss5, false);
                break;
        }

        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, db);
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
    }
    /*public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
     if (!c.isLoggedIn()) {
     c.getSession().close();
     return;
     }
     final String name = slea.readMapleAsciiString();
     final JobType jobType = JobType.getByType(slea.readInt()); //0 = Resistance, 1 = Adventurer, 2 = Cygnus, 3 = Aran, 4 = Evan, 5 = mercedes, 6 = demon slayer, 7 = phantom, 8 = dual blade, 9 = mihile
     final short db = slea.readShort(); //whether dual blade = 1 or adventurer = 0
     final byte gender = slea.readByte(); //??idk corresponds with the thing in addCharStats
     byte skinColor = slea.readByte(); // 01
     int hairColor = 0;
     final byte unk2 = slea.readByte(); // 08
     final boolean mercedes = (jobType == JobType.Mercedes);
     final boolean demon = (jobType == JobType.Demon);
     final boolean mihile = (jobType == JobType.Mihile);
     final boolean jett = (jobType == JobType.Jett);
     final boolean phantom = (jobType == JobType.Phantom);
     boolean jettPhantom = (jobType == LoginInformationProvider.JobType.Jett) || (jobType == LoginInformationProvider.JobType.Phantom) || (jobType == LoginInformationProvider.JobType.DualBlade);
     final int face = slea.readInt();
     final int hair = slea.readInt();
     if (!mercedes && !demon && !jettPhantom && !mihile) { //mercedes/demon dont need hair color since its already in the hair, the same about mihile, jett and phantom
     hairColor = slea.readInt();
     skinColor = (byte) slea.readInt();
     }
     final int demonMark = demon ? slea.readInt() : 0;
     final int top = slea.readInt();
     final int bottom = (mercedes || demon || jettPhantom) ? 0 : slea.readInt();
     final int cape = (jettPhantom) ? slea.readInt() : 0;
     final int shoes = slea.readInt();
     final int weapon = slea.readInt();
     int shield = 0;
        
     if (jobType == JobType.Demon) {
     shield = slea.readInt();
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, demonMark) || (skinColor != 0 && skinColor != 13)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shield)) {
     return;
     }
     } else if (jobType == JobType.Mercedes) {
     shield = 1352000;
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0 && skinColor != 12)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, weapon)) {
     return;
     }

     } else if (jobType == JobType.Jett) { //diff
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, cape) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon)) {
     return;
     }
  
     } else if (jobType == JobType.Phantom) { //diff 
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, ((hair / 10) * 10) + 3)                  
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, cape) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon)) {     
     return;
     }
     shield = 1352104; //carte
  
     } else if (jobType == JobType.Mihile) { //diff 
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, ((hair / 10) * 10) + 3)                  
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon)) {     
     return;
     }
     } else {
     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, hairColor) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, skinColor)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, bottom)
     || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 7, jobType.type, weapon)) {
     return;
     }
     }
        
     MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
     newchar.setWorld((byte) c.getWorld());
     newchar.setFace(face);
     newchar.setHair(hair + hairColor);
     newchar.setGender(gender);
     newchar.setName(name);
     newchar.setSkinColor(skinColor);
     newchar.setDemonMarking(demonMark);

     final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
     final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
     Item item;

     if (!phantom && !mihile) {
     item = li.getEquipById(top);
     item.setPosition((byte) -5);
     equip.addFromDB(item);
     if (bottom > 0) { //resistance have overall
     item = li.getEquipById(bottom);
     item.setPosition((byte) -6);
     equip.addFromDB(item);
     }
     item = li.getEquipById(shoes);
     item.setPosition((byte) -7);
     equip.addFromDB(item);
     item = li.getEquipById(weapon);
     item.setPosition((byte) -11);
     equip.addFromDB(item);
     if (shield > 0) {
     item = li.getEquipById(shield);
     item.setPosition((byte) -10);
     equip.addFromDB(item);
     }
     if (cape > 0) {
     item = li.getEquipById(cape);
     item.setPosition((byte) (-9));
     equip.addFromDB(item);
     }
     } else if (phantom) {
     item = li.getEquipById(gender == 0 ? 1050222 : 1051272);
     item.setPosition((byte) -5);
     equip.addFromDB(item);
     item = li.getEquipById(gender == 0 ? 1070023 : 1071035);
     item.setPosition((byte) -7);
     equip.addFromDB(item);
     item = li.getEquipById(1102347);
     item.setPosition((byte) -9);
     equip.addFromDB(item);
     item = li.getEquipById(1352104);
     item.setPosition((byte) -10);
     equip.addFromDB(item);
     item = li.getEquipById(1362000);
     item.setPosition((byte) -11);
     equip.addFromDB(item);
     } else if (mihile) {
     item = li.getEquipById(1040002);
     item.setPosition((byte) -5);
     equip.addFromDB(item);
     item = li.getEquipById(1060002);
     item.setPosition((byte) -6);
     equip.addFromDB(item);
     item = li.getEquipById(1072001);
     item.setPosition((byte) -7);
     equip.addFromDB(item);
     item = li.getEquipById(1302000);
     item.setPosition((byte) -11);
     equip.addFromDB(item);
     }
     newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
     newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));
     //blue/red pots
     switch (jobType) {
     case Resistance: // Resistance
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
     break;
     case Adventurer: // Adventurer
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
     break;
     case Cygnus: // Cygnus
     newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
     newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
     break;
     case Aran: // Aran
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
     break;
     case Evan: //Evan
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
     break;
     case Mercedes: // Mercedes
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161079, (byte) 0, (short) 1, (byte) 0));
     final Map<Skill, SkillEntry> ss = new HashMap<>();
     ss.put(SkillFactory.getSkill(20021000), new SkillEntry((byte) 0, (byte) 0, -1));
     ss.put(SkillFactory.getSkill(20021001), new SkillEntry((byte) 0, (byte) 0, -1));
     ss.put(SkillFactory.getSkill(20020002), new SkillEntry((byte) 0, (byte) 0, -1));
     ss.put(SkillFactory.getSkill(20020022), new SkillEntry((byte) 1, (byte) 1, -1));
     ss.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
     ss.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
     ss.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
     ss.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
     ss.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
     ss.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
     newchar.changeSkillLevel_Skip(ss, false);
     break;
     case Demon: //Demon
     newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161054, (byte) 0, (short) 1, (byte) 0));
     final Map<Skill, SkillEntry> ss2 = new HashMap<>();
     ss2.put(SkillFactory.getSkill(30011000), new SkillEntry((byte) 0, (byte) 0, -1));
     ss2.put(SkillFactory.getSkill(30011001), new SkillEntry((byte) 0, (byte) 0, -1));
     ss2.put(SkillFactory.getSkill(30010002), new SkillEntry((byte) 0, (byte) 0, -1));
     ss2.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30010112), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30010022), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30011170), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30011169), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30011168), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30011167), new SkillEntry((byte) 1, (byte) 1, -1));
     ss2.put(SkillFactory.getSkill(30010166), new SkillEntry((byte) 1, (byte) 1, -1));
     newchar.changeSkillLevel_Skip(ss2, false);
     break;
     case Phantom:
     final Map<Skill, SkillEntry> ss3 = new HashMap<>();
     ss3.put(SkillFactory.getSkill(20031203), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031204), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031205), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031206), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031207), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031208), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031209), new SkillEntry((byte) 1, (byte) 1, -1));
     ss3.put(SkillFactory.getSkill(20031210), new SkillEntry((byte) 1, (byte) 1, -1));
     newchar.changeSkillLevel_Skip(ss3, false);
     break;
     case Jett:
     final Map<Skill, SkillEntry> ss4 = new HashMap<>();
     ss4.put(SkillFactory.getSkill(228), new SkillEntry((byte) 1, (byte) 1, -1));
     ss4.put(SkillFactory.getSkill(0001214), new SkillEntry((byte) 1, (byte) 1, -1));
     newchar.changeSkillLevel_Skip(ss4, false);
     break;
     case Mihile:
     final Map<Skill, SkillEntry> ss5 = new HashMap<>();
     ss5.put(SkillFactory.getSkill(50001214), new SkillEntry((byte) 1, (byte) 1, -1));
     newchar.changeSkillLevel_Skip(ss5, false);
     break;
     }

     if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
     MapleCharacter.saveNewCharToDB(newchar, jobType, db);
     c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
     c.createdChar(newchar.getId());
     } else {
     c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
     }
     }*/

    public static final void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isGM() && (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || c.getPlayer().getQuestStatus(20734) != 0 || c.getPlayer().getQuestStatus(20616) != 2 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld()))) {
            c.getPlayer().print(1, "You have no character slots.");
            c.getSession().write(CField.createUltimate(1));
            return;
        }
        //Logger.println(slea.toString());
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt(); //job ID

        final int face = slea.readInt();
        final int hair = slea.readInt();

        // No idea what are these used for:
        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();

        //JobType errorCheck = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, errorCheck.type, face)) {
        //    c.getPlayer().print(1, "An error occurred.");
        //    c.getSession().write(CField.createUltimate(0));
        //    return;
        //}

        JobType jobType = JobType.UltimateAdventurer;


        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld(c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); // Troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); // 49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); // 2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; // Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; // Job Advancement
                newchar.getStat().maxhp += 2000; // Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; // Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; // Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; // Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; // Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        // TODO: Make this GMS - Like
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        // TODO: Make this GMS - Like
        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; // Brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(CField.createUltimate(0));
        } else {
            c.getSession().write(CField.createUltimate(1));
        }
    }

    public static final void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        String Secondpw_Client = GameConstants.GMS ? slea.readMapleAsciiString() : null;
        if (Secondpw_Client == null) {
            if (slea.readByte() > 0) { // Specific if user have second password or not
                Secondpw_Client = slea.readMapleAsciiString();
            }
            slea.readMapleAsciiString();
        }

        final int Character_ID = slea.readInt();

        if (!c.login_Auth(Character_ID) || !c.isLoggedIn() || loginFailCount(c)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static final void Character_WithoutSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean haspic, final boolean view) {
        slea.readByte(); // 1?
        slea.readByte(); // 1?
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        final String currentpw = c.getSecondPassword();
        if (!c.isLoggedIn() || loginFailCount(c) || (currentpw != null && (!currentpw.equals("") || haspic)) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null/* || c.getWorld() != WorldConstants.defaultserver*/) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        c.updateMacs(slea.readMapleAsciiString());
        slea.readMapleAsciiString();
        if (slea.available() != 0) {
            final String setpassword = slea.readMapleAsciiString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
                return;
            }
        } else if (GameConstants.GMS && haspic) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        c.getSession().write(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }

    public static void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || c.hasBannedMac() || c.hasBannedIP()/* || c.getWorld() != WorldConstants.defaultserver*/) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        if (GameConstants.GMS) {
            c.updateMacs(slea.readMapleAsciiString());
        }
        if (c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16) {
            FileoutputUtil.logToFile("logs/Secondary Passwords.txt", "\r\nID: " + c.getAccountName() + " PIC: " + password);
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }

            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
            c.getSession().write(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
        }
    }

    public static void ViewChar(LittleEndianAccessor slea, MapleClient c) {
        Map<Byte, ArrayList<MapleCharacter>> worlds = new HashMap<>();
        List<MapleCharacter> chars = null;
        for (Servers servers : Servers.values()) {
            if (servers.show() && servers.isAvailable()) {
                for (MapleCharacter chr : c.loadCharacters(servers.getWorld())) {
                    chars.add(chr);
                }
            }
        }
        // Idk how to add tespia servers because their world is a string
        c.getSession().write(LoginPacket.showAllCharacter(chars.size()));
        for (MapleCharacter chr : chars) {
            if (chr != null) {
                ArrayList<MapleCharacter> chrr;
                if (!worlds.containsKey(chr.getWorld())) {
                    chrr = new ArrayList<>();
                    worlds.put(chr.getWorld(), chrr);
                } else {
                    chrr = worlds.get(chr.getWorld());
                }
                chrr.add(chr);
            }
        }
        for (Entry<Byte, ArrayList<MapleCharacter>> w : worlds.entrySet()) {
            c.getSession().write(LoginPacket.showAllCharacterInfo(w.getKey(), w.getValue(), c.getSecondPassword()));
        }
    }

    public static final void partTimeJob(final LittleEndianAccessor slea, final MapleClient c) {
        byte mode = slea.readByte(); // 1 = start 2 = end
        int cid = slea.readInt(); // Character ID
        byte job = slea.readByte(); // Part time job
        Logger.println("[Part Time Job] data: " + slea);
        if (mode == 0) {
            //LoginPacket.stopPartTime(cid, job);
        } else if (mode == 1) {
            //LoginPacket.activatePartTime(cid, job, System.currentTimeMillis());
        }
    }
}