package anaels.com.cocktailrecipe;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.RecipeAdapter;
import anaels.com.cocktailrecipe.api.RecipeApiHelper;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.helper.FavoriteHelper;
import anaels.com.cocktailrecipe.helper.InternetConnectionHelper;
import anaels.com.cocktailrecipe.persistence.RecipeContract;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, display the list of recipes
 */
public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ArrayList<DrinkRecipe> mRecipeList;
    ArrayList<DrinkRecipe> mFavoriteRecipeList;
    RecipeAdapter mRecipeAdapter;
    LinearLayoutManager layoutManagerRecyclerView;
    Context mContext;

    public static final String KEY_INTENT_RECIPE = "keyIntentRecipe";
    public static final String KEY_INTENT_LIST_RECIPE = "keyIntentRecipeList";
    public static final String KEY_INTENT_LIST_FAV_RECIPE = "keyIntentFavorite";

    private static final int RECIPE_DB = 0;

    private boolean loadFavFromDB = true;

    @BindView(R.id.recyclerViewRecipes)
    RecyclerView recyclerViewRecipes;
    @BindView(R.id.fabSearch)
    FloatingActionButton fabSearch;

    //Analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
        ButterKnife.bind(this);
        mContext = this;

        //Orientation
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManagerRecyclerView = new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_home), LinearLayoutManager.HORIZONTAL, false);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManagerRecyclerView = new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_home));
        }

        //Button onClick
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSearchActivity();
            }
        });

        //If we already got our recipe lists, we recover them
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_INTENT_LIST_RECIPE);
            initRecyclerView(false);
        }

        //Ad
        MobileAds.initialize(this, getString(R.string.app_id_admob));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice("51CE1F2EACEE1C8EB4FBA9B4F0F2098F") //Phone
                .addTestDevice("3FA0ACCC8A4E195EE4C1BD13BD8BECED") //S2
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) //Emulator
                .build();
        mAdView.loadAd(adRequest);

        //Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_INTENT_LIST_RECIPE, mRecipeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManagerRecyclerView = new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_home), LinearLayoutManager.HORIZONTAL, false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManagerRecyclerView = new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_home));
        }
        initRecyclerView(true);
    }

    /**
     * Load the recipes fron internet if we have an internet connection
     * Load them fron a local json file otherwise
     */
    private void loadRecipesFromAPI() {
        //If we have an internet connection
        if (InternetConnectionHelper.isNetworkAvailable(this)) {
            //We get our recipe from the network
            RecipeApiHelper.getRandomCocktail(this, new RecipeApiHelper.OnCocktailRecipeRecovered() {
                @Override
                public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                    if (mRecipeList == null) mRecipeList = new ArrayList<DrinkRecipe>();
                    //If we already got it in the list, we dont add it again
                    if (!mRecipeList.contains(recipeList.get(0))) {
                        mRecipeList.addAll(recipeList);
                    }
                    //We load 3 random recipe just to have some recipe to start
                    if (mRecipeList.size() < 3) {
                        loadRecipesFromAPI();
                    } else {
                        initRecyclerView(false);
                    }
                }
            }, new RecipeApiHelper.OnError() {
                @Override
                public void onError() {
                    Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadFavFromDB) {
            //We load our recipe from the DB
            getLoaderManager().initLoader(RECIPE_DB, null, this);
            loadFavFromDB = false;
        } else {
            //We get our updated fav list without accessing the DB again
            if (FavoriteHelper.getFavorite(this) != null && !FavoriteHelper.getFavorite(this).isEmpty()) {
                mRecipeList = FavoriteHelper.getFavorite(this);
                mFavoriteRecipeList = mRecipeList;
                initRecyclerView(false);
            } else { //If its empty, we check in our DB, if still empty then we reset some random recipe with our loader
                getLoaderManager().initLoader(RECIPE_DB, null, this);
                loadFavFromDB = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            launchSearchActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchSearchActivity() {
        Intent i = new Intent(mContext, SearchActivity.class);
        i.putExtra(KEY_INTENT_LIST_FAV_RECIPE, mFavoriteRecipeList);
        startActivity(i);
    }

    /**
     * Initialize the recyclerview and his adapter
     */
    private void initRecyclerView(boolean hardReset) {
        recyclerViewRecipes.setHasFixedSize(true);
        recyclerViewRecipes.setLayoutManager(layoutManagerRecyclerView);
        if (mRecipeAdapter == null || hardReset) {
            mRecipeAdapter = new RecipeAdapter(this, true, mRecipeList, new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DrinkRecipe item) {
                    Intent i = new Intent(mContext, RecipeActivity.class);
                    i.putExtra(KEY_INTENT_RECIPE, item);
                    i.putExtra(KEY_INTENT_LIST_FAV_RECIPE, mFavoriteRecipeList);
                    i.putExtra(HomeActivity.KEY_INTENT_LIST_RECIPE, mRecipeList);
                    ActivityOptionsCompat options;
                    Pair<View, String> p1;
                    Pair<View, String> p2;
                    p1 = Pair.create((View) mRecipeAdapter.getImageViewClicked(), "recipeImageView");
                    p2 = Pair.create((View) mRecipeAdapter.getTextViewClicked(), "titleRecipeTextView");
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, p1);
                    startActivity(i, options.toBundle());
                }
            });
            recyclerViewRecipes.setAdapter(mRecipeAdapter);
        } else {
            //We update the adapter
            mRecipeAdapter.setListRecipe(mRecipeList);
            mRecipeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Load the favorites movies from the DB
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecipeContract.RecipeEntry.COLUMN_NAME + " DESC";
        String SELECTION = RecipeContract.RecipeEntry.COLUMN_FAVORITE + "=?";
        String[] selectionArgs = new String[]{"1"};

        return new CursorLoader(this,
                RecipeContract.RecipeEntry.CONTENT_URI,
                RecipeContract.RecipeEntry.getAllColumn(), // Projection
                SELECTION, //selection
                selectionArgs, //selection args
                sortOrder);
    }

    /**
     * When the movie are loaded, we put it on our list
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && !data.isClosed() && data.getCount() > 0 && data.moveToFirst()) {
            mFavoriteRecipeList = new ArrayList<>();
            do {
                final int id = data.getInt(data.getColumnIndex(RecipeContract.RecipeEntry._ID));
                String title = data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME));
                String image = data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL));
                String type = data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_TYPE));
                final boolean IS_FAVORITE = data.getInt(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_FAVORITE)) == 1;
                DrinkRecipe lRecipe = new DrinkRecipe();
                lRecipe.setIdDrink(String.valueOf(id));
                lRecipe.setStrDrink(title);
                lRecipe.setStrDrinkThumb(image);
                lRecipe.setFav(IS_FAVORITE);
                lRecipe.setStrCategory(type);
                mFavoriteRecipeList.add(lRecipe);
            }
            while (data.moveToNext());
            data.close();
        }

        //If we got some favorite we display them, otherwise we load some random recipe from the API
        if (mFavoriteRecipeList != null && mFavoriteRecipeList.size() > 0) {
            mRecipeList = mFavoriteRecipeList;
            initRecyclerView(false);
        } else {
            loadRecipesFromAPI();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}
