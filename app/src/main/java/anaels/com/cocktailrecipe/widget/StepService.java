package anaels.com.cocktailrecipe.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Service used to populate the listview of step in the widget
 */
public class StepService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> stepList = new ArrayList<>();
        stepList = intent.getStringArrayListExtra(RecipeWidgetProvider.KEY_INTENT_RECIPE_STEP_WIDGET);
        return (new StepsViewsFactory(this.getApplicationContext(), intent, stepList));
    }
}