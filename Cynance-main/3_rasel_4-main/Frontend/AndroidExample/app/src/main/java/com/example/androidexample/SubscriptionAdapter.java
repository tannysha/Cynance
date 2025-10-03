package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {
    private List<Subscription> subscriptionList;
    private View.OnClickListener onItemClickListener;

    public SubscriptionAdapter(List<Subscription> subscriptionList, View.OnClickListener onItemClickListener) {
        this.subscriptionList = subscriptionList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription subscription = subscriptionList.get(position);
        holder.title.setText(subscription.getServiceName());
        holder.startDate.setText("Start: " + subscription.getStartDate());
        holder.endDate.setText("End: " + subscription.getEndDate());
        holder.price.setText("Price: $" + subscription.getPrice());

        holder.itemView.setTag(subscription);
        holder.itemView.setOnClickListener(onItemClickListener);

        holder.editButton.setOnClickListener(v -> showEditDialog(holder.itemView.getContext(), subscription, position));
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, startDate, endDate, price;
        Button editButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_service_name);
            startDate = itemView.findViewById(R.id.text_start_date);
            endDate = itemView.findViewById(R.id.text_end_date);
            price = itemView.findViewById(R.id.text_price);
            editButton = itemView.findViewById(R.id.btn_edit_subscription);
        }
    }

    private void showEditDialog(Context context, Subscription subscription, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Subscription");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        EditText inputName = new EditText(context);
        inputName.setHint("Service Name");
        inputName.setText(subscription.getServiceName());
        layout.addView(inputName);

        EditText inputStartDate = new EditText(context);
        inputStartDate.setHint("Start Date (YYYY-MM-DD)");
        inputStartDate.setText(subscription.getStartDate());
        layout.addView(inputStartDate);

        EditText inputEndDate = new EditText(context);
        inputEndDate.setHint("End Date (YYYY-MM-DD)");
        inputEndDate.setText(subscription.getEndDate());
        layout.addView(inputEndDate);

        EditText inputPrice = new EditText(context);
        inputPrice.setHint("Price ($)");
        inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputPrice.setText(subscription.getPrice());
        layout.addView(inputPrice);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            subscription.setServiceName(inputName.getText().toString());
            subscription.setStartDate(inputStartDate.getText().toString());
            subscription.setEndDate(inputEndDate.getText().toString());
            subscription.setPrice(inputPrice.getText().toString());

            subscriptionList.set(position, subscription);
            notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
