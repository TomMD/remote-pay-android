package com.clover.remote.client.lib.example.adapter;

import com.clover.remote.client.lib.example.PayloadMessage;
import com.clover.remote.client.lib.example.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PayloadMessageAdapter extends ArrayAdapter<PayloadMessage> {

  public PayloadMessageAdapter(Context context, int resource) {
    super(context, resource);
  }

  public PayloadMessageAdapter(Context context, int resource, List<PayloadMessage> items) {
    super(context, resource, items);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {
      LayoutInflater vi;
      vi = LayoutInflater.from(getContext());
      v = vi.inflate(R.layout.payload_message_row, null);
    }

    PayloadMessage payloadMessage = getItem(position);

    if (payloadMessage != null) {
      TextView payload = (TextView) v.findViewById(R.id.PayloadContent);
      LinearLayout payloadContainer = (LinearLayout) v.findViewById(R.id.PayloadContainer);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      payload.setText(payloadMessage.getPayloadContent());

      if(payloadMessage.isSentToCustomActivity()){
        payloadContainer.setBackgroundResource(R.drawable.rounded_gray);
        params.gravity = Gravity.RIGHT;
        payloadContainer.setLayoutParams(params);
      }
      else{
        payloadContainer.setBackgroundResource(R.drawable.rounded_light_gray);
        params.gravity = Gravity.LEFT;
        payloadContainer.setLayoutParams(params);
      }
    }

    return v;
  }
}

