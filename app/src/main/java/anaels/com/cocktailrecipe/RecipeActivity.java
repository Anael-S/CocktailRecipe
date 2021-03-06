package anaels.com.cocktailrecipe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.RecipeApiHelper;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.helper.FavoriteHelper;
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

    public static final String KEY_INTENT_STEP = "keyIntentStep";
    public static final String KEY_INTENT_STEP_LIST = "keyIntentStepList";
    public static final String KEY_INTENT_FROM_WIDGET = "keyIntentFromWidget";
    public static final String KEY_INTENT_IS_FAV = "keyIntentIsFav";

    @BindView(R.id.recipeImageView)
    ImageView recipeImageView;
    @BindView(R.id.favoriteImageView)
    ImageView favoriteImageView;
    @BindView(R.id.titleRecipeTextView)
    TextView titleRecipeTextView;

    RecipeFragment fragmentRecipe;
    Parcelable positionIngredientList;
    Parcelable positionStepList;

    private ArrayList<DrinkRecipe> listFavRecipe;

    private Boolean isComingFromWidget;
    private boolean isFromWidgetAndFav;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        mContext = this;

        mRecipe = getIntent().getParcelableExtra(HomeActivity.KEY_INTENT_RECIPE);
        mRecipeList = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_RECIPE);
        isComingFromWidget = getIntent().getBooleanExtra(KEY_INTENT_FROM_WIDGET, false);
        isFromWidgetAndFav = getIntent().getBooleanExtra(KEY_INTENT_IS_FAV, false);

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
            RecipeApiHelper.searchCocktailById(this, mRecipe.getIdDrink(), new RecipeApiHelper.OnCocktailRecipeRecovered() {
                @Override
                public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> drinkRecipeList) {
                    mRecipe = drinkRecipeList.get(0);
                    loadUI(savedInstanceState);
                }
            }, new RecipeApiHelper.OnError() {
                @Override
                public void onError() {
                    Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            });
        }

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animate the imageView
                final Animation bounceAnimation = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                bounceAnimation.setInterpolator(new BounceInterpolator());
                favoriteImageView.startAnimation(bounceAnimation);
                //if the movie is already in our favorite, we remove it
                if (listFavRecipe.contains(mRecipe) || isFromWidgetAndFav) {
                    listFavRecipe.remove(mRecipe);
                    mRecipe.setFav(false);
                    isFromWidgetAndFav = false;
                    favoriteImageView.setImageResource(R.drawable.ic_empty_heart);
                    RecipesDBHelper.removeFromFavorite(mRecipe, getContentResolver());
                    Snackbar.make(favoriteImageView, R.string.removed_from_fav, Snackbar.LENGTH_LONG).show();
                } else { //otherwise we just add it
                    mRecipe.setFav(true);
                    listFavRecipe.add(mRecipe);
                    favoriteImageView.setImageResource(R.drawable.ic_filled_heart);
                    RecipesDBHelper.addToFavorite(mRecipe, getContentResolver());
                    Snackbar.make(favoriteImageView, R.string.added_from_fav, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Set the right icon for the favorite button
     */
    private void setFavoriteIcon(){
        if (listFavRecipe.contains(mRecipe) || isFromWidgetAndFav) {
            favoriteImageView.setImageResource(R.drawable.ic_filled_heart);
            mRecipe.setFav(true);
        } else {
            favoriteImageView.setImageResource(R.drawable.ic_empty_heart);
            mRecipe.setFav(false);
        }
    }

    @Override
    protected void onPause() {
        FavoriteHelper.setFavorite(this, listFavRecipe);
        super.onPause();
    }

    private void loadUI(Bundle savedInstanceState) {
        //UI
        //favorite
        setFavoriteIcon();

        //Recipe
        if (mRecipe != null) {
            titleRecipeTextView.setText(mRecipe.getStrDrink());
            Picasso.with(mContext).load(mRecipe.getStrDrinkThumb()).placeholder( R.drawable.anim_loading ).into(recipeImageView);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(mRecipe.getStrDrink());
            }
        }

        //To avoid the double onCreateView call, we check if this is just a screen roation
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
            Snackbar.make(titleRecipeTextView, R.string.added_to_widget, Snackbar.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, RecipeWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
            int[] ids = AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext, RecipeWidgetProvider.class));
            if (ids != null && ids.length > 0) {
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                mContext.sendBroadcast(intent);
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isComingFromWidget){
            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
        super.onSaveInstanceState(outState);
    }


}
