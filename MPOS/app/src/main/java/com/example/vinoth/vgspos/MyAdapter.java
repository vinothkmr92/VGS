package com.example.vinoth.vgspos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter implements Filterable {

    private final List<String> originalList;
    private List<String> filteredList;

    // Constructor
    public MyAdapter(List<String> originalList) {
        this.originalList = originalList;
        this.filteredList = originalList; // Initially the same
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate default layout. Replace with R.layout.your_custom_row if you have one.
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind data from the filtered list
        String item = filteredList.get(position);
        holder.textView.setText(item);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = (constraint != null) ? constraint.toString().toLowerCase().trim() : "";
                List<String> resultsList = new ArrayList<>();

                if (searchString.isEmpty()) {
                    resultsList = originalList;
                } else {
                    for (String item : originalList) {
                        // The core case-insensitive "contains" search
                        if (item.toLowerCase().contains(searchString)) {
                            resultsList.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultsList;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<String>) results.values;
                notifyDataSetChanged(); // Refresh ListView UI with filtered results
            }
        };
    }

    // ViewHolder pattern optimization for ListView performance
    private static class ViewHolder {
        TextView textView;
    }
}
