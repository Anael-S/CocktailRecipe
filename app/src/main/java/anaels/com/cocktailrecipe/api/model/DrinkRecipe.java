
package anaels.com.cocktailrecipe.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DrinkRecipe implements Parcelable {

    @SerializedName("idDrink")
    @Expose
    private String idDrink;
    @SerializedName("strDrink")
    @Expose
    private String strDrink;
    @SerializedName("strVideo")
    @Expose
    private String strVideo;
    @SerializedName("strCategory")
    @Expose
    private String strCategory;
    @SerializedName("strIBA")
    @Expose
    private String strIBA;
    @SerializedName("strAlcoholic")
    @Expose
    private String strAlcoholic;
    @SerializedName("strGlass")
    @Expose
    private String strGlass;
    @SerializedName("strInstructions")
    @Expose
    private String strInstructions;
    @SerializedName("strDrinkThumb")
    @Expose
    private String strDrinkThumb;
    @SerializedName("strIngredient1")
    @Expose
    private String strIngredient1;
    @SerializedName("strIngredient2")
    @Expose
    private String strIngredient2;
    @SerializedName("strIngredient3")
    @Expose
    private String strIngredient3;
    @SerializedName("strIngredient4")
    @Expose
    private String strIngredient4;
    @SerializedName("strIngredient5")
    @Expose
    private String strIngredient5;
    @SerializedName("strIngredient6")
    @Expose
    private String strIngredient6;
    @SerializedName("strIngredient7")
    @Expose
    private String strIngredient7;
    @SerializedName("strIngredient8")
    @Expose
    private String strIngredient8;
    @SerializedName("strIngredient9")
    @Expose
    private String strIngredient9;
    @SerializedName("strIngredient10")
    @Expose
    private String strIngredient10;
    @SerializedName("strIngredient11")
    @Expose
    private String strIngredient11;
    @SerializedName("strIngredient12")
    @Expose
    private String strIngredient12;
    @SerializedName("strIngredient13")
    @Expose
    private String strIngredient13;
    @SerializedName("strIngredient14")
    @Expose
    private String strIngredient14;
    @SerializedName("strIngredient15")
    @Expose
    private String strIngredient15;
    @SerializedName("strMeasure1")
    @Expose
    private String strMeasure1;
    @SerializedName("strMeasure2")
    @Expose
    private String strMeasure2;
    @SerializedName("strMeasure3")
    @Expose
    private String strMeasure3;
    @SerializedName("strMeasure4")
    @Expose
    private String strMeasure4;
    @SerializedName("strMeasure5")
    @Expose
    private String strMeasure5;
    @SerializedName("strMeasure6")
    @Expose
    private String strMeasure6;
    @SerializedName("strMeasure7")
    @Expose
    private String strMeasure7;
    @SerializedName("strMeasure8")
    @Expose
    private String strMeasure8;
    @SerializedName("strMeasure9")
    @Expose
    private String strMeasure9;
    @SerializedName("strMeasure10")
    @Expose
    private String strMeasure10;
    @SerializedName("strMeasure11")
    @Expose
    private String strMeasure11;
    @SerializedName("strMeasure12")
    @Expose
    private String strMeasure12;
    @SerializedName("strMeasure13")
    @Expose
    private String strMeasure13;
    @SerializedName("strMeasure14")
    @Expose
    private String strMeasure14;
    @SerializedName("strMeasure15")
    @Expose
    private String strMeasure15;
    @SerializedName("dateModified")
    @Expose
    private String dateModified;

    private boolean isFav;

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    /**
     * HOTIFX : The API is kinda... lame? so we're forced to recreate the list here...
     */
    private ArrayList<String> listInstructions;
    public ArrayList<String> getSteps() {
        if (listInstructions == null) {
            listInstructions = new ArrayList<>();
            String fullInstructions = getStrInstructions();
            String splittedInstructions[] = fullInstructions.split("\\r\\n");
            String splittedInstructionsBackUp[] = fullInstructions.split("\\.");
            if (splittedInstructions.length > 1) {
                for (String lInstruction : splittedInstructions) {
                    if (lInstruction != null && !lInstruction.isEmpty()) {
                        listInstructions.add(lInstruction);
                    }
                }
            } else if (splittedInstructionsBackUp.length > 1) {
                for (String lInstruction : splittedInstructionsBackUp) {
                    if (lInstruction != null && !lInstruction.isEmpty()) {
                        listInstructions.add(lInstruction);
                    }
                }
            }else {
                listInstructions.add(fullInstructions);
            }
        }
        return listInstructions;
    }

    /**
     * HOTIFX : The API is kinda... lame? so we're forced to recreate the list here...
     */
    private ArrayList<String> listIngredients;
    public ArrayList<String> getIngredients() {
        if (listIngredients == null) {
            listIngredients = new ArrayList<>();
            if (strIngredient1 != null && !strIngredient1.isEmpty()) listIngredients.add(strIngredient1.trim());
            if (strIngredient2 != null && !strIngredient2.isEmpty()) listIngredients.add(strIngredient2.trim());
            if (strIngredient3 != null && !strIngredient3.isEmpty()) listIngredients.add(strIngredient3.trim());
            if (strIngredient4 != null && !strIngredient4.isEmpty()) listIngredients.add(strIngredient4.trim());
            if (strIngredient5 != null && !strIngredient5.isEmpty()) listIngredients.add(strIngredient5.trim());
            if (strIngredient6 != null && !strIngredient6.isEmpty()) listIngredients.add(strIngredient6.trim());
            if (strIngredient7 != null && !strIngredient7.isEmpty()) listIngredients.add(strIngredient7.trim());
            if (strIngredient8 != null && !strIngredient8.isEmpty()) listIngredients.add(strIngredient8.trim());
            if (strIngredient9 != null && !strIngredient9.isEmpty()) listIngredients.add(strIngredient9.trim());
            if (strIngredient10 != null && !strIngredient10.isEmpty()) listIngredients.add(strIngredient10.trim());
            if (strIngredient11 != null && !strIngredient11.isEmpty()) listIngredients.add(strIngredient11.trim());
            if (strIngredient12 != null && !strIngredient12.isEmpty()) listIngredients.add(strIngredient12.trim());
            if (strIngredient13 != null && !strIngredient13.isEmpty()) listIngredients.add(strIngredient13.trim());
            if (strIngredient14 != null && !strIngredient14.isEmpty()) listIngredients.add(strIngredient14.trim());
            if (strIngredient15 != null && !strIngredient15.isEmpty()) listIngredients.add(strIngredient15.trim());
        }
        return listIngredients;
    }

    /**
     * HOTIFX : The API is kinda... lame? so we're forced to recreate the list here...
     */
    private ArrayList<String> listMeasures;
    public ArrayList<String> getMeasures() {
        if (listMeasures == null) {
            listMeasures = new ArrayList<>();
            if (strMeasure1 != null && !strMeasure1.isEmpty()) listMeasures.add(strMeasure1.trim());
            if (strMeasure2 != null && !strMeasure2.isEmpty()) listMeasures.add(strMeasure2.trim());
            if (strMeasure3 != null && !strMeasure3.isEmpty()) listMeasures.add(strMeasure3.trim());
            if (strMeasure4 != null && !strMeasure4.isEmpty()) listMeasures.add(strMeasure4.trim());
            if (strMeasure5 != null && !strMeasure5.isEmpty()) listMeasures.add(strMeasure5.trim());
            if (strMeasure6 != null && !strMeasure6.isEmpty()) listMeasures.add(strMeasure6.trim());
            if (strMeasure7 != null && !strMeasure7.isEmpty()) listMeasures.add(strMeasure7.trim());
            if (strMeasure8 != null && !strMeasure8.isEmpty()) listMeasures.add(strMeasure8.trim());
            if (strMeasure9 != null && !strMeasure9.isEmpty()) listMeasures.add(strMeasure9.trim());
            if (strMeasure10 != null && !strMeasure10.isEmpty()) listMeasures.add(strMeasure10.trim());
            if (strMeasure11 != null && !strMeasure11.isEmpty()) listMeasures.add(strMeasure11.trim());
            if (strMeasure12 != null && !strMeasure12.isEmpty()) listMeasures.add(strMeasure12.trim());
            if (strMeasure13 != null && !strMeasure13.isEmpty()) listMeasures.add(strMeasure13.trim());
            if (strMeasure14 != null && !strMeasure14.isEmpty()) listMeasures.add(strMeasure14.trim());
            if (strMeasure15 != null && !strMeasure15.isEmpty()) listMeasures.add(strMeasure15.trim());
        }
        return listMeasures;
    }

    public String getIdDrink() {
        return idDrink;
    }

    public void setIdDrink(String idDrink) {
        this.idDrink = idDrink;
    }

    public String getStrDrink() {
        return strDrink;
    }

    public void setStrDrink(String strDrink) {
        this.strDrink = strDrink;
    }

    public Object getStrVideo() {
        return strVideo;
    }

    public void setStrVideo(String strVideo) {
        this.strVideo = strVideo;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public Object getStrIBA() {
        return strIBA;
    }

    public void setStrIBA(String strIBA) {
        this.strIBA = strIBA;
    }

    public String getStrAlcoholic() {
        return strAlcoholic;
    }

    public void setStrAlcoholic(String strAlcoholic) {
        this.strAlcoholic = strAlcoholic;
    }

    public String getStrGlass() {
        return strGlass;
    }

    public void setStrGlass(String strGlass) {
        this.strGlass = strGlass;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public String getStrDrinkThumb() {
        return strDrinkThumb;
    }

    public void setStrDrinkThumb(String strDrinkThumb) {
        this.strDrinkThumb = strDrinkThumb;
    }

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public void setStrIngredient1(String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }

    public String getStrIngredient2() {
        return strIngredient2;
    }

    public void setStrIngredient2(String strIngredient2) {
        this.strIngredient2 = strIngredient2;
    }

    public String getStrIngredient3() {
        return strIngredient3;
    }

    public void setStrIngredient3(String strIngredient3) {
        this.strIngredient3 = strIngredient3;
    }

    public String getStrIngredient4() {
        return strIngredient4;
    }

    public void setStrIngredient4(String strIngredient4) {
        this.strIngredient4 = strIngredient4;
    }

    public String getStrIngredient5() {
        return strIngredient5;
    }

    public void setStrIngredient5(String strIngredient5) {
        this.strIngredient5 = strIngredient5;
    }

    public String getStrIngredient6() {
        return strIngredient6;
    }

    public void setStrIngredient6(String strIngredient6) {
        this.strIngredient6 = strIngredient6;
    }

    public String getStrIngredient7() {
        return strIngredient7;
    }

    public void setStrIngredient7(String strIngredient7) {
        this.strIngredient7 = strIngredient7;
    }

    public String getStrIngredient8() {
        return strIngredient8;
    }

    public void setStrIngredient8(String strIngredient8) {
        this.strIngredient8 = strIngredient8;
    }

    public String getStrIngredient9() {
        return strIngredient9;
    }

    public void setStrIngredient9(String strIngredient9) {
        this.strIngredient9 = strIngredient9;
    }

    public String getStrIngredient10() {
        return strIngredient10;
    }

    public void setStrIngredient10(String strIngredient10) {
        this.strIngredient10 = strIngredient10;
    }

    public String getStrIngredient11() {
        return strIngredient11;
    }

    public void setStrIngredient11(String strIngredient11) {
        this.strIngredient11 = strIngredient11;
    }

    public String getStrIngredient12() {
        return strIngredient12;
    }

    public void setStrIngredient12(String strIngredient12) {
        this.strIngredient12 = strIngredient12;
    }

    public String getStrIngredient13() {
        return strIngredient13;
    }

    public void setStrIngredient13(String strIngredient13) {
        this.strIngredient13 = strIngredient13;
    }

    public String getStrIngredient14() {
        return strIngredient14;
    }

    public void setStrIngredient14(String strIngredient14) {
        this.strIngredient14 = strIngredient14;
    }

    public String getStrIngredient15() {
        return strIngredient15;
    }

    public void setStrIngredient15(String strIngredient15) {
        this.strIngredient15 = strIngredient15;
    }

    public String getStrMeasure1() {
        return strMeasure1;
    }

    public void setStrMeasure1(String strMeasure1) {
        this.strMeasure1 = strMeasure1;
    }

    public String getStrMeasure2() {
        return strMeasure2;
    }

    public void setStrMeasure2(String strMeasure2) {
        this.strMeasure2 = strMeasure2;
    }

    public String getStrMeasure3() {
        return strMeasure3;
    }

    public void setStrMeasure3(String strMeasure3) {
        this.strMeasure3 = strMeasure3;
    }

    public String getStrMeasure4() {
        return strMeasure4;
    }

    public void setStrMeasure4(String strMeasure4) {
        this.strMeasure4 = strMeasure4;
    }

    public String getStrMeasure5() {
        return strMeasure5;
    }

    public void setStrMeasure5(String strMeasure5) {
        this.strMeasure5 = strMeasure5;
    }

    public String getStrMeasure6() {
        return strMeasure6;
    }

    public void setStrMeasure6(String strMeasure6) {
        this.strMeasure6 = strMeasure6;
    }

    public String getStrMeasure7() {
        return strMeasure7;
    }

    public void setStrMeasure7(String strMeasure7) {
        this.strMeasure7 = strMeasure7;
    }

    public String getStrMeasure8() {
        return strMeasure8;
    }

    public void setStrMeasure8(String strMeasure8) {
        this.strMeasure8 = strMeasure8;
    }

    public String getStrMeasure9() {
        return strMeasure9;
    }

    public void setStrMeasure9(String strMeasure9) {
        this.strMeasure9 = strMeasure9;
    }

    public String getStrMeasure10() {
        return strMeasure10;
    }

    public void setStrMeasure10(String strMeasure10) {
        this.strMeasure10 = strMeasure10;
    }

    public String getStrMeasure11() {
        return strMeasure11;
    }

    public void setStrMeasure11(String strMeasure11) {
        this.strMeasure11 = strMeasure11;
    }

    public String getStrMeasure12() {
        return strMeasure12;
    }

    public void setStrMeasure12(String strMeasure12) {
        this.strMeasure12 = strMeasure12;
    }

    public String getStrMeasure13() {
        return strMeasure13;
    }

    public void setStrMeasure13(String strMeasure13) {
        this.strMeasure13 = strMeasure13;
    }

    public String getStrMeasure14() {
        return strMeasure14;
    }

    public void setStrMeasure14(String strMeasure14) {
        this.strMeasure14 = strMeasure14;
    }

    public String getStrMeasure15() {
        return strMeasure15;
    }

    public void setStrMeasure15(String strMeasure15) {
        this.strMeasure15 = strMeasure15;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public DrinkRecipe() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idDrink);
        dest.writeString(this.strDrink);
        dest.writeString(this.strVideo);
        dest.writeString(this.strCategory);
        dest.writeString(this.strIBA);
        dest.writeString(this.strAlcoholic);
        dest.writeString(this.strGlass);
        dest.writeString(this.strInstructions);
        dest.writeString(this.strDrinkThumb);
        dest.writeString(this.strIngredient1);
        dest.writeString(this.strIngredient2);
        dest.writeString(this.strIngredient3);
        dest.writeString(this.strIngredient4);
        dest.writeString(this.strIngredient5);
        dest.writeString(this.strIngredient6);
        dest.writeString(this.strIngredient7);
        dest.writeString(this.strIngredient8);
        dest.writeString(this.strIngredient9);
        dest.writeString(this.strIngredient10);
        dest.writeString(this.strIngredient11);
        dest.writeString(this.strIngredient12);
        dest.writeString(this.strIngredient13);
        dest.writeString(this.strIngredient14);
        dest.writeString(this.strIngredient15);
        dest.writeString(this.strMeasure1);
        dest.writeString(this.strMeasure2);
        dest.writeString(this.strMeasure3);
        dest.writeString(this.strMeasure4);
        dest.writeString(this.strMeasure5);
        dest.writeString(this.strMeasure6);
        dest.writeString(this.strMeasure7);
        dest.writeString(this.strMeasure8);
        dest.writeString(this.strMeasure9);
        dest.writeString(this.strMeasure10);
        dest.writeString(this.strMeasure11);
        dest.writeString(this.strMeasure12);
        dest.writeString(this.strMeasure13);
        dest.writeString(this.strMeasure14);
        dest.writeString(this.strMeasure15);
        dest.writeString(this.dateModified);
        dest.writeStringList(this.listInstructions);
        dest.writeStringList(this.listIngredients);
        dest.writeStringList(this.listMeasures);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrinkRecipe recipe = (DrinkRecipe) o;

        return idDrink != null ? idDrink.equals(recipe.idDrink) : recipe.idDrink == null;

    }

    protected DrinkRecipe(Parcel in) {
        this.idDrink = in.readString();
        this.strDrink = in.readString();
        this.strVideo = in.readString();
        this.strCategory = in.readString();
        this.strIBA = in.readString();
        this.strAlcoholic = in.readString();
        this.strGlass = in.readString();
        this.strInstructions = in.readString();
        this.strDrinkThumb = in.readString();
        this.strIngredient1 = in.readString();
        this.strIngredient2 = in.readString();
        this.strIngredient3 = in.readString();
        this.strIngredient4 = in.readString();
        this.strIngredient5 = in.readString();
        this.strIngredient6 = in.readString();
        this.strIngredient7 = in.readString();
        this.strIngredient8 = in.readString();
        this.strIngredient9 = in.readString();
        this.strIngredient10 = in.readString();
        this.strIngredient11 = in.readString();
        this.strIngredient12 = in.readString();
        this.strIngredient13 = in.readString();
        this.strIngredient14 = in.readString();
        this.strIngredient15 = in.readString();
        this.strMeasure1 = in.readString();
        this.strMeasure2 = in.readString();
        this.strMeasure3 = in.readString();
        this.strMeasure4 = in.readString();
        this.strMeasure5 = in.readString();
        this.strMeasure6 = in.readString();
        this.strMeasure7 = in.readString();
        this.strMeasure8 = in.readString();
        this.strMeasure9 = in.readString();
        this.strMeasure10 = in.readString();
        this.strMeasure11 = in.readString();
        this.strMeasure12 = in.readString();
        this.strMeasure13 = in.readString();
        this.strMeasure14 = in.readString();
        this.strMeasure15 = in.readString();
        this.dateModified = in.readString();
        this.listInstructions = in.createStringArrayList();
        this.listIngredients = in.createStringArrayList();
        this.listMeasures = in.createStringArrayList();
    }

    public static final Creator<DrinkRecipe> CREATOR = new Creator<DrinkRecipe>() {
        @Override
        public DrinkRecipe createFromParcel(Parcel source) {
            return new DrinkRecipe(source);
        }

        @Override
        public DrinkRecipe[] newArray(int size) {
            return new DrinkRecipe[size];
        }
    };
}
