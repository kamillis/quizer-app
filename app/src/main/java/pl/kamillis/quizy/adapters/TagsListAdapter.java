package pl.kamillis.quizy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Tag;

/**
 * Created by Kamil on 11.01.2016.
 */
public class TagsListAdapter extends BaseAdapter {

    private List<Tag> tagList;
    private LayoutInflater inflater;

    public TagsListAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        this.tagList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag = tagList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_tag, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setText(tag.getName());
        holder.quantity.setText(tag.getFormattedQuantity());

        return convertView;
    }

    public void addItem(Tag tag) {
        tagList.add(tag);
        notifyDataSetChanged();
    }

    public void addItems(Collection<Tag> tags) {
        tagList.addAll(tags);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.tagsListName) TextView name;
        @Bind(R.id.tagsListQuantity) TextView quantity;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
