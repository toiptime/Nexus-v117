var status; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (mode == 1) 
        status++; 
    else { 
        cm.sendOk("#e#kCome back to me when you wish to exchange your Vote Points for my items."); 
        cm.dispose(); 
        return; 
    } 
                    if (status == 0) { 
        cm.sendSimple ("Hello #b#h ##k,\r\n\r\nYou currently have #g" + cm.getVPoints() + "#k Vote Points.\r\n" +  
                "\r\n#L1##kHow To Get Vote Points" + 
                "\r\n#L2##kExchange Vote Points For Reward"); 
                     } else if (selection == 1) { 
                      cm.sendOk("You can get 1 Vote Point from GTop100 and Ultimate Private Servers every 24 hours by voting at: http://Project Nexus.net/?base=main&page=vote"); 
                      cm.dispose(); 
                     } else if (selection == 2) { 
                cm.sendSimple ("#eWhat would you like to exchange for?"+ 
                 "#k\r\n#L4##bNX#k" +
				 "#k\r\n#L5##bSuper Miracle Cubes#k"); 
                     } else if (selection == 4) { 
        cm.sendSimple ("Please choose the amount of NX that you would like to exchange:" + 
                 "\r\n#L8##b500 NX (1 Vote Point)" + 
                 "\r\n#L9#1,000 NX (2 Vote Points)" + 
                 "\r\n#L10#2,000 NX (4 Vote Points)" + 
                 "\r\n#L11#5,000 NX (10 Vote Points)" + 
                 "\r\n#L12#10,000 NX (20 Vote Points)#k"); 
        } else if (selection == 5) { 
               cm.sendSimple ("Please choose the amount of Super Miracle Cubes that you would like to exchange:" +  
            "\r\n#L23##b5 (2 Vote Points)" +  
            "\r\n#L24#10 (4 Vote Points)" +  
            "\r\n#L25#25 (10 Vote Points)" +  
            "\r\n#L26#50 (20 Vote Points)#k"); 
                    } else if (selection == 8) { 
                var price = 5000000; 
                if (cm.getVPoints() > 0) {       
                    cm.setVPoints(-1);                     
                   cm.gainNXCredit(1000);
                   cm.dispose(); 
                     } else { 
                   cm.sendOk ("You do not have enough Vote Points to exchange for 500 NX."); 
                   cm.dispose(); 
                   } 
                } else if (selection == 9) { 
                var price = 10000000; 
                if (cm.getVPoints() > 1) {       
                    cm.setVPoints(-2);                     
                   cm.gainNXCredit(2000); 
                   cm.dispose(); 
                     } else { 
                   cm.sendOk ("You do not have enough Vote Points to exchange for 1,000 NX."); 
                   cm.dispose(); 
                   } 
                } else if (selection == 10) { 
                var price = 15000000; 
                if (cm.getVPoints() > 3) {       
                    cm.setVPoints(-4);                     
                   cm.gainNXCredit(4000);
                   cm.dispose(); 
                     } else { 
                   cm.sendOk ("You do not have enough Vote Points to exchange for 2,000 NX."); 
                   cm.dispose(); 
                   } 
                } else if (selection == 11) { 
                var price = 20000000; 
                if (cm.getVPoints() > 9) {       
                    cm.setVPoints(-10);                     
                   cm.gainNXCredit(10000); 
                   cm.dispose(); 
                     } else { 
                   cm.sendOk ("You do not have enough Vote Points to exchange for 5,000 NX."); 
                   cm.dispose(); 
                   } 
                } else if (selection == 12) { 
                if (cm.getVPoints() > 19) {       
                    cm.setVPoints(-20);                     
                   cm.gainNXCredit(20000); 
                   cm.dispose(); 
                     } else { 
                   cm.sendOk ("You do not have enough Vote Points to exchange for 10,000 NX."); 
                   cm.dispose(); 
} 
}  
else if (selection == 23) { 
                if (cm.getVPoints() > 1) {    
                    cm.setVPoints(-2);  
        cm.gainItem(5062002, 5); 
        cm.sendOk("Enjoy your Super Miracle Cubes"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough Vote Points to exchange for 5 Super Miracle Cubes.") 
        cm.dispose(); 
        } 
    } 
else if (selection == 24) { 
                if (cm.getVPoints() > 3) {    
                    cm.setVPoints(-4);  
        cm.gainItem(5062002, 10); 
        cm.sendOk("Enjoy your Super Miracle Cubes"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough Vote Points to exchange for 10 Super Miracle Cubes.") 
        cm.dispose(); 
        } 
    } 
else if (selection == 25) { 
                if (cm.getVPoints() > 9) {    
                    cm.setVPoints(-10);  
        cm.gainItem(5062002, 25); 
        cm.sendOk("Enjoy your Super Miracle Cubes"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough Vote Points to exchange for 25 Super Miracle Cubes.") 
        cm.dispose(); 
        } 
    } 
else if (selection == 26) { 
                if (cm.getVPoints() > 19) {    
                    cm.setVPoints(-20);  
        cm.gainItem(5062002, 50); 
        cm.sendOk("Enjoy your Super Miracle Cubes"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough Vote Points to exchange for 50 Super Miracle Cubes.") 
        cm.dispose(); 
        } 
    }  
    }  