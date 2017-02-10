package client.messages.commands;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Bekzod
 */
public class DonatorCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.DONATOR;
    }

    public static class Heal extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() == 109020001 || c.getPlayer().getMapId() == 800020400) {
                c.getPlayer().print(5, "You may not use this command in the current map.");
                return 0;
            }
            c.getPlayer().getStat().heal(c.getPlayer());
            c.getPlayer().dispelDebuffs();
            return 1;
        }
    }

    public static class location extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, "You are on map " + c.getPlayer().getMap().getId());
            return 1;
        }
    }

    public static class clear extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new HashMap<Pair<Short, Short>, MapleInventoryType>();
            if (splitted[1].equals("all")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), type);
                    }
                }
            } else if (splitted[1].equals("equipped")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                }
            } else if (splitted[1].equals("equip")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIP);
                }
            } else if (splitted[1].equals("use")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.USE);
                }
            } else if (splitted[1].equals("setup")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.SETUP);
                }
            } else if (splitted[1].equals("etc")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.ETC);
                }
            } else if (splitted[1].equals("cash")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.CASH);
                }
            } else {
                c.getPlayer().print(6, "[Syntax] !clear [all / equipped / equip / use / setup / etc / cash]");
            }
            for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return 1;
        }
    }

    public static class weather extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(CField.removeMapEffect());
                c.getPlayer().print(5, "Map weather has been disabled.");
            } else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().print(5, "Invalid ID.");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(CField.startMapEffect("", weather, false));
                    c.getPlayer().print(5, "Map weather has been enabled.");
                }
            }
            return 1;
        }
    }

    public static class go extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("donor", 910000007);
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                c.getPlayer().print(6, "Even if you are a donator, You cannot warp out of jail.");
                return 0;
            }
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] #go [map]. To view a list of locations that are available, use #locations");
            }
            return 1;
        }
    }

    public static class locations extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".:::::::::::: Donor Locations ::::::::::::.");
            c.getPlayer().print(5, "| donor |");
            return 1;
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return 1;
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                c.getPlayer().print(6, "Even if you are a donator, You cannot use the command #say in jail.");
                return 0;
            }
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                if (c.getPlayer().isDonor()) {
                    sb.append("[Donor] ");
                }
                sb.append(c.getPlayer().getName());
                sb.append(": ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(c.getPlayer().isGM() ? 6 : 5, sb.toString()));
            } else {
                c.getPlayer().print(6, "[Syntax] #say [message]");
                return 0;
            }
            return 1;
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] #letter [green / red] [word]");
                return 0;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().print(6, "Unknown Color!");
                return 0;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            // Logger.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return 1;
        }
    }

    public static class loot extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final List<MapleMapObject> items = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), GameConstants.maxViewRangeSq(), Arrays.asList(MapleMapObjectType.ITEM));
            MapleMapItem mapitem;
            for (MapleMapObject item : items) {
                mapitem = (MapleMapItem) item;
                if (mapitem.getMeso() > 0) {
                    c.getPlayer().gainMeso(mapitem.getMeso(), true);
                } else if (mapitem.getItem() == null || !MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
                    continue;
                }
                mapitem.setPickedUp(true);
                c.getPlayer().getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()), mapitem.getPosition());
                c.getPlayer().getMap().removeMapObject(item);

            }
            return 1;
        }
    }

    public static class help extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, ".::::::::::::::::::::::::::::::: Project Nexus Donor Commands ::::::::::::::::::::::::::::::::.");
            c.getPlayer().print(6, "#heal - Heals yourself.");
            c.getPlayer().print(6, "#clear [equipped / equip / use / setup / etc / cash / all] - Cleans the trash in your inventory that you requested.");
            c.getPlayer().print(6, "#weather - Toggle on / off the snow effect in your current map.");
            c.getPlayer().print(6, "#go [map] - Go to the map that you requested.");
            c.getPlayer().print(6, "#locations - Shows the list of locations that are available.");
            c.getPlayer().print(6, "#clock [second] - Make the time effect in your current map.");
            c.getPlayer().print(6, "#say [message] - Say message to the world of Project Nexus with a [Donor] tag.");
            c.getPlayer().print(6, "#letter [red / green] [word] - Drops the letters that you have requested.");
            c.getPlayer().print(6, "#loot - Loot all the items in your current map.");

            return 1;
        }
    }
}