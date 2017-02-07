var chosen = 1;
var Error;
var one;
var c; 
status = 0; 
function start() {
	
    cm.sendAcceptDecline("Hello #b#h ##k,\r\n\r\nI am the Donation NPC for #dProject Nexus#k.\r\n\r\n#bWhen you donate, you can get various rewards as a thank you gift from us!\r\n\r\nDonor Benefits:\r\n1) Donor Command\r\n2) Green Chat\r\n3) Forum Donor Rank\r\n4) Access To Donor Events\r\n5) Weekly And Monthly Lucky Draw\r\n6) Access To Donor Map\r\n7) Add Warp To Another Map Command On Request\r\n8) 2x EXP Card (1024x)\r\n\r\nSuper Donor Benefits:\r\nIncludes Everything From Donor\r\n1) Access To Super Donor Events\r\n2) Weekly And Monthly Lucky Draw\r\n3) Unlimited Power Elixirs\r\n4) Unlimited 2x EXP Coupons\r\n5) Pendant of the Spirit\r\n6) GM Skills\r\n7) 3x EXP Card (1536x)\r\n\r\nUltra Donor Benefits:\r\nIncludes Everything From Super Donor\r\nAccess To Everything\r\nAdd Features On Request\r\n\r\nUltra Donor Information: You won't be able to give your items to other players.#k\r\n\r\n#rFor more information, please check out the forums.#k");
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
        cm.sendSimple ("You currently have #g"+cm.getDPoints()+"#k Donor Points.\r\nWhat would you like to exchange?#k\r\n#b#L0#Mesos\r\n#L1#Name Change (2 Donor Points)\r\n#L2#Rares\r\n#L3#Donor Status\r\n#L4#Custom#k");
    }
    else if (status == 2) {
        if (selection == 0) {
        cm.sendSimple("Please choose the amount of Mesos that you wish to exchange.\r\n#b#L100#60,000,000 Mesos (1 Donor Point)\r\n#L101#180,000,000 Mesos (3 Donor Points)\r\n#L102#300,000,000 Mesos (5 Donor Points)\r\n#L103#600,000,000 Mesos (10 Donor Points)\r\n#L104#1,200,000,000 Mesos (20 Donor Points)#k");
        }	
        else if (selection == 1) { // Name Changer
            if (cm.getDPoints() > 1) {  
                cm.sendGetText("#rNo Special Characters Are Allowed#k\r\nMy new name is:");
            }
        }
		else if (selection == 2) {
		cm.sendSimple("Please choose the item that you wish to exchange.\r\n#b#L400##i1122017# (10 Donor Points)\r\n#L401##i2450059# [10 x 1 Hour] (1 Donor Point)\r\n#L402##i2450041# [6 x 1 Hour] (1 Donor Point)\r\n#L403##i2450043# [3 x 1 Hour] (1 Donor Point)#k");
		}
		else if (selection == 3) {
		cm.sendOk("#bMessage Shiro to exchange your Donor Points for Donor Status.\r\n\r\nDonor (25 Donor Points)\r\nSuper Donor (100 Donor Points)\r\nUltra Donor (200 Donor Points)#k");
		cm.dispose();
		}
		else if (selection == 4) {
		cm.sendOk("#bMax Stats Item (30 Donor Points)\r\n\r\nNeed an item?#k");
		cm.dispose();
		}
  } else if (status == 3) {
        var name = cm.getText();
        // Starting Mesos
		if (selection == 100) {
            if (cm.getDPoints() > 0) { 
                cm.setDPoints(-1);			
                cm.gainMeso(60000000);
				cm.sendOk("You have exchanged for #b60,000,000 Mesos#k.");
				cm.dispose();
            } else {
                cm.sendOk("You do not have enough Donor Points to exchange for #b60,000,000 Mesos#k.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
                cm.dispose();
            }
        }
		if (selection == 101) {
            if (cm.getDPoints() > 2) { 
                cm.setDPoints(-3);			
                cm.gainMeso(180000000);
				cm.sendOk("You have exchanged for #b180,000,000 Mesos#k.");
				cm.dispose();
            } else  {
                cm.sendOk("You do not have enough Donor Points to exchange for #b180,000,000 Mesos#k.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
                cm.dispose();
            }
        }
		if (selection == 102) {
            if (cm.getDPoints() > 4) { 
                cm.setDPoints(-5);			
                cm.gainMeso(300000000);
				cm.sendOk("You have exchanged for #b300,000,000 Mesos#k.");
				cm.dispose();
            } else  {
                cm.sendOk("You do not have enough Donor Points to exchange for #b300,000,000 Mesos#k.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
                cm.dispose();
            }
        }
		if (selection == 103) {
            if (cm.getDPoints() > 9) { 
                cm.setDPoints(-10);			
                cm.gainMeso(600000000);
				cm.sendOk("You have exchanged for #b600,000,000 Mesos#k.");
				cm.dispose();
            } else  {
                cm.sendOk("You do not have enough Donor Points to exchange for #b600,000,000 Mesos#k.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
                cm.dispose();
            }
        }
		if (selection == 104) {
            if (cm.getDPoints() > 19) { 
                cm.setDPoints(-20);			
                cm.gainMeso(1200000000);
				cm.sendOk("You have exchanged for #b1,200,000,000 Mesos#k.");
				cm.dispose();
            } else  {
                cm.sendOk("You do not have enough Donor Points to exchange for #b1,200,000,000 Mesos#k.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
                cm.dispose();
            }
        }
		// End Mesos
        //Start Of Rare
	else if (selection == 400) {
	if (cm.getDPoints() > 9) {
	cm.setDPoints(-10);
	cm.gainItem(1122017, 1);
	cm.sendOk("You have exchanged for #i1122017#.");
	cm.dispose();
	} else {
	cm.sendOk("You do not have enough Donor Points to exchange for #i1122017#.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
	cm.dispose();
	}
}
        //End Of Rare
		//Start Of Exp Coupon
		else if (selection == 401) {
		if (cm.getDPoints() > 0) {
		cm.setDPoints(-1);
		cm.gainItem(2450059, 10);
		cm.sendOk("You have exchanged for #d10#k #i2450059#.");
		cn.dispose();
		} else {
		cm.sendOk("You do not have enough Donor Points to exchange for #i2450059#.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
		cm.dispose();
		}
	}
	else if (selection == 402) {
		if (cm.getDPoints() > 0) {
		cm.setDPoints(-1);
		cm.gainItem(2450041, 6);
		cm.sendOk("You have exchanged for #d6#k #i2450041#.");
		cn.dispose();
		} else {
		cm.sendOk("You do not have enough Donor Points to exchange for #i2450041#.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
		cm.dispose();
		}
	}
	else if (selection == 403) {
		if (cm.getDPoints() > 0) {
		cm.setDPoints(-1);
		cm.gainItem(2450043, 3);
		cm.sendOk("You have exchanged for #d3#k #i2450043#.");
		cn.dispose();
		} else {
		cm.sendOk("You do not have enough Donor Points to exchange for #i2450043#.\r\nYou currently have #r"+cm.getDPoints()+" Donor Points#k.");
		cm.dispose();
		}
	}
		//End Of Exp Coupon		
		else {
            //Name Changer
            if (name != null) {
                if(name.contains(" ")) {
                    cm.sendOk("Your name contains a space in it, please enter a name without a space");
                    cm.dispose();
                } else {
                    if (cm.isValid(name) == true) {
                        if (cm.ifNameExist(name) == false) {
                            //Changing the name here
                            cm.setDPoints(-2); 
                            cm.setName(name);
                            cm.dispose();
                        }else {
                            cm.sendOk("The name you entered is already exist.");
                            cm.dispose();
                        }
                    }else {
                        cm.sendOk("You have entered a name that is with special character.");
                        cm.dispose();
                    }
                }
            }
}
}
}