var status;

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
cm.sendAcceptDecline("You have completed the Jumping Quest. Click accept to receive #g2 Jumping Quest Points#k.");
}else if (status == 1) {
if (cm.haveItem(4032024, 1)) {
 var map = cm.getPlayer().getMapId();
 var mapname = "Unknown MapID";
 if (map == 910530100) {
 mapname = "Forest of Tenacity : Stage 3 - 4";
} else if (map == 910130000) {
 mapname = "Forest of Endurance : Stage 1 - 2"
}
    cm.warp(910000000,0);
	cm.gainItem(4032024, -1);
	cm.gainJQPoints(2);
	cm.dispose();
} else {
	cm.msiMessage("[Jumping Quest Notice] Congratulations to "+cm.getPlayer().getName()+" for trying to beat the Jumping Quest without the required item.");
	cm.dispose();
}
}
}