
package anaels.com.cocktailrecipe.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListDrink implements Parcelable {

    @SerializedName("drinkRecipes")
    @Expose
    private List<DrinkRecipe> drinkRecipes = null;

    public List<DrinkRecipe> getDrinkRecipes() {
        return drinkRecipes;
    }

    public void setDrinkRecipes(List<DrinkRecipe> drinkRecipes) {
        this.drinkRecipes = drinkRecipes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.drinkRecipes);
    }

    public ListDrink() {
    }

    protected ListDrink(Parcel in) {
        this.drinkRecipes = in.createTypedArrayList(DrinkRecipe.CREATOR);
    }

    public static final Parcelable.Creator<ListDrink> CREATOR = new Parcelable.Creator<ListDrink>() {
        @Override
        public ListDrink createFromParcel(Parcel source) {
            return new ListDrink(source);
        }

        @Override
        public ListDrink[] newArray(int size) {
            return new ListDrink[size];
        }
    };
}
