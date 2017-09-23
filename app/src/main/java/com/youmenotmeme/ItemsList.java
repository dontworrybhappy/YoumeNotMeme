package com.youmenotmeme;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by patriciahale on 9/23/17.
 */

public class ItemsList extends ListActivity {

        private ItemsAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.items_list);

            this.adapter = new ItemsAdapter(this, R.layout.items_list_item, ItemManager.getLoadedItems());
            setListAdapter(this.adapter);
        }

        private class ItemsAdapter extends ArrayAdapter<Item> {

            private Item[] items;

            public ItemsAdapter(Context context, int textViewResourceId, Item[] items) {
                super(context, textViewResourceId, items);
                this.items = items;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.items_list_item, null);
                }

                Item it = items[position];
                if (it != null) {
                    ImageView iv = (ImageView) v.findViewById(R.id.list_item_image);
                    if (iv != null) {
                        iv.setImageDrawable(it.getImage());
                    }
                }

                return v;
            }
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            this.adapter.getItem(position).click(this.getApplicationContext());
        }
    }
}
