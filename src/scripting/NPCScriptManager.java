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
package scripting;

import client.MapleClient;
import constants.WorldConstants;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

public class NPCScriptManager extends AbstractScriptManager {

    private static final NPCScriptManager instance = new NPCScriptManager();
    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<MapleClient, NPCConversationManager>();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final boolean hasScript(final MapleClient c, final int npc, String filename) {
        Invocable iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + npc + ".js", c, true);
        if (filename != null) {
            iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + filename + ".js", c, true);
        }
        return iv != null;
    }

    public final void start(final MapleClient c, final int npc, String filename) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("npc/" + npc + ".js", c, true); // Safe disposal
                if (filename != null) {
                    iv = getInvocable("npc/" + filename + ".js", c, true); // Safe disposal
                }
                if (iv == null) {
                    iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + npc + ".js", c, true);
                    if (filename != null) {
                        iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + filename + ".js", c, true);
                    }
                    if (iv == null) {
                        iv = getInvocable("npc/default/" + npc + ".js", c, true); // Safe disposal
                        if (filename != null) {
                            iv = getInvocable("npc/default/" + filename + ".js", c, true); // Safe disposal
                        }
                        if (iv == null) {
                            iv = getInvocable("npc/notcoded.js", c, true); // Safe disposal
                            if (iv == null) {
                                iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/notcoded.js", c, true); // Safe disposal
                                if (iv == null) {
                                    dispose(c);
                                    return;
                                }
                            }
                        }
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte) -1, iv);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            }
        } catch (final Exception e) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final Exception e) {
                System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + ":" + e);
                dispose(c);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + cm.getNpc() + "." + e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 0, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc + " startquest " + quest);
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // Start it off as something
            }
        } catch (final Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPC ID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPC ID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 1, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // Start it off as something
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPC ID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPC ID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPC ID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startItemScript(final MapleClient c, final int npc, final String script, final int itemid) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("item/" + script + ".js", c, true);
                if (iv == null) {
                    System.out.println("New scripted item : " + itemid);
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte) -1, iv);
                cms.put(c, cm);
                scriptengine.put("im", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("use");
            }
        } catch (final Exception e) {
            System.err.println("Error executing Item script, ITEM ID : " + itemid + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Item script, ITEM ID : " + itemid + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                c.removeScriptEngine("scripts/npc/notcoded.js");
            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }
}