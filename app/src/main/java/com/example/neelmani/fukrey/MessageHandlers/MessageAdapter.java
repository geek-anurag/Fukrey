package com.example.neelmani.fukrey.MessageHandlers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neelmani.fukrey.MainActivity;
import com.example.neelmani.fukrey.R;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private final List<MessageDetails> messageDetails;
    private Activity context;
    private static String deviceEmailId;

    public MessageAdapter(Activity context, List<MessageDetails> messageDetails) {
        this.context = context;
        this.messageDetails = messageDetails;
        deviceEmailId= MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, "");
    }

    @Override
    public int getCount() {
        if (messageDetails != null) {
            return messageDetails.size();
        } else {
            return 0;
        }
    }

    @Override
    public MessageDetails getItem(int position) {
        if (messageDetails != null) {
            return messageDetails.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            MessageDetails item = getItem(position);
            // convertView.setBackgroundResource(R.drawable.update_bk);
            convertView.setTag(holder);

            //region Assign Holder's properties values
            holder.message.setText(item.getMessage());
            holder.agree.setText(getAgreeDisargeeNumber(item.getAgree()));
            holder.disagree.setText(getAgreeDisargeeNumber(item.getDisagree()));
            holder.userName.setText(item.getUserName());
            holder.timeStamp.setText(item.getTimeStamp());
            holder.distance.setText(item.getDistance());
            if(item.getAddress()=="")
                holder.address.setVisibility(convertView.GONE);
            else
                holder.address.setText(item.getAddress());
            holder.address.setText(item.getAddress());
            holder.lsc.setText(item.getLSC());
            holder.imageButtonAgree.setId(Integer.parseInt(item.getMessageId()));//TODO MessageID should be less then 2000000000
            holder.imageButtonDisgree.setId(Integer.parseInt(item.getMessageId()) + 2000);
            //endregion
            //region Assign Delete button
            if(!deviceEmailId.equals(item.getUserName()))
            {holder.imageButtonDelete.setVisibility(View.GONE);}
            else
            {holder.imageButtonDelete.setVisibility(View.VISIBLE);
                holder.imageButtonDelete.setId(Integer.parseInt(item.getMessageId()) + 4000);}
            //endregion
            //region Assign agree disagree image button
          if(item.getAgree().contains(deviceEmailId))
            {holder.imageButtonAgree.setBackgroundResource(R.drawable.agreed);}
            else
            { holder.imageButtonAgree.setBackgroundResource(R.drawable.agree);}
            if(item.getDisagree().contains(deviceEmailId))
            { holder.imageButtonDisgree.setBackgroundResource(R.drawable.disagreed);;}
            else
            { holder.imageButtonDisgree.setBackgroundResource(R.drawable.disagree);;}
           //endregion
            //region Assign Default Image from converted Bytearray to Bitmap
           // new ImageByteToBitmapTask(holder.image).execute(item.getImage());
            holder.image.setImageBitmap(item.getBitmapImage());
            //endregion
            }
        catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private ViewHolder createViewHolder(View row) {
        ViewHolder holder = new ViewHolder();
        holder.message = (TextView) row.findViewById(R.id.text);
        holder.image = (ImageView) row.findViewById(R.id.image);
        holder.agree = (TextView) row.findViewById(R.id.txtAgree);
        holder.disagree = (TextView) row.findViewById(R.id.txtDisagree);
        holder.userName = (TextView) row.findViewById(R.id.txtUserName);
        holder.timeStamp = (TextView) row.findViewById(R.id.txtTime);
        holder.imageButtonAgree=(ImageButton) row.findViewById(R.id.btnAgree);
        holder.imageButtonDisgree=(ImageButton) row.findViewById(R.id.btnDisagree);
        holder.imageButtonDelete=(ImageButton) row.findViewById(R.id.btnDelete);
        holder.distance = (TextView) row.findViewById(R.id.txtDistance);
        holder.address = (TextView) row.findViewById(R.id.txtAddress);
        holder.lsc = (TextView) row.findViewById(R.id.txtLSC);


        return holder;
    }

    public void add(MessageDetails message) {
        messageDetails.add(message);
    }

    static class ViewHolder {
        TextView message;
        ImageView image;
        TextView agree;
        TextView disagree;
        TextView userName;
        TextView timeStamp;
        TextView distance;
        TextView address;
        TextView lsc;
        ImageButton imageButtonAgree;
        ImageButton imageButtonDisgree;
        ImageButton imageButtonDelete;
       }

    private String getAgreeDisargeeNumber(String users)
    {
        String number="0";
        if(!users.isEmpty())
        {
           number= String.valueOf((users.split(",").length-1));
        }
        return number;
    }

   /* private class ImageByteToBitmapTask extends AsyncTask<byte[], Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageByteToBitmapTask(ImageView imageView )
        {
            imageViewWeakReference=new WeakReference<ImageView>(imageView);
        }
        @Override
        protected Bitmap doInBackground(byte[]... params) {
            Log.d("AreaUpdateFragment", "doInBackground ...............................");
                return objBH.getImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {
                if (isCancelled()) {
                    bitmap = null;
                }

                if (imageViewWeakReference != null) {
                    ImageView imageView = imageViewWeakReference.get();
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }*/

   /* public String getColorInteger(int randNum)
    {
       if(randNum==0)
            return ;
        else
            return "#FF42A5F5";
    }*/
}