package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.adapters.TagsListAdapter;
import pl.kamillis.quizy.models.Tag;
import pl.kamillis.quizy.utils.ResponseHandler;
import pl.kamillis.quizy.utils.RestClient;

public class TagsListFragment extends Fragment {

    private int offset;
    private boolean isAllLoaded;
    private volatile boolean isLoading;
    private TagsListAdapter listAdapter;
    private TagsListListener listener;
    private static final int BREAKPOINT = 10;
    private static final int LIMIT = 25;

    @Bind(R.id.tagsListView) ListView tagsListView;
    @Bind(R.id.tagsListStatus) TextView tagsListStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags_list, container, false);
        ButterKnife.bind(this, view);

        offset = 0;
        isAllLoaded = false;
        isLoading = false;

        // Init list view
        initListView(inflater);

        // Get first part of list
        getTagsList();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TagsListListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void initListView(LayoutInflater inflater) {
        listAdapter = new TagsListAdapter(inflater);
        tagsListView.setAdapter(listAdapter);
        tagsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nothing to do
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (offset > 0 && !isAllLoaded && !isLoading &&
                        (totalItemCount - visibleItemCount) <= (firstVisibleItem + BREAKPOINT)) {
                    getTagsList();
                }
            }

        });
        tagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag selectedTag = (Tag)listAdapter.getItem(position);
                listener.onTagSelected(selectedTag.getName());
            }

        });
    }

    private void getTagsList() {
        RequestParams params = new RequestParams();
        params.put("limit", LIMIT);
        params.put("offset", offset);

        isLoading = true;
        tagsListStatus.setText(R.string.loading);
        tagsListStatus.setVisibility(View.VISIBLE);

        RestClient.get("tag/list", params, new ResponseHandler(getContext()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                TagsList list = gson.fromJson(responseString, TagsList.class);

                if (list.tags.size() == 0) {
                    isAllLoaded = true;
                    if (offset == 0) {
                        tagsListStatus.setText(R.string.not_found_tag);
                        isLoading = false;
                        return;
                    }
                }

                listAdapter.addItems(list.tags);
                tagsListStatus.setVisibility(View.GONE);
                offset += list.tags.size();
                isLoading = false;
            }

            class TagsList {
                List<Tag> tags;
            }

        });
    }

    public interface TagsListListener {
        void onTagSelected(String name);
    }

}
