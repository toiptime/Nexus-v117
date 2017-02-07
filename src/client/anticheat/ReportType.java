/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.anticheat;

public enum ReportType {
    Advertising(0, "Advertise"),
    Spamming(1, "Spam"),
    Trolling(2, "Troll"),
    Scamming(3, "Scam"),
    Disrespecting(4, "Disrespect"),
    Flamming(5, "Flame"),
    Misusing(6, "Misuse"),
    Racist(7, "Racist"),
    Hacking(8, "Hack");

    public byte i;
    public String theId;

    ReportType(int i, String theId) {
        this.i = (byte) i;
        this.theId = theId;
    }

    public static ReportType getById(int z) {
        for (ReportType t : ReportType.values()) {
            if (t.i == z) {
                return t;
            }
        }
        return null;
    }

    public static ReportType getByString(String z) {
        for (ReportType t : ReportType.values()) {
            if (z.contains(t.theId)) {
                return t;
            }
        }
        return null;
    }
}