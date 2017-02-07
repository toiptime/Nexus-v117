var status = 0;  
var selected = 1;  


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
cm.sendAcceptDecline("#gCongratulations on finding me#k.\r\n\r\n#bI can sell you Pink Leaf of Transformation at a price of 456,789 mesos#k.");  
} else if (status == 1) {  
if (cm.getMeso() > 456788) {   
cm.gainMeso(-456789);
cm.gainItem(4031584, 1);
cm.sendOk("#gEnjoy your Pink Leaf of Transformation#k.");
cm.dispose();  //I always put this on the bottom.. 
} else {
cm.sendOk("You do not have enough mesos."); 
cm.dispose();
}  
}
}  
}