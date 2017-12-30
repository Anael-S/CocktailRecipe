package anaels.com.cocktailrecipe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.CocktailApiHelper;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.helper.FavoriteHelper;
import anaels.com.cocktailrecipe.persistence.RecipeContract;
import anaels.com.cocktailrecipe.persistence.RecipesDBHelper;
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
    private final String LOG_TAG = "RecipeDB";

    public static final String KEY_INTENT_STEP = "keyIntentStep";
    public static final String KEY_INTENT_STEP_LIST = "keyIntentStepList";
    public static final String KEY_INTENT_RECIPE_NAME = "keyIntentRecipeName";

    @Nullable
    @BindView(R.id.fragmentStep)
    FrameLayout fragmentStep;
    @BindView(R.id.recipeImageView)
    ImageView recipeImageView;
    @BindView(R.id.favoriteImageView)
    ImageView favoriteImageView;


    RecipeFragment fragmentRecipe;
    Parcelable positionIngredientList;
    Parcelable positionStepList;

    private ArrayList<DrinkRecipe> listFavRecipe;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        mContext = this;

        mRecipe = getIntent().getParcelableExtra(HomeActivity.KEY_INTENT_RECIPE);
        mRecipeList = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_RECIPE);

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(HomeActivity.KEY_INTENT_RECIPE);
            positionIngredientList = savedInstanceState.getParcelable(RecipeFragment.KEY_INTENT_POSITION_INGREDIENT_LIST);
            positionStepList = savedInstanceState.getParcelable(RecipeFragment.KEY_INTENT_POSITION_STEP_LIST);
        }
        listFavRecipe = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_FAV_RECIPE);
        if (listFavRecipe == null) {
            listFavRecipe = new ArrayList<>();
        }

        //We don't have to load it
        if (mRecipe.getDateModified() != null) {
            loadUI(savedInstanceState);
        } else {
            //We load the recipe
            CocktailApiHelper.searchCocktailById(this, mRecipe.getIdDrink(), new CocktailApiHelper.OnCocktailRecipeRecovered() {
                @Override
                public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> drinkRecipeList) {
                    mRecipe = drinkRecipeList.get(0);
                    loadUI(savedInstanceState);
                }
            }, new CocktailApiHelper.OnError() {
                @Override
                public void onError() {
                    Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            });
        }

        //favorite
        if (listFavRecipe.contains(mRecipe)){
            favoriteImageView.setImageResource(R.drawable.filled_star);
        } else {
            favoriteImageView.setImageResource(R.drawable.empty_star);
        }
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the movie is already in our favorite, we remove it
                if (listFavRecipe.contains(mRecipe)) {
                    listFavRecipe.remove(mRecipe);
                    favoriteImageView.setImageResource(R.drawable.empty_star);
                    RecipesDBHelper.removeFromFavorite(mRecipe, getContentResolver());
                } else { //otherwise we just add it
                    listFavRecipe.add(mRecipe);
                    favoriteImageView.setImageResource(R.drawable.filled_star);
                    RecipesDBHelper.addToFavorite(mRecipe, getContentResolver());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        FavoriteHelper.setFavorite(this,listFavRecipe);
        super.onPause();
    }

    private void loadUI(Bundle savedInstanceState){
        //UI
        if (mRecipe != null) {
            Picasso.with(mContext).load(mRecipe.getStrDrinkThumb()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(recipeImageView);
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
            bundle.putParcelable(RecipeFragment.KEY_INTENT_POSITION_INGREDIENT_LIST, positionIngredientList);
            bundle.putParcelable(RecipeFragment.KEY_INTENT_POSITION_STEP_LIST, positionStepList);
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
