package anaels.com.cocktailrecipe.helper;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.Step;


/**
 * Created by Anael on 11/13/2017.
 */
public class StepHelper {

    public static int getSelectStepPosition(ArrayList<Step> listStep){
        int lastSelectedStep=0;
        boolean isStepSelected = false;
        if (listStep != null) {
            for (Step lStep : listStep) {
                if (lStep != null && lStep.isSelected()) {
                    isStepSelected = true;
                    break;
                }
                lastSelectedStep++;
            }
            if (!isStepSelected) {
                lastSelectedStep = 0;
            }
        }
        return lastSelectedStep;
    }

    public static String formatQuantityForDisplay(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
}
