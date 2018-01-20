package anaels.com.cocktailrecipe.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;

public class IngredientsViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<String> items;
    private Context ctxt = null;
    private int appWidgetId;

    public IngredientsViewsFactory(Context ctxt, Intent intent, ArrayList<String> items) {
        this.ctxt = ctxt;
        this.items=items;
        appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart())
                - RecipeWidgetProvider.randomNumber;
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return (items.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.row_ingredient_widget);

        row.setTextViewText(R.id.tv_recipe_widget_ingredient_item, items.get(position));

        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }
}