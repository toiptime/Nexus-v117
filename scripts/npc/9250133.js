var points;
var one;
var status = -1;
var menu = ["Equip", "Use", "Etc", "Setup", "Cash", "VIP", "10 Miracle Cubes", "25 Miracle Cubes", "50 Miracle Cubes", "100 Miracle Cubes"]; 
var talk = "\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\n\r##b"; 
var rewards = [
[[1003439, 1500, 1], [1302098, 200, 1], [1302099, 400, 1], [1302100, 600, 1], [1302101, 800, 1], [1302106, 200, 1], [1302107, 500, 1]],  // Equips
[[1003439, 1000000, 1]], // Use
[[3010313, 3000, 1]], // Etc
[[3010313, 3000, 1]], // Setup
[[5062000, 100, 1], [5062001, 150, 1], [5062002, 200, 1], [5062005, 250, 1], [5062003, 300, 1], [5750000, 300, 1]], // Cash
[[1032080, 300, 1], [1032081, 600, 1], [1032082, 900, 1], [1032083, 1200, 1], [1032084, 1500, 1], [1122081, 300, 1], [1122082, 600, 1], [1122083, 900, 1], [1122084, 1200, 1], [1122085, 1500, 1], [1132036, 300, 1], [1132037, 600, 1], [1132038, 900, 1], [1132039, 1200, 1], [1132040, 1500, 1], [1112435, 300, 1], [1112436, 600, 1], [1112437, 900, 1], [1112438, 1200, 1], [1112439, 1500, 1], [1092070, 300, 1], [1092071, 600, 1], [1092072, 900, 1], [1092073, 1200, 1], [1092074, 1500, 1], [1092075, 300, 1], [1092076, 600, 1], [1092077, 900, 1], [1092078, 1200, 1], [1092079, 1500, 1], [1092080, 300, 1], [1092081, 600, 1], [1092082, 900, 1], [1092083, 1200, 1], [1092084, 1500, 1], [1302143, 300, 1], [1302144, 600, 1], [1302145, 900, 1], [1302146, 1200, 1], [1302147, 1500, 1], [1312058, 300, 1], [1312059, 600, 1], [1312060, 900, 1], [1312061, 1200, 1], [1312062, 1500, 1], [1322086, 300, 1], [1322087, 600, 1], [1322088, 900, 1], [1322089, 1200, 1], [1322090, 1500, 1], [1332116, 300, 1], [1332117, 600, 1], [1332118, 900, 1], [1332119, 1200, 1], [1332120, 1500, 1], [1332121, 300, 1], [1332121, 600, 1], [1332122, 900, 1], [1332123, 1200, 1], [1332124, 1500, 1], [1342029, 300, 1], [1342030, 600, 1], [1342031, 900, 1], [1342032, 1200, 1], [1342033, 1500, 1], [1372074, 300, 1], [1372075, 600, 1], [1372076, 900, 1], [1372077, 1200, 1], [1372078, 1500, 1], [1382095, 300, 1], [1382096, 600, 1], [1382097, 900, 1], [1382098, 1200, 1], [1382099, 1500, 1], [1402086, 300, 1], [1402087, 600, 1], [1402088, 900, 1], [1402089, 1200, 1], [1402090, 1500, 1], [1412058, 300, 1], [1412059, 600, 1], [1412060, 900, 1], [1412061, 1200, 1], [1412062, 1500, 1], [1422059, 300, 1], [1422060, 600, 1], [1422061, 900, 1], [1422062, 1200, 1], [1422063, 1500, 1], [1432077, 300, 1], [1432078, 600, 1], [1432079, 900, 1], [1432080, 1200, 1], [1432081, 1500, 1], [1442107, 300, 1], [1442108, 600, 1], [1442109, 900, 1], [1442110, 1200, 1], [1442111, 1500, 1], [1452102, 300, 1], [1452103, 600, 1], [1452104, 900, 1], [1452105, 1200, 1], [1452106, 1500, 1], [1462087, 300, 1], [1462088, 600, 1], [1462089, 900, 1], [1462090, 1200, 1], [1462091, 1500, 1], [1472113, 300, 1], [1472114, 600, 1], [1472115, 900, 1], [1472116, 1200, 1], [1472117, 1500, 1], [1482075, 300, 1], [1482076, 600, 1], [1482077, 900, 1], [1482078, 1200, 1], [1482079, 1500, 1], [1492075, 300, 1], [1492076, 600, 1], [1492077, 900, 1], [1492078, 1200, 1], [1492079, 1500, 1]],
[[5062000, 1000, 10], [5062001, 1500, 10], [5062002, 2000, 10], [5062005, 2500, 10], [5062003, 3000, 10], [5750000, 3000, 10]], // 10x Cash
[[5062000, 2500, 25], [5062001, 3750, 25], [5062002, 5000, 25], [5062005, 6750, 25], [5062003, 8000, 25], [5750000, 8000, 25]], // 25x Cash
[[5062000, 5000, 50], [5062001, 7500, 50], [5062002, 10000, 50], [5062005, 12500, 50], [5062003, 15000, 50], [5750000, 15000, 50]], // 50x Cash
[[5062000, 10000, 100], [5062001, 15000, 100], [5062002, 20000, 100], [5062005, 25000, 100], [5062003, 30000, 100], [5750000, 30000, 100]] // 100x Cash
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        }
    }

    if (mode == 1) 
        status++;

    else 
        status--;
    if (status == 0) { 
        var record = cm.getQuestRecord(150001);
        points = record.getCustomData() == null ? "0" : record.getCustomData();
        cm.sendSimple("Hello #b#h ##k,\r\n\r\nWhat would you like to do?\r\n#b#L104#Check My Current Points#l\r\n#b#L105#Exchange For Prizes#l\r\n\r\n#b#L100# #v03994115##l #L101# #v03994116##l #L102# #v03994117##l #L103# #v03994118##l\r\n");
    }else if (status == 1) {
        if (mode == 1) {
            switch (selection) {
                case 100:
                    if (cm.getParty() != null) {
                        if (cm.getDisconnected("BossQuestEASY") != null) {
                            cm.getDisconnected("BossQuestEASY").registerPlayer(cm.getPlayer());
                        } else if (cm.isLeader()) {
                            var party = cm.getPlayer().getParty().getMembers();
                            var mapId = cm.getPlayer().getMapId();
                            var next = true;
                            var it = party.iterator();
                            while (it.hasNext()) {
                                var cPlayer = it.next();
                                var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                                if (ccPlayer == null || ccPlayer.getLevel() < 70) {
                                    next = false;
                                    break;
                                }
                            }	
                            if (next) {
                                var q = cm.getEventManager("BossQuestEASY");
                                if (q == null) {
                                    cm.sendOk("Unknown error occured");
                                } else {
                                    q.startInstance(cm.getParty(), cm.getMap());
									cm.dispose();
                                }
                            } else {
                                cm.sendOk("Your party members must be in this map and above level 70 to enter.");
                                cm.dispose();
                            }
                        } else {
                            cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("Please form a party first.");
                        cm.dispose();
                    }
                    break;
                case 101:
                    if (cm.getParty() != null) {
                        if (cm.getDisconnected("BossQuestMed") != null) {
                            cm.getDisconnected("BossQuestMed").registerPlayer(cm.getPlayer());
                        } else if (cm.isLeader()) {
                            var party = cm.getPlayer().getParty().getMembers();
                            var mapId = cm.getPlayer().getMapId();
                            var next = true;
                            var it = party.iterator();
                            while (it.hasNext()) {
                                var cPlayer = it.next();
                                var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                                if (ccPlayer == null || ccPlayer.getLevel() < 100) {
                                    next = false;
                                    break;
                                }
                            }	
                            if (next) {
                                var q = cm.getEventManager("BossQuestMed");
                                if (q == null) {
                                    cm.sendOk("Unknown error occured");
                                } else {
                                    q.startInstance(cm.getParty(), cm.getMap());
									cm.dispose();
                                }
                            } else {
                                cm.sendOk("Your party members must be in this map and above level 100 to enter.");
                                cm.dispose();
                            }
                        } else {
                            cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("Please form a party first.");
                        cm.dispose();
                    }
                    break;
                case 102:
                    if (cm.getParty() != null) {
                        if (cm.getDisconnected("BossQuestHARD") != null) {
                            cm.getDisconnected("BossQuestHARD").registerPlayer(cm.getPlayer());
                        } else if (cm.isLeader()) {
                            var party = cm.getPlayer().getParty().getMembers();
                            var mapId = cm.getPlayer().getMapId();
                            var next = true;
                            var it = party.iterator();
                            while (it.hasNext()) {
                                var cPlayer = it.next();
                                var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                                if (ccPlayer == null || ccPlayer.getLevel() < 120) {
                                    next = false;
                                    break;
                                }
                            }	
                            if (next) {
                                var q = cm.getEventManager("BossQuestHARD");
                                if (q == null) {
                                    cm.sendOk("Unknown error occured");
                                } else {
                                    q.startInstance(cm.getParty(), cm.getMap());
									cm.dispose();
                                }
                            } else {
                                cm.sendOk("Your party members must be in this map and above level 120 to enter.");
                                cm.dispose();
                            }
                        } else {
                            cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("Please form a party first.");
                        cm.dispose();
                    }
                    break;
                case 103:
                    if (cm.getParty() != null) {
                        if (cm.getDisconnected("BossQuestHELL") != null) {
                            cm.getDisconnected("BossQuestHELL").registerPlayer(cm.getPlayer());
                        } else if (cm.isLeader()) {
                            var party = cm.getPlayer().getParty().getMembers();
                            var mapId = cm.getPlayer().getMapId();
                            var next = true;
                            var it = party.iterator();
                            while (it.hasNext()) {
                                var cPlayer = it.next();
                                var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                                if (ccPlayer == null || ccPlayer.getLevel() < 160) {
                                    next = false;
                                    break;
                                }
                            }	
                            if (next) {
                                var q = cm.getEventManager("BossQuestHELL");
                                if (q == null) {
                                    cm.sendOk("Unknown error occured");
                                } else {
                                    q.startInstance(cm.getParty(), cm.getMap());
									cm.dispose();
                                }
                            } else {
                                cm.sendOk("Your party members must be in this map and above level 160 to enter.");
                                cm.dispose();
                            }
                        } else {
                            cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("Please form a party first.");
                        cm.dispose();
                    }
                    break;
                case 104:
                    cm.sendOk("Your Current Points: #g" + points);
                    cm.dispose();
                    break;
                case 105:
                    var text = "Your Current Points: #g"+points+"#k\r\n"+talk+""; 
                    for (var z = 0; z < menu.length; z++) 
                        text+= "#L"+z+"##b"+menu[z]+"#l\r\n"; 
                    one = false;
                    cm.sendSimple(text); 
                    break;
            }
        }
    } else if (status == 2) {
        if (one == false) {
            c = selection; 
            for (var i = 0; i < rewards[c].length; i++) 
                talk+="#L"+i+"##e#i"+rewards[c][i]+":##k#l"; 
            cm.sendSimple("#r#eClick on any of these items to purchase.#k#n\r\n"+talk);
            one = false;
        }		
    }
    else if (status == 3) {
        var record = cm.getQuestRecord(150001);
        var intPoints = parseInt(points);
        var id = rewards[c][selection];
		
        if (intPoints >= id[1]) {
            if (cm.canHold(id[0])) {
                intPoints -= id[1];
                record.setCustomData(""+intPoints+"");
                cm.gainItem(id[0], id[2]);
                //cm.sendOk("id "+id[0]+" price "+id[1]+" amount "+id[2]);         
                cm.sendOk("Enjoy your new item.");
                cm.dispose();
            } else {
                cm.sendOk("Please check if you have sufficient inventory slot for it.");
                cm.dispose();
            }
        } else {
		cm.sendOk("You don't have enough #rBoss Party Quest Points#k.\r\n#b#t"+id[0]+"##k costs #b"+id[1]+"#k Boss Party Quest Points.");
        cm.dispose();
        }               
    }
}