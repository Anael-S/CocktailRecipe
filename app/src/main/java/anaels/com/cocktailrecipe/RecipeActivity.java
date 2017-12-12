package anaels.com.cocktailrecipe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.widget.RecipeWidgetProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display the detail of a specific recipe
 */
public class RecipeActivity extends AppCompatActivity {

    DrinkRecipe mRecipe;
    Context mContext;
    ArrayList<DrinkRecipe> mRecipeList;

    public static final String KEY_INTENT_STEP = "keyIntentStep";
    public static final String KEY_INTENT_STEP_LIST = "keyIntentStepList";
    public static final String KEY_INTENT_RECIPE_NAME = "keyIntentRecipeName";

    @Nullable
    @BindView(R.id.fragmentStep)
    FrameLayout fragmentStep;

    RecipeFragment fragmentRecipe;
    int positionIngredientList;
    int positionStepList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        mContext = this;
        positionIngredientList = 0;
        positionStepList = 0;

        mRecipe = getIntent().getParcelableExtra(HomeActivity.KEY_INTENT_RECIPE);
        mRecipeList = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_RECIPE);

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(HomeActivity.KEY_INTENT_RECIPE);
            positionIngredientList = savedInstanceState.getInt(RecipeFragment.KEY_INTENT_POSITION_INGREDIENT_LIST);
            positionStepList = savedInstanceState.getInt(RecipeFragment.KEY_INTENT_POSITION_STEP_LIST);
        }

        //UI
        if (mRecipe != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(mRecipe.getStrDrink());
            }
        }

        //To avoid the double onCreateView claa, we check if this is just a screen roation
        if (savedInstanceState == null) {
            fragmentRecipe = new RecipeFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
            bundle.putParcelableArrayList(HomeActivity.KEY_INTENT_LIST_RECIPE, mRecipeList);
            bundle.putInt(RecipeFragment.KEY_INTENT_POSITION_INGREDIENT_LIST, positionIngredientList);
            bundle.putInt(RecipeFragment.KEY_INTENT_POSITION_STEP_LIST, positionStepList);
            fragmentRecipe.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentRecipe, fragmentRecipe).commit();
        }

        //If we're on a tablet && its not a screen rotation
        if (fragmentStep != null && savedInstanceState == null) {
            StepFragment fragmentStep = new StepFragment();
            Bundle bundle = new Bundle();
            bundle.putString(RecipeActivity.KEY_INTENT_STEP, mRecipe.getSteps().get(0));
            bundle.putStringArrayList(RecipeActivity.KEY_INTENT_STEP_LIST, new ArrayList<>(mRecipe.getSteps()));
            fragmentStep.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentStep, fragmentStep).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_to_widget_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addToWidgetButton) {
            //We send the recipe to the widget
            Toast.makeText(mContext, R.string.added_to_widget, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, RecipeWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
            int[] ids = AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext, RecipeWidgetProvider.class));
            if (ids != null && ids.length > 0) {
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                mContext.sendBroadcast(intent);
            }
        } else if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
        super.onSaveInstanceState(outState);
    }


}
