/**                     
  *  @author :  f4fk53l 
  *  @Date   :  Jul 13, 2022 
  *  @Time   :  4:54:59 PM 
**/                      
package com.loan.aggregator.logmasking;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public enum MaskingAction {

    

    MASK_STAR {
        @Override
        String mask(String valueToMask) {
             return StringUtils.repeat("*", valueToMask.length());
        }
                            },
    SHA_256 {
        
        @Override
        String mask(String valueToMask) {
            
           return digestUtils.digestAsHex(valueToMask.strip());
        }
    },REMOVE {
        @Override
        String mask(String valueToMask) {
            return "";
        }
    };

    DigestUtils digestUtils = new DigestUtils("SHA-256");
    abstract String mask(String valueToMask);

    public static MaskingAction fromActionValue(String value){
        for(MaskingAction action : values())
        {
            if(action.name().equalsIgnoreCase(value)){
                return action;
            }
        }
        return MASK_STAR;
    }

    
}
