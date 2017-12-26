package anaels.com.cocktailrecipe.persistence;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for the movie DB
 */
public class RecipeContract {

	public static final String CONTENT_AUTHORITY = "anaels.com.cocktailrecipe";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


	public static final class RecipeEntry implements BaseColumns{
		// table name
		public static final String TABLE_RECIPE = "recipe";
		// columns
		public static final String _ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_FAVORITE = "favorite";
		public static final String COLUMN_IMAGE_URL = "image";

		public static final String[] getAllColumn(){
			return new String[]{_ID,COLUMN_NAME,COLUMN_TYPE,COLUMN_FAVORITE,COLUMN_IMAGE_URL};
		}

		// create content uri
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(TABLE_RECIPE).build();
		// create cursor of base type directory for multiple entries
		public static final String CONTENT_DIR_TYPE =
		ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_RECIPE;
		// create cursor of base type item for single entry
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_RECIPE;

		// for building URIs on insertion
		public static Uri buildMovieURI(long id){
        		return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}