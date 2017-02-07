var status = 0;  
var selected = 1;  
var wui = 0;  


function start() {  
status = -1;  
action(1, 0, 0);  
}  


function action(mode, type, selection) {  
selected = selection;  
if (mode == -1) {  
cm.dispose();  
} else {  
if (mode == 0 && status == 0) {  
cm.dispose();  
return;  
}  
if (mode == 1)  
status++;  
else  
status--;  
if (status == 0) {  
cm.sendAcceptDecline("Hello #b#h ##k,\r\n\r\n#rPlease have these requirements to make a Max Stats Item: \r\n\r\n#b32,767 STR\r\n32,767 DEX\r\n32,767 INT\r\n32,767 LUK\r\n\r\nAs an Agent, I need a living. Therefore, there will be a 1,000,000 Mesos service fee for each Max Stats Item created for you.");  
} else if (status == 1) {  
if (cm.getPlayer().getStat().getStr() == 32767 && cm.getPlayer().getStat().getDex() == 32767 && cm.getPlayer().getStat().getInt() == 32767 && cm.getPlayer().getStat().getLuk() == 32767 && cm.getMeso() > 999999) {  
var String = "Please choose your desired item that you want as your new Max Stats Item. Please check your Inventory to make sure you have enough space because we don't give refunds.\r\n\r\n";  
cm.sendSimple(String+cm.EquipList(cm.getClient())); //isn't it cm.getPlayer().getClient() ? O.O 
} else {  
cm.sendOk ("You do not meet the requirements to make a Max Stats Item.");  
cm.dispose();   
}  
} else if (status == 2) { 
cm.gainMeso(-1000000);
cm.MakeGMItem(selected, cm.getPlayer());
cm.getPlayer().resetStats(4, 4, 4 , 4);
cm.reloadChar();
cm.dispose();  //I always put this on the bottom.. 
}  
}  
}  