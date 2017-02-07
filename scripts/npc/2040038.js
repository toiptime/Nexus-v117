/* 	Item Exchange NPC
	Coded by Quiet_Crystal
*/

var status = 0;
var selectedItem = -1;
var totalMesoCost = 0;
var canBuy = true;

var onSale = [
	[1012181, [ //   === Pink Candle ===
		[4001429, 5]]], // Mardi Gras Bead Necklace
	[1012182, [ //   === Maple Necklace Gem ===
		[4001429, 10]]], // Mardi Gras Bead Necklace
	[1012183, [ // === Strawberry ===
		[4001429, 15]]], // Mardi Gras Bead Necklace
		[1012184, [ //   === Lady Boss's Comb ===
		[4001429, 20]]], // Mardi Gras Bead Necklace
		[1012185, [ //   === Piece of Crack Dimension ===
		[4001429, 25]]], // Mardi Gras Bead Necklace
		[1012186, [ //   === Spirit of Fantasy Theme Park ===
		[4001429, 30]]] // Mardi Gras Bead Necklace
];

function start() {
    var selStr = "Hello #b#h ##k,\r\n\r\nWhat would you like to exchange?\r\n\r\n#b#k";
    for (var i = 0; i < onSale.length; i++){
        selStr += "\r\n#L" + i + "# #i" + onSale[i][0] + "# - #t" + onSale[i][0] + "# #l\r\n";
    }
    cm.sendSimple(selStr);
}

function action(mode, type, selection) {
    if (mode > 0) {
        status++;
	} else if (mode == 0) {
		cm.sendOk("Come back to me if you wish to exchange for an item.");
		cm.dispose();
        return;
	} else {
        cm.dispose();
        return;
    }
    if (status == 1) {
        selectedItem = selection;
        var selStr = "I can exchange my #b#t" + onSale[selectedItem] + "##k for your following items:\r\n";
		for (var i = 0; i < onSale[selectedItem][1].length; i++){
			if (onSale[selectedItem][1][i][0] == -1) {
				selStr += "\r\n#d" + onSale[selectedItem][1][i][1] + " Mesos#k";
			} else {
				selStr += "\r\n" + onSale[selectedItem][1][i][1] + "x #i" + onSale[selectedItem][1][i][0] + "# - #d#t" + onSale[selectedItem][1][i][0] + "##k\r\n";
			}
		}
		selStr += "\r\n\r\nDo you want to exchange your items for my #b#t" + onSale[selectedItem] + "##k?";
    	cm.sendYesNo(selStr);
    } else if (status == 2) {
		for (var i = 0; i < onSale[selectedItem][1].length && canBuy; i++){
			if (onSale[selectedItem][1][i][0] == -1) {
				totalMesoCost += onSale[selectedItem][1][i][1]; // In case some retards attempt to put 2 entries of meso cost for the same object.
				canBuy = canBuy && cm.getMeso() >= totalMesoCost;
			} else {
				canBuy = canBuy && cm.haveItem(onSale[selectedItem][1][i][0], onSale[selectedItem][1][i][1]);
			}
		}
		if (canBuy) {
			for (var i = 0; i < onSale[selectedItem][1].length; i++){
				if (onSale[selectedItem][1][i][0] == -1) {
					cm.gainMeso(-onSale[selectedItem][1][i][1]);
				} else {
					cm.gainItem(onSale[selectedItem][1][i][0], -onSale[selectedItem][1][i][1]);
				}
			}
			cm.gainItem(onSale[selectedItem][0], 1);
			cm.sendOk("Enjoy your new item.");
			cm.dispose();
        	return;
		} else {
			cm.sendOk("Please come back to me if you have the items.");
			cm.dispose();
        	return;
		}
    }
}