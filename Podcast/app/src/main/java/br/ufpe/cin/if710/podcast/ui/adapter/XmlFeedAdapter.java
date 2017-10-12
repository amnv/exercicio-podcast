package br.ufpe.cin.if710.podcast.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.service.DownloadIntentService;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;
    public final static String  DOWNLOAD_LINK = "DOWNLOAD_LINK";
    public final static String  TITLE_NAME = "DOWNLOAD_LINK";

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);

            //adicionando click listener
            //ao cliclar vai para EpisodeDetailActivity
            holder.item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "funcionou", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), EpisodeDetailActivity.class);
                    intent.putExtra(ItemFeed.CLICKED_ITEM, holder.item_title.getText());
                    intent.putExtra(ItemFeed.CLICK_PUBDATE, holder.item_date.getText());
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            convertView.setTag(holder);


            holder.button = convertView.findViewById(R.id.item_action);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Clicked button", "chamando o DownloadIntentService");
                    Intent intent = new Intent(getContext(), DownloadIntentService.class);
                    intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_AUDIO);
                    intent.putExtra(DOWNLOAD_LINK, getItem(position).getDownloadLink());
                    intent.putExtra(TITLE_NAME, holder.item_title.getText().toString());
                    getContext().startService(intent);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_title.setText(getItem(position).getTitle());
        holder.item_date.setText(getItem(position).getPubDate());
        return convertView;
    }
}