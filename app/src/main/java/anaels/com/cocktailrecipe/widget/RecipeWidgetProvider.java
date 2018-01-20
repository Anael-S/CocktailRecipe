package anaels.com.cocktailrecipe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import anaels.com.cocktailrecipe.HomeActivity;
import anaels.com.cocktailrecipe.R;
import anaels.com.cocktailrecipe.RecipeActivity;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;


/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static DrinkRecipe mRecipe;
    static int randomNumber;

    protected static final String KEY_INTENT_RECIPE_INGREDIENT_WIDGET = "keyIntentRecipeIngredientWidget";
    protected static final String KEY_INTENT_RECIPE_STEP_WIDGET = "keyIntentRecipeStepWidget";

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
            views.setViewVisibility(R.id.recipeLayout, View.GONE);
        } else {
            //name
            views.setTextViewText(R.id.titleRecipeTextView, mRecipe.getStrDrink());

            //Visibility
            views.setViewVisibility(R.id.addToWidgetIcon, View.GONE);
            views.setViewVisibility(R.id.recipeLayout, View.VISIBLE);


            //ListView ingredients
            randomNumber=(int)(Math.random()*1000);
            Intent intentIngredients=new Intent(context, IngredientService.class);
            intentIngredients.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intentIngredients.setData(Uri.fromParts("content", String.valueOf(appWidgetId+randomNumber), null));
            intentIngredients.putExtra(KEY_INTENT_RECIPE_INGREDIENT_WIDGET,mRecipe.getIngredients());
            views.setRemoteAdapter(R.id.listViewIngredientsRecipes, intentIngredients);

            //ListView steps
            randomNumber=(int)(Math.random()*1000);
            Intent intentSteps=new Intent(context, StepService.class);
            intentSteps.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intentSteps.setData(Uri.fromParts("content", String.valueOf(appWidgetId+randomNumber), null));
            intentSteps.putExtra(KEY_INTENT_RECIPE_STEP_WIDGET,mRecipe.getSteps());
            views.setRemoteAdapter(R.id.listViewStepsRecipes, intentSteps);

            //OnClick
            Intent intentRecipe = new Intent(context, RecipeActivity.class);
            intentRecipe.putExtra(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
            PendingIntent pendingIntentRecipe = PendingIntent.getActivity(context, 0, intentRecipe, PendingIntent.FLAG_UPDATE_CURRENT);
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

