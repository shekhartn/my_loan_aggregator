/**                     
  *  @author :  f4fk53l 
  *  @Date   :  Jul 13, 2022 
  *  @Time   :  4:34:51 PM 
**/
package com.loan.aggregator.logmasking;

public class MaskingActionPattern {
    private String pattern;
    private MaskingAction action;
    private boolean isEnabled = true;

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {

        this.action = MaskingAction.fromActionValue(action);
    }

    public boolean isValid() {
        return !this.pattern.isBlank();
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return the action
     */
    public MaskingAction getAction() {
        return action;
    }

    public String apply(String message) {

        return message;
    }

}
