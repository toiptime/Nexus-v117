function start() {
    cm.sendSimple ("Hello #b#h ##k,\r\n\r\nWhat do you want me to do?\r\n\#b#L1#Max All My Skills (Free)\r\n\#L2#Max All My Skills By Job (Free)\r\n\#L3#Clear All My Skills (Free)\r\n#L4#Set My Unused Skill Points To 0 (Free)\r\n#L5#Teach Me GM Skills (500,000,000 Mesos)#k");
}

function action(mode, type, selection) {
    cm.dispose();
    if (selection == 1) {
        cm.maxAllSkills(); {
		}
		cm.sendOk("I have maxed all your Skills.");
      } else if (selection == 2) {
        cm.maxSkillsByJob(); {
		}
		cm.sendOk("I have maxed all your Skills by Job.");
      } else if (selection == 3) {
        cm.clearSkills(); {
    }
	cm.sendOk("I have cleared all your Skills.");
   } else if (selection == 4) {
    cm.getPlayer().resetSP(0); {
	}
	cm.sendOk("I have set your Skill Points to 0.");
	  }
	 else if (selection == 5) {
	 if (cm.getMeso() > 499999999) {
	cm.gainMeso(-500000000);
	cm.changeJob(900);
    cm.sendOk("Put the GM Skills to your Maple Keyboard before changing your Job.");	
} else { 
cm.sendOk("Come back to me when you have enough mesos");
}
	}
}