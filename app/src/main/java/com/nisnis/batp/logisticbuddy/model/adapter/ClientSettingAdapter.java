package com.nisnis.batp.logisticbuddy.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nisnis.batp.logisticbuddy.R;
import com.nisnis.batp.logisticbuddy.model.ClientMarker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sebastianuskh on 9/4/16.
 */
public class ClientSettingAdapter extends RecyclerView.Adapter<ClientSettingAdapter.ClientSettingViewHolder> {

    private static final String TAG = ClientSettingAdapter.class.getSimpleName();
    List<ClientMarker> clientMarkers;

    public ClientSettingAdapter() {
        clientMarkers = new ArrayList<>();
    }

    public List<ClientMarker> getClientMarkers() {
        return clientMarkers;
    }

    @Override
    public ClientSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_item, parent, false);
        return new ClientSettingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClientSettingViewHolder holder, final int position) {
        holder
                .setClient(clientMarkers.get(position).getMarker().getTitle())
                .setListener(new DeleteClient() {
                    @Override
                    public void deleteClient() {
                        clientMarkers.get(position).getMarker().remove();
                        clientMarkers.remove(position);
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return clientMarkers.size();
    }

    interface DeleteClient{
        void deleteClient();
    }

    public class ClientSettingViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.text_client_name)
        TextView clientName;

        @OnClick(R.id.button_delete)
        void deleteClient(){
            Log.i(TAG, "delete pressed on item : " + clientName.getText().toString());
            listener.deleteClient();
        }

        private DeleteClient listener;


        public ClientSettingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public ClientSettingViewHolder setClient(String title) {
            clientName.setText(title);
            return this;
        }

        public ClientSettingViewHolder setListener(DeleteClient listener){
            this.listener = listener;
            return this;
        }
    }
}
