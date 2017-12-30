package anaels.com.cocktailrecipe.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;


/**
 * Display the ingredient on recipe activity
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private ArrayList<String> listFilter;
    private Activity mActivity;
    private OnClickFilter onClickFilter;


    public FilterAdapter(Activity activity, ArrayList<String> listFilter, OnClickFilter onClickFilter) {
        this.mActivity = activity;
        this.listFilter = listFilter;
        this.onClickFilter=onClickFilter;
    }

    public interface OnClickFilter {
        void onClickFilter(ArrayList<String> updatedFilterList);
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_filter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int i) {
        //Text
        viewHolder.filterTextView.setText(listFilter.get(i));
        //onClick
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> updatedFilterList = listFilter;
                updatedFilterList.remove(i);
                onClickFilter.onClickFilter(updatedFilterList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView filterTextView;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            filterTextView = (TextView) view.findViewById(R.id.filterTextView);
        }
    }

    public void setListFilter(ArrayList<String> listFilter) {
        this.listFilter = listFilter;
    }
}