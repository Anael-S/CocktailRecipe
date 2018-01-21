package anaels.com.cocktailrecipe.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Provider for the Recipe DB
 */
public class RecipesProvider extends ContentProvider{
	private static final String LOG_TAG = RecipesProvider.class.getSimpleName();
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private RecipesDBHelper mOpenHelper;

	private static final int RECIPE = 100;
	private static final int RECIPE_WITH_ID = 200;

	private static UriMatcher buildUriMatcher(){
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = RecipeContract.CONTENT_AUTHORITY;

		// add a code for each type of URI
		matcher.addURI(authority, RecipeContract.RecipeEntry.TABLE_RECIPE, RECIPE);
		matcher.addURI(authority, RecipeContract.RecipeEntry.TABLE_RECIPE + "/#", RECIPE_WITH_ID);

		return matcher;
	}

	@Override
	public boolean onCreate(){
		mOpenHelper = new RecipesDBHelper(getContext());

		return true;
	}

	@Override
	public String getType(Uri uri){
		final int match = sUriMatcher.match(uri);

		switch (match){
			case RECIPE:{
				return RecipeContract.RecipeEntry.CONTENT_DIR_TYPE;
			}
			case RECIPE_WITH_ID:{
				return RecipeContract.RecipeEntry.CONTENT_ITEM_TYPE;
			}
			default:{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		Cursor retCursor;
		switch(sUriMatcher.match(uri)){
			// All Flavors selected
			case RECIPE:{
				retCursor = mOpenHelper.getReadableDatabase().query(
						RecipeContract.RecipeEntry.TABLE_RECIPE,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				return retCursor;
			}
			// Individual flavor based on Id selected
			case RECIPE_WITH_ID:{
				retCursor = mOpenHelper.getReadableDatabase().query(
						RecipeContract.RecipeEntry.TABLE_RECIPE,
						projection,
						RecipeContract.RecipeEntry._ID + " = ?",
						new String[] {String.valueOf(ContentUris.parseId(uri))},
						null,
						null,
						sortOrder);
				return retCursor;
			}
			default:{
				// By default, we assume a bad URI
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values){
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Uri returnUri;
		switch (sUriMatcher.match(uri)) {
			case RECIPE: {
				long _id = db.insert(RecipeContract.RecipeEntry.TABLE_RECIPE, null, values);
				// insert unless it is already contained in the database
				if (_id > 0) {
					returnUri = RecipeContract.RecipeEntry.buildMovieURI(_id);
				} else {
					throw new android.database.SQLException("Failed to insert row into: " + uri);
				}
				break;
			}

			default: {
				throw new UnsupportedOperationException("Unknown uri: " + uri);

			}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int numDeleted;
		switch(match){
			case RECIPE:
				numDeleted = db.delete(
						RecipeContract.RecipeEntry.TABLE_RECIPE, selection, selectionArgs);
				// reset _ID
				db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
						RecipeContract.RecipeEntry.TABLE_RECIPE + "'");
				break;
			case RECIPE_WITH_ID:
				numDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_RECIPE,
						RecipeContract.RecipeEntry._ID + " = ?",
						new String[]{String.valueOf(ContentUris.parseId(uri))});
				// reset _ID
				db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + 
						RecipeContract.RecipeEntry.TABLE_RECIPE + "'");

				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		return numDeleted;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values){
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch(match){
			case RECIPE:
				// allows for multiple transactions
				db.beginTransaction();

				// keep track of successful inserts
				int numInserted = 0;
				try{
					for(ContentValues value : values){
						if (value == null){
							throw new IllegalArgumentException("Cannot have null content values");
						}
						long _id = -1;
						try{
							_id = db.insertOrThrow(RecipeContract.RecipeEntry.TABLE_RECIPE,
									null, value);
						}catch(SQLiteConstraintException e) {
							Log.w(LOG_TAG, "Attempting to insert but value is already in database.");
						}
						if (_id != -1){
							numInserted++;
						}
					}
					if(numInserted > 0){
						// If no errors, declare a successful transaction.
						// database will not populate if this is not called
						db.setTransactionSuccessful();
					}
				} finally {
					// all transactions occur at once
					db.endTransaction();
				}
				if (numInserted > 0){
					// if there was successful insertion, notify the content resolver that there
					// was a change
					getContext().getContentResolver().notifyChange(uri, null);
				}
				return numInserted;
			default:
				return super.bulkInsert(uri, values);
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int numUpdated = 0;

		if (contentValues == null){
			throw new IllegalArgumentException("Cannot have null content values");
		}

		switch(sUriMatcher.match(uri)){
			case RECIPE:{
				numUpdated = db.update(RecipeContract.RecipeEntry.TABLE_RECIPE,
						contentValues,
						selection,
						selectionArgs);
				break;
			}
			case RECIPE_WITH_ID: {
				numUpdated = db.update(RecipeContract.RecipeEntry.TABLE_RECIPE,
						contentValues,
						RecipeContract.RecipeEntry._ID + " = ?",
						new String[] {String.valueOf(ContentUris.parseId(uri))});
				break;
			}
			default:{
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}

		if (numUpdated > 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return numUpdated;
	}

}