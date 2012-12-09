/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * @author Laurent
 */
public enum Keys {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN,
    LMB,
    MMB,
    RMB;

    public static Keys fromOrdinal(int count) {
        if(count>0 && count<values().length) {
            return values()[count];
        }
        return FORWARD;
    }
}
