package anaels.com.cocktailrecipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.FilterAdapter;
import anaels.com.cocktailrecipe.adapter.IngredientPresenter;
import anaels.com.cocktailrecipe.adapter.RecipeAdapter;
import anaels.com.cocktailrecipe.api.RecipeApiHelper;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.helper.InternetConnectionHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, display the list of recipes
 */
public class SearchActivity extends AppCompatActivity {

    ArrayList<DrinkRecipe> mRecipeList;
    ArrayList<DrinkRecipe> listFavRecipe;
    RecipeAdapter mRecipeAdapter;
    Context mContext;

    public static final String KEY_INTENT_LIST_RECIPE = "keyIntentRecipeList";


    @BindView(R.id.recyclerViewRecipes)
    RecyclerView recyclerViewRecipes;
    @Nullable
    @BindView(R.id.folding_cell)
    FoldingCell foldingCell;
    @BindView(R.id.fabSearch)
    FloatingActionButton fabSearch;
    @BindView(R.id.checkBoxAlcoholic)
    CheckBox checkBoxAlcoholic;
    @BindView(R.id.checkBoxNonalcoholic)
    CheckBox checkBoxNonalcoholic;
    @BindView(R.id.checkBoxCocktail)
    CheckBox checkBoxCocktail;
    @BindView(R.id.checkBoxOrdinaryDrink)
    CheckBox checkBoxOrdinaryDrink;
    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.editTextIngredient)
    EditText editTextIngredient;
    @BindView(R.id.recyclerViewIngredientFilter)
    RecyclerView recyclerViewIngredientFilter;

    private Autocomplete ingredientAutocomplete;
    private FilterAdapter mFilterAdapter;
    private ArrayList<String> listFilterIngredient;

    boolean isSearchLayoutFolded;
    boolean isAnimatonRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listFilterIngredient = new ArrayList<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
        ButterKnife.bind(this);
        mContext = this;
        isSearchLayoutFolded = true;
        isAnimatonRunning = false;

        initListeners();

        //If we already got our recipe lists, we recover them
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_INTENT_LIST_RECIPE);
            if (mRecipeList != null) {
                initRecyclerView();
            }
        }

        listFavRecipe = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_FAV_RECIPE);

        getIngredientList();
    }

    private void initListeners() {
        //OnClick listeners
        if (foldingCell != null) {
            foldingCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAnimatonRunning) {
                        if (editTextIngredient.hasFocus()) {
                            hideSoftKeyboard();
                        }
                        //We disable the click during the animation
                        int animationTime = 1000;
                        if (!isSearchLayoutFolded){
                            animationTime=1; //If the panel is not folded, the animation is instant
                        }
                        isAnimatonRunning = true;

                        if (isSearchLayoutFolded) {
                            //We empty the name search
                            nameEditText.setText("");
                        }
                        foldingCell.toggle(!isSearchLayoutFolded);
                        isSearchLayoutFolded = !isSearchLayoutFolded;

                        //We enable the click after the animation
                        final Handler handler = new Handler(Looper.getMainLooper());
                        final Runnable r = new Runnable() {
                            public void run() {
                                isAnimatonRunning = false;
                            }
                        };
                        handler.postDelayed(r, animationTime);
                    }
                }
            });
        }
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close the keyboard
                hideSoftKeyboard();
                //Search
                loadRecipes();
                //We fold the search panel if necessary
                if (!isSearchLayoutFolded) {
                    foldingCell.toggle(!isSearchLayoutFolded);
                    isSearchLayoutFolded = !isSearchLayoutFolded;
                }
            }
        });

        nameEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loadRecipes();
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        editTextIngredient.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void getIngredientList() {
        RecipeApiHelper.getIngredientList(this, new RecipeApiHelper.OnIngredientListRecovered() {
            @Override
            public void onIngredientListRecovered(ArrayList<String> ingredientList) {
                setupIngredientAutocomplete(ingredientList);
            }
        }, new RecipeApiHelper.OnError() {
            @Override
            public void onError() {
            }
        });
    }

    private void setupIngredientAutocomplete(ArrayList<String> ingredientList) {
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePresenter<String> presenter = new IngredientPresenter(mContext, ingredientList);
        AutocompleteCallback<String> callback = new AutocompleteCallback<String>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, String item) {
                editable.clear();
                if (!listFilterIngredient.contains(item)) {
                    listFilterIngredient.add(item);
                    initRecyclerViewFilter();
                }
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

        ingredientAutocomplete = Autocomplete.<String>on(editTextIngredient)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
    }

    private void initRecyclerViewFilter() {
        if (mFilterAdapter == null) {
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewIngredientFilter.setLayoutManager(layoutManager);
            mFilterAdapter = new FilterAdapter(this, listFilterIngredient, new FilterAdapter.OnClickFilter() {
                @Override
                public void onClickFilter(ArrayList<String> updatedFilterList) {
                    listFilterIngredient = updatedFilterList;
                    initRecyclerViewFilter();
                }
            });
            recyclerViewIngredientFilter.setAdapter(mFilterAdapter);
        } else {
            //We update the adapter
            mFilterAdapter.setListFilter(listFilterIngredient);
            mFilterAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_INTENT_LIST_RECIPE, mRecipeList);
        super.onSaveInstanceState(outState);
    }


    private void loadRecipes() {
        //If we have an internet connection
        if (InternetConnectionHelper.isNetworkAvailable(this)) {
            //If the name is filled up, we search by name
            if (!nameEditText.getText().toString().isEmpty()) {
                loadCocktailByName();
            } else if (!listFilterIngredient.isEmpty()) { //otherwise we search them by filters : ingredients + type
                loadCocktailByIngredient();
            } else { //otherwise we search them by filters : ingredients + type
                loadCocktailByTypeFilter();
            }
        } else {
            Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void loadCocktailByName() {
        String lNameCocktail = nameEditText.getText().toString();
        //We get our recipe from the network
        RecipeApiHelper.searchCocktailByName(this, lNameCocktail, new RecipeApiHelper.OnCocktailRecipeRecovered() {
            @Override
            public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                mRecipeList = new ArrayList<DrinkRecipe>();
                if (recipeList != null && recipeList.size() > 0) {
                    mRecipeList.addAll(recipeList);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }
        }, new RecipeApiHelper.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCocktailByTypeFilter() {
        String filterAlcoholic = null;
        String filterTypeOfDrink = null;
        //Alcoholic or not
        if (checkBoxAlcoholic.isChecked() && !checkBoxNonalcoholic.isChecked()) {
            filterAlcoholic = RecipeApiHelper.FILTER_ALCOHOLIC;
        } else if (checkBoxNonalcoholic.isChecked() && !checkBoxAlcoholic.isChecked()) {
            filterAlcoholic = RecipeApiHelper.FILTER_NON_ALCOHOLIC;
        }
        //Type of drink
        if (checkBoxOrdinaryDrink.isChecked() && !checkBoxCocktail.isChecked()) {
            filterTypeOfDrink = RecipeApiHelper.FILTER_ORDINARY_DRINK;
        } else if (checkBoxCocktail.isChecked() && !checkBoxOrdinaryDrink.isChecked()) {
            filterTypeOfDrink = RecipeApiHelper.FILTER_COCKTAIL;
        }
        //We get our recipe from the network
        RecipeApiHelper.searchCocktailByFilters(this, filterAlcoholic, filterTypeOfDrink, new RecipeApiHelper.OnCocktailRecipeRecovered() {
            @Override
            public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                mRecipeList = new ArrayList<DrinkRecipe>();
                if (recipeList != null && recipeList.size() > 0) {
                    mRecipeList.addAll(recipeList);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }
        }, new RecipeApiHelper.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCocktailByIngredient() {
        //We get our recipe from the network
        RecipeApiHelper.searchCocktailByIngredients(this, null, listFilterIngredient, new RecipeApiHelper.OnCocktailRecipeRecovered() {
            @Override
            public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                mRecipeList = new ArrayList<DrinkRecipe>();
                if (recipeList != null && recipeList.size() > 0) {
                    mRecipeList.addAll(recipeList);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }
        }, new RecipeApiHelper.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Initialize the recyclerview and his adapter
     */
    private void initRecyclerView() {
        recyclerViewRecipes.setHasFixedSize(true);
        recyclerViewRecipes.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_search)));
        if (mRecipeAdapter == null) {
            mRecipeAdapter = new RecipeAdapter(this, false, mRecipeList, new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DrinkRecipe item) {
                    Intent i = new Intent(mContext, RecipeActivity.class);
                    i.putExtra(HomeActivity.KEY_INTENT_RECIPE, item);
                    i.putExtra(HomeActivity.KEY_INTENT_LIST_FAV_RECIPE, listFavRecipe);
                    ActivityOptionsCompat options;
                    Pair<View, String> p1;
                    Pair<View, String> p2;
                    p1 = Pair.create((View) mRecipeAdapter.getImageViewClicked(), "recipeImageView");
                    p2 = Pair.create((View) mRecipeAdapter.getTextViewClicked(), "titleRecipeTextView");
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, p1);
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
}
