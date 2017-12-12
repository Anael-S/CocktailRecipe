package anaels.com.cocktailrecipe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import anaels.com.cocktailrecipe.HomeActivity;
import anaels.com.cocktailrecipe.R;
import anaels.com.cocktailrecipe.RecipeActivity;
import anaels.com.cocktailrecipe.api.model.Ingredient;
import anaels.com.cocktailrecipe.api.model.Recipe;
import anaels.com.cocktailrecipe.helper.StepHelper;


/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static Recipe mRecipe;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        //OnClick default
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);


        if (mRecipe == null) {
            views.setTextViewText(R.id.titleRecipeTextView, context.getString(R.string.choose_recipe));
            views.setViewVisibility(R.id.addToWidgetIcon, View.VISIBLE);
            views.setViewVisibility(R.id.layoutIngredient, View.GONE);
        } else {
            //name
            views.setTextViewText(R.id.titleRecipeTextView, mRecipe.getName());

            //Visibility
            views.setViewVisibility(R.id.addToWidgetIcon, View.GONE);
            views.setViewVisibility(R.id.layoutIngredient, View.VISIBLE);

            //ListView ingredients
            views.removeAllViews(R.id.listViewIngredientsRecipes);
            for (Ingredient ingredient : mRecipe.getIngredients()) {
                RemoteViews rvIngredient = new RemoteViews(context.getPackageName(), R.layout.row_ingredient_widget);
                rvIngredient.setTextViewText(R.id.tv_recipe_widget_ingredient_item, StepHelper.formatQuantityForDisplay(ingredient.getQuantity()) +
                        String.valueOf(ingredient.getMeasure().toLowerCase()) + " " + ingredient.getIngredient());
                views.addView(R.id.listViewIngredientsRecipes, rvIngredient);
            }

            //OnClick
            Intent intentRecipe = new Intent(context, RecipeActivity.class);
            intentRecipe.putExtra(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
            PendingIntent pendingIntentRecipe = PendingIntent.getActivity(context, 0, intentRecipe, 0);
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntentRecipe);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            mRecipe = intent.getExtras().getParcelable(HomeActivity.KEY_INTENT_RECIPE);
            //update all widgets
            onUpdate(context, appWidgetManager, appWidgetIds);
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

