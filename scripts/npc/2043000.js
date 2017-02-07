/* 	Item Exchange NPC
	Coded by Quiet_Crystal
*/

var status = 0;
var selectedItem = -1;
var totalMesoCost = 0;
var canBuy = true;

var onSale = [
	[4032308, [ //   === Pink Candle ===
		[4033268, 1], //  Pink Anthurium x1
		[4031989, 1], // Pink Water Drop x1
		[4031584, 1], // Pink Leaf of Transformation x1
		[4001097, 1]]], // Pink Primrose Seed x1
	[4001249, [ //   === Maple Necklace Gem ===
		[4032233, 1], //   Maple Necklace Chain x1
		[4032239, 1], // Piece of Mind of Maple x1
		[4032234, 1], // Piece of Courage x1
		[4032235, 1], // Piece of Wisdom  x1
		[4032236, 1], // Piece of Accuracy x1
		[4032237, 1], // Piece of Dexterity x1
		[4032238, 1]]], // Piece of Freedom x1
	[4031124, [ // === Strawberry ===
		[2000000, 1], // Red Potion x1
		[2002017, 1], // Warrior Elixir x1
		[2002024, 1], // Sorcerer Elixir x1
		[2010000, 1], // Apple x1
		[2001000, 1]]], // Watermelon x1
		[4000138, [ //   === Lady Boss's Comb ===
		[-1, 50000]]], // 50,000 Mesos
		[4032246, [ //   === Spirit of Fantasy Theme Park ===
		[-1, 150000]]], // 150,000 Mesos
		[4001017, [ //   === Eye of Fire ===
		[-1, 200000]]], // 200,000 Mesos
		[4032002, [ // === Marble of Chaos ===
		[-1, 300000]]], // 300,000 Mesos
		[3010180, [ // === Giant HP Bottle ===
		[2000000, 10000]]], // Red Potion x10000
		[3010181, [ // === Giant MP Bottle ===
		[2000003, 10000]]], // Blue Potion x10000
		[3010095, [ // === Stone Golem Chair ===
		[4000022, 1000]]], // Stone Golem Rubble x1000
		[3010111, [ // === Tiger Skin Chair ===
		[4000171, 1000]]], // Tiger Skin x1000
		[3010301, [ // === Elixir Chair ===
		[2000004, 10000]]], // x10000 Elixir
		[3010302, [ // === Power Elixir Chair ===
		[2000005, 10000]]], // Power Elixir x10000
		[3010177, [ // === Gamepad Chair ===
		[4032712, 100]]], // Alien Chip x100
		[3010224, [ // === Mochi Ice Cream Chair ===
		[2010004, 100], // Lemon x100
		[2022000, 100], // Pure Water x100
		[2020013, 100]]], // Reindeer Milk x100
		[3010040, [ // === The Stirge Seat ===
		[4000042, 1000]]] // Stirge Wing x1000
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