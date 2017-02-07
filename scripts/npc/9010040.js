var name = "#b#eProject Nexus#k#n"; 
var talk = "What would you like to browse?\r\n\r\n"; 
var menu = ["Warrior","Magician","Archer","Thief","Pirate","Phantom","Cannoneer","Mercedes","Dual Blade"]; 
var options = [ 
/*Warrior*/   ["Hats","Tops","Bottoms","Overalls","Gloves","Shields","Shoes","One-Handed Axes","Two-Handed Axes","One-Handed Maces","Two-Handed Maces","One-Handed Swords","Two-Handed Swords","Spears","Pole Arms"],  
/*Mage*/      ["Hats","Overalls","Gloves","Shields","Shoes","Wands","Staffs"],  
/*Archer*/    ["Hats","Overalls","Gloves","Shoes","Bows","Crossbows","Arrows"],  
/*Thief*/     ["Hats","Tops","Bottoms","Overalls","Gloves","Shields","Shoes","Daggers","Claws","Throwing Stars"],  
/*Pirate*/    ["Hats","Overalls","Gloves","Shoes","Weapons","Bullets And Capsules"],
/*Phantom*/   ["Canes","Cards"],
/*Cannoneer*/ ["Cannons"],
/*Mercedes*/  ["Dual Bow Guns","Magic Arrows"],
/*db*/        ["Kataras"]];
var colors = ["#g","#r","#d","#b"]; 
var rand = Math.floor(Math.random()*4); 
var rand2 = Math.ceil(Math.floor(Math.random()*4)); 
var c; 
npc = 0; 
function start() { 
    var text = "Hello #b#h ##k,\r\n\r\nWhat would you like to browse?\r\n"; 
    for (var z = 0; z < menu.length; z++) 
        text+= "#L"+z+"##e"+colors[rand]+""+menu[z]+"#l\r\n"; 
    cm.sendSimple(text); 
} 
function action(m,t,s) { 
    if (m != 1) { 
        cm.dispose(); 
        return; 
    }else{ 
        npc++; 
    } 
    if (npc == 1) { 
        c = s; 
        for (var i = 0; i < options[c].length; i++) 
            talk+="#L"+i+"##e"+colors[rand2]+""+options[c][i]+"#k#l\r\n"; 
        cm.sendSimple(talk); 
    } else if (npc == 2) { 
        cm.openShop(6100+((c*100)+s)); 
        cm.dispose(); 
    } 
}  