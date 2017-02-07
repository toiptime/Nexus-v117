function start() {
    cm.sendSimple("Hello #b#h ##k,\r\n\r\nDo you wish to buy some NX?\r\n\r\n#d#L0#Buy 1,000 NX For 3,000,000 Mesos#l\r\n#L1#Buy 3,000 NX For 9,000,000 Mesos#l\r\n#L2#Buy 5,000 NX For 15,000,000 Mesos#l\r\n#L3#Buy 10,000 NX For 30,000,000 Mesos#l\r\n#L4#Buy 20,000 NX For 60,000,000 Mesos#l\r\n#L5#Buy 50,000 NX For 150,000,000 Mesos#l\r\n#L6#Buy 100,000 NX For 300,000,000 Mesos#l#k");
}

function action(mode, type, selection) {
    if (mode < 1) {
        cm.dispose();
        return;
    } else if (selection == 0) {
        if (cm.getMeso() >= 3000000) { 
                cm.sendOk("You have bought 1,000 NX.");
                cm.gainMeso(-3000000);
                cm.gainNXCredit(2000); // 2000 / 2 = 1000 NX
            } else {
                cm.sendOk("You don't have enough mesos to buy NX.");
				cm.dispose();
            } 
			}
		else if (selection == 1) {
		if (cm.getMeso() >=9000000) {
		cm.gainMeso(-9000000);
		cm.sendOk("You have bought 3,000 NX.");
		cm.gainNXCredit(6000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		else if (selection == 2) {
		if (cm.getMeso() >=15000000) {
		cm.gainMeso(-15000000);
		cm.sendOk("You have bought 5,000 NX.");
		cm.gainNXCredit(10000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		else if (selection == 3) {
		if (cm.getMeso() >=30000000) {
		cm.gainMeso(-30000000);
		cm.sendOk("You have bought 10,000 NX.");
		cm.gainNXCredit(20000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		else if (selection == 4) {
		if (cm.getMeso() >=60000000) {
		cm.gainMeso(-60000000);
		cm.sendOk("You have bought 20,000 NX.");
		cm.gainNXCredit(40000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		else if (selection == 5) {
		if (cm.getMeso() >=150000000) {
		cm.gainMeso(-150000000);
		cm.sendOk("You have bought 50,000 NX.");
		cm.gainNXCredit(100000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		else if (selection == 6) {
		if (cm.getMeso() >=300000000) {
		cm.gainMeso(-300000000);
		cm.sendOk("You have bought 100,000 NX.");
		cm.gainNXCredit(200000);
		} else {
		cm.sendOk("You don't have enough mesos to buy NX.");
		cm.dispose();
		}
		}
		cm.dispose();
    }