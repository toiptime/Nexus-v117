var chosen = 1;
var Error;
var one;
var c; 
status = 0; 
function start() {
    cm.sendAcceptDecline("Hello #b#h ##k,\r\n\r\nI can give you reward if you have enough reborns.");
} 
function action(m,t,selection) { 
    chosen = selection;
    if (m != 1) { 
        cm.dispose(); 
        return; 
    }else{ 
        status++; 
    } 
    if (status == 1) {
        cm.sendSimple ("You currently have #g"+cm.getReborns()+"#k Reborns.\r\nWhat would you like to receive?#k\r\n#b#L0#Medals\r\n#L1#Honor Level#k");
    }
    else if (status == 2) {
        if (selection == 0) {
        cm.sendSimple("Please choose the Medal that you would like to receive.\r\n#b#L102##i1142087# (1 Reborn)\r\n#L103##i1142088# (3 Reborns)\r\n#L104##i1142089# (6 Reborns)\r\n#L105##i1142090# (12 Reborns)\r\n#L106##i1142091# (24 Reborns)\r\n#L107##i1142092# (48 Reborns)\r\n#L108##i1142093# (100 Reborns)\r\n#L109##i1142094# (200 Reborns)\r\n#L110##i1142095# (350 Reborns)\r\n#L111##i1142096# (650 Reborns)\r\n#L112##i1142097# (900 Reborns)\r\n#L113##i1142098# (1200 Reborns)\r\n#L114##i1142099# (1536 Reborns)#k");
        }	
		if (selection == 1) {
        cm.sendSimple("#L200#Level Up My Honor Level (100 Reborns)");
        }
  } else if (status == 3) {
        var name = cm.getText();
        // Starting Mesos
		if (selection == 102) {
            if (cm.getReborns() >= 1) { 			
			    cm.gainItem(1142086, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 103) {
            if (cm.getReborns() >= 3) { 			
			    cm.gainItem(1142087, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 104) {
            if (cm.getReborns() >= 6) { 			
			    cm.gainItem(1142088, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 105) {
            if (cm.getReborns() >= 12) { 			
			    cm.gainItem(1142089, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 106) {
            if (cm.getReborns() >= 24) { 			
			    cm.gainItem(1142090, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 107) {
            if (cm.getReborns() >= 48) { 			
			    cm.gainItem(1142091, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 108) {
            if (cm.getReborns() >= 100) { 			
			    cm.gainItem(1142092, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 109) {
            if (cm.getReborns() >= 200) { 			
			    cm.gainItem(1142093, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 110) {
            if (cm.getReborns() >= 350) { 			
			    cm.gainItem(1142094, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 111) {
            if (cm.getReborns() >= 650) { 			
			    cm.gainItem(1142095, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 112) {
            if (cm.getReborns() >= 900) { 			
			    cm.gainItem(1142096, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 113) {
            if (cm.getReborns() >= 1200) { 			
			    cm.gainItem(1142097, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		if (selection == 114) {
            if (cm.getReborns() >= 1536) { 			
			    cm.gainItem(1142098, 1);
				cm.sendOk("You have received the item.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Reborns to receive this Medal.");
                cm.dispose();
            }
        }
		// End Medal
		// Start Honor Level Up
		if (selection == 200) {
		   if (cm.getReborns() >= 100) {
            cm.getPlayer().honourLevelUp();
            cm.dispose();
		} else {
		cm.sendOk("You do not have enough Reborns to level up your honor level.");
		cm.dispose();
		}
		}
		// End Honor Level Up
}
}