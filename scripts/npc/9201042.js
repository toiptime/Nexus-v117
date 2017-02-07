var status = 0;
sendYesNoOption = false

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }else if (mode == 0){
        cm.dispose();
        return;        
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        }
        if (status == 0) {
            cm.sendSimple("Hello #b#h ##k,\r\n\r\nYou currently have #g" + cm.getPlayer().getJQPoints() + "#k Jumping Quest Points.\r\n" +
            "\r\n#L0##gI would like to exchange for rewards"); 
        } else if (status == 1) {
            if (selection == 0) { // so all of this selection stuff will be in the next status
                cm.sendSimple("Please pick your reward.\r\n" +
                "\r\n#L0##i1142204# #b(20 Jumping Quest Points)" +
                "\r\n#L1##i1142205# (30 Jumping Quest Points)" +
				"\r\n#L2##i1142206# (40 Jumping Quest Points)" +
				"\r\n#L3##i1142207# (50 Jumping Quest Points)" +
				"\r\n#L4##i4033268# (10 Jumping Quest Points)" +
				"\r\n#L5##i2022529# (2x Meso Drops 30 Minutes) [10 Jumping Quest Points]" +
				"\r\n#L6##i2022530# (2x Item Drops 30 Minutes) [10 Jumping Quest Points]" +
				"\r\n#L7##i2022531# (2x Item Drops 1 Hour) [18 Jumping Quest Points]" +
				"\r\n#L8##i3010123# (50 Jumping Quest Points)"); 
              } else if (selection == 1) {
                sendYesNoOption = true
                cm.sendYesNo("Are you sure you would like to purchase 1,000 Nexon Cash for 2 JQ Points?"); // same as above comment
            }
        } else if (status == 2) {
            if (selection == 0) { 
                if (cm.getJQPoints() > 19) {
                   cm.gainJQPoints(-20);
                   cm.gainItem(1142204, 1);
				   cm.sendOk("You have exchanged for an #i1142204#.");
                   cm.dispose();
                } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for an #i1142204#.");
                    cm.dispose();
        }
        }
			else if (selection == 1) {
                if (cm.getJQPoints() > 29) {
                   cm.gainJQPoints(-30);
                   cm.gainItem(1142205, 1);
				   cm.sendOk("You have exchanged for a #i1142205#.");
                   cm.dispose();            
           } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i1142205#.");
                    cm.dispose();
        }
        }
		else if (selection == 2) {
                if (cm.getJQPoints() > 39) {
                   cm.gainJQPoints(-40);
                   cm.gainItem(1142206, 1);
				   cm.sendOk("You have exchanged for a #i1142206#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i1142206#.");
                    cm.dispose();
        }
        }
		else if (selection == 3) {
                if (cm.getJQPoints() > 49) {
                   cm.gainJQPoints(-50);
                   cm.gainItem(1142207, 1);
				   cm.sendOk("You have exchanged for a #i1142207#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i1142207#.");
                    cm.dispose();
                }
        }
		else if (selection == 4) {
                if (cm.getJQPoints() > 9) {
                   cm.gainJQPoints(-10);
                   cm.gainItem(4033268, 1);
				   cm.sendOk("You have exchanged for a #i4033268#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i4033268#.");
                    cm.dispose();
                }
        }
		else if (selection == 5) {
                if (cm.getJQPoints() > 9) {
                   cm.gainJQPoints(-10);
                   cm.gainItem(2022529, 1);
				   cm.sendOk("You have exchanged for a #i2022529#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i2022529#.");
                    cm.dispose();
                }
        }
		else if (selection == 6) {
                if (cm.getJQPoints() > 9) {
                   cm.gainJQPoints(-10);
                   cm.gainItem(2022530, 1);
				   cm.sendOk("You have exchanged for a #i2022530#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i2022530#.");
                    cm.dispose();
                }
        }
		else if (selection == 7) {
                if (cm.getJQPoints() > 17) {
                   cm.gainJQPoints(-18);
                   cm.gainItem(2022531, 1);
				   cm.sendOk("You have exchanged for a #i2022531#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i2022531#.");
                    cm.dispose();
                }
        }
		else if (selection == 8) {
                if (cm.getJQPoints() > 49) {
                   cm.gainJQPoints(-50);
                   cm.gainItem(3010123, 1);
				   cm.sendOk("You have exchanged for a #i3010123#.");
                   cm.dispose();            
            } else {
                    cm.sendOk("You don't have enough Jumping Quest Points to exchange for a #i3010123#.");
                    cm.dispose();
                }
        }
    }
}  