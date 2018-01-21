package anaels.com.cocktailrecipe.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import anaels.com.cocktailrecipe.api.model.DrinkRecipe;

/**
 * Helper for the Recipe DB
 */
public class RecipesDBHelper extends SQLiteOpenHelper {
	public static final String LOG_TAG = RecipesDBHelper.class.getSimpleName();

	//name & version
	private static final String DATABASE_NAME = "recipes.db";
	private static final int DATABASE_VERSION = 12;

	public RecipesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create the database
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
				RecipeContract.RecipeEntry.TABLE_RECIPE + "(" + RecipeContract.RecipeEntry._ID +
				" INTEGER PRIMARY KEY, " +
				RecipeContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
				RecipeContract.RecipeEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
				RecipeContract.RecipeEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
				RecipeContract.RecipeEntry.COLUMN_IMAGE_URL + " TEXT);";

		sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
	}

	/**
	 * Insert the recipe into the DB
	 * @param recipe the recipe to insert
	 */
	private static void insertInDB(DrinkRecipe recipe, ContentResolver contentResolver) {
		ContentValues recipeValue = new ContentValues();
		recipeValue.put(RecipeContract.RecipeEntry._ID, recipe.getIdDrink());
		recipeValue.put(RecipeContract.RecipeEntry.COLUMN_FAVORITE, 1); //is fav
		recipeValue.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, recipe.getStrDrinkThumb());
		recipeValue.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.getStrDrink());
		recipeValue.put(RecipeContract.RecipeEntry.COLUMN_TYPE, recipe.getStrCategory());

		// insert our ContentValues array
        contentResolver.insert(RecipeContract.RecipeEntry.CONTENT_URI,
				recipeValue);
		Log.d(LOG_TAG, "Recipe inserted");
	}

    /**
     * Add the recipe to favorite
     * @param pRecipe the recipe to add
     */
    public static void addToFavorite(DrinkRecipe pRecipe, ContentResolver contentResolver) {
        ContentValues addFavorite = new ContentValues();
        addFavorite.put(RecipeContract.RecipeEntry.COLUMN_FAVORITE, 1); //mark as favorite

        int updatedRows = contentResolver.update(
                RecipeContract.RecipeEntry.CONTENT_URI,
                addFavorite,
                RecipeContract.RecipeEntry._ID + " = ?",
                new String[]{String.valueOf(pRecipe.getIdDrink())}
        );
        //If the row doesn't exist in the DB yet
        if (updatedRows <= 0) {
            Log.d(LOG_TAG, "Movie can't be updated, we need to insert it");
            //We need to insert it
            insertInDB(pRecipe,contentResolver);
        } else {
            Log.d(LOG_TAG, "Movie marked as favorite");
        }
    }

    /**
     * Remove the recipe from the favorite
     * @param pRecipe the movie to remove
     */
    public static void removeFromFavorite(DrinkRecipe pRecipe, ContentResolver contentResolver) {
        ContentValues removeFromFavorite = new ContentValues();
        removeFromFavorite.put(RecipeContract.RecipeEntry.COLUMN_FAVORITE, 0);

        int updatedRows = contentResolver.update(
                RecipeContract.RecipeEntry.CONTENT_URI,
                removeFromFavorite,
                RecipeContract.RecipeEntry._ID + " = ?",
                new String[]{String.valueOf(pRecipe.getIdDrink())}
        );
        if (updatedRows <= 0) {
            Log.d(LOG_TAG, "Movie not updated");
        } else {
            Log.d(LOG_TAG, "Movie updated");
        }
    }

	// Upgrade database when version is changed.
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ". OLD DATA WILL BE DESTROYED");
		// Drop the table
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_RECIPE);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                RecipeContract.RecipeEntry.TABLE_RECIPE + "'");

		// re-create database
		onCreate(sqLiteDatabase);
	}
}