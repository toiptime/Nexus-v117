/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import constants.GameConstants;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Itzik
 */
public class getShopItemData {

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        Scanner input = new Scanner(System.in);
        byte[] data;
        // Loading Data
        System.out.println("Print in the packet data");
        data = HexTool.getByteArrayFromHexString(input.next());

        FileOutputStream out = new FileOutputStream("logs/getShopItemData.txt", false);
        System.out.println("\r\n\r\n");

        //Parsing Data
        final LittleEndianAccessor slea = new LittleEndianAccessor(new ByteArrayByteStream(data));
        slea.readShort(); // Header
        slea.readInt(); // 0
        int shopid = slea.readInt(); // Shop Id (in gms, npc id is shop id so....)
        boolean ranks = slea.readByte() == 1; // 0 = no ranks 1 = ranks
        int ranksize = 0;
        int rank = 0;
        String rankmsg = null;
        if (ranks) {
            ranksize = slea.readByte();
            for (int r = 0; r < ranksize; r++) {
                rank = slea.readInt();
                rankmsg = slea.readMapleAsciiString();
            }
        }
        short itemsize = slea.readShort(); // Items in shop + Rebuy items
        // Shop Items
        // TODO: Create loop here
        int itemid = slea.readInt();
        int price = slea.readInt();
        slea.readByte(); // 0
        int reqItem = slea.readInt();
        int reqItemQuantity = slea.readInt();
        int expiration = slea.readInt();
        int minLevel = slea.readInt();
        int category = slea.readInt();
        slea.readByte(); // 0
        slea.readInt(); // 0
        slea.readInt(); // 0
        if ((!GameConstants.isThrowingStar(itemid)) && (!GameConstants.isBullet(itemid))) {
            slea.readShort(); // Always 1
            slea.readShort(); // Always 1000 (might be recharge price)
        } else {
            slea.readInt(); // 0
            slea.readShort(); // 0
            slea.readShort(); // Price? might be recharge price
            short itemsPerSlot = slea.readShort();
        }
        //Building the String

        //not finished yet

        //SQL Script
        sb.append("SQL:");
        sb.append("shops:");
        sb.append("INSERT too lazy to continue... VALUES (").append(shopid).append(", ").append(shopid).append(");");
        sb.append("shopitems:");
        for (int i = 1; i < itemsize; i++) {
            sb.append("INSERT TO shopitems blah blah VALUES(").append(shopid).append(", ").append(itemid).append(", ").append(price).append(", ").append(i).append(", ").append(reqItem).append(", ").append(reqItemQuantity).append(", ").append(rank).append("),");
        }
        sb.append("INSERT TO shopitems blah blah VALUES(").append(shopid).append(", ").append(itemid).append(", ").append(price).append(", ").append(itemsize).append(", ").append(reqItem).append(", ").append(reqItemQuantity).append(", ").append(rank).append(");");
        //Writing into the file
        out.write(sb.toString().getBytes());
    }
}