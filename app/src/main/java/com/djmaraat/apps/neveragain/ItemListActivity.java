package com.djmaraat.apps.neveragain;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.djmaraat.apps.neveragain.helpers.Utilities;
import com.djmaraat.apps.neveragain.entities.DocumentItem;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    // Constants to be used when sharing message on facebook time line.
    private static final int FACEBOOK_ERROR_PERMISSION = 200;
    private static final String PARAM_EXPLICIT = "fb:explicitly_shared";
    private static final String PARAM_GRAPH_PATH = "/me/feed";
    private static final String PARAM_MSG = "message";
    private static final String PARAM_LINK = "link";

    private boolean mTwoPane;
    ArrayList<DocumentItem> documentItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        loadData();

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                        .build();
//                ShareApi.share(content, null);
//                Log.d("LOGTEST", "shared? ");
                shareUsingGraph();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    void loadData() {
        documentItemArrayList = Utilities.loadJSONFromAsset(getApplicationContext());
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(documentItemArrayList));
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.line_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DocumentItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DocumentItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).title);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.mContentView.setText(Html.fromHtml((mValues.get(position).details).substring(0,99) + "...", Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.mContentView.setText(Html.fromHtml((mValues.get(position).details).substring(0,99) + "..."));
            }
            // set image resource based on the document id - TODO: map the data to the corresponding resource
            holder.mImageView.setImageResource(getResources().getIdentifier("image" + position , "drawable", getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        arguments.putParcelable("document", holder.mItem);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("document", holder.mItem);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mContentView;
            public final ImageView mImageView;
            public DocumentItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.list_item_img);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }

            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }

    void shareUsingGraph() {

        // Create the parameter for share.
        final Bundle params = new Bundle();
        params.putBoolean(PARAM_EXPLICIT, true);
        params.putString(PARAM_LINK, "https://developers.facebook.com");

        // If message is empty, only our link gets posted.
        String message = "This is the test message to share";
        if (!TextUtils.isEmpty(message))
            params.putString(PARAM_MSG, message);

        // Send the request via Graph API of facebook to post message on time line.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        new GraphRequest(accessToken, PARAM_GRAPH_PATH,
                params, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                if (graphResponse.getError() == null) {
                    // Success in posting on time line.
                    Log.d("LOGTEST", "Success: " + graphResponse);
                } else {
                    FacebookRequestError error = graphResponse.getError();
                    if (error.getErrorCode() == FACEBOOK_ERROR_PERMISSION)
                        // Cancelled while asking permission, show msg
                        Log.d("LOGTEST", "share permission stuff");
                    else
                        // Error occurred while posting message.
                    Log.d("LOGTEST", "Error: " + error);
                }
            }
        }).executeAsync();
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                // Application code
//                System.out.println("object>>" + object);
//                System.out.println("response>>" + response.toString());
//
//                Log.d("LOGTEST", "object: " + object);
//                Log.d("LOGTEST", "response: " + response.toString());
//
//            }
//        });
//        request.executeAsync();
    }
}
