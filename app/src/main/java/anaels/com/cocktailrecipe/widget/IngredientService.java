package anaels.com.cocktailrecipe.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Service used to populate the listview of ingredient in the widget
 */
public class IngredientService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> ingredientList = new ArrayList<>();
        ingredientList = intent.getStringArrayListExtra(RecipeWidgetProvider.KEY_INTENT_RECIPE_INGREDIENT_WIDGET);
        return (new IngredientsViewsFactory(this.getApplicationContext(), intent, ingredientList));
    }
}