package anaels.com.cocktailrecipe.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.otaliastudios.autocomplete.RecyclerViewPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import anaels.com.cocktailrecipe.R;
import anaels.com.cocktailrecipe.api.RecipeApiHelper;


public class IngredientPresenter extends RecyclerViewPresenter<String> {

    protected Adapter adapter;
    private Context context;
    private ArrayList<String> ingredientList;

    public IngredientPresenter(Context context, ArrayList<String> ingredientList) {
        super(context);
        this.context=context;
        this.ingredientList = ingredientList;
    }

    public IngredientPresenter(Context context) {
        super(context);
    }

    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        List<String> all = ingredientList;
        if (TextUtils.isEmpty(query)) {
            adapter.setData(all);
        } else {
            query = query.toString().toLowerCase();
            List<String> list = new ArrayList<>();
            for (String ingredient : all) {
                if (ingredient.toLowerCase().contains(query)) {
                    list.add(ingredient);
                }
            }
            adapter.setData(list);
            Log.e("IngredientPresenter", "found "+list.size()+" ingredient for query "+query);
        }
        adapter.notifyDataSetChanged();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<String> data;

        public class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView ingredientName;
            private ImageView imageIngredient;
            public Holder(View itemView) {
                super(itemView);
                root = itemView;
                imageIngredient = (ImageView) itemView.findViewById(R.id.imageIngredient);
                ingredientName = (TextView) itemView.findViewById(R.id.nameIngredientTextView);
            }
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.presenter_ingredient, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if (isEmpty()) {
                holder.ingredientName.setText("No result");
                holder.root.setOnClickListener(null);
                holder.imageIngredient.setVisibility(View.GONE);
                return;
            }
            final String ingredient = data.get(position);
            holder.ingredientName.setText(ingredient);
            holder.imageIngredient.setVisibility(View.VISIBLE);
            Picasso.with(context).load(RecipeApiHelper.getUrlImageByIngredient(ingredient)).into( holder.imageIngredient);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(ingredient);
                }
            });
        }
    }
}