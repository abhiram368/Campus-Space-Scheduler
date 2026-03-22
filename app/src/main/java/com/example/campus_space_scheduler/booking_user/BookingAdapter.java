package com.example.campus_space_scheduler.booking_user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.R;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, OnItemClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        if (holder.textViewSpaceName != null) {
            holder.textViewSpaceName.setText(booking.getSpaceName() != null ? booking.getSpaceName() : "Loading...");
        }

        holder.textViewDate.setText(booking.getDate() != null ? booking.getDate() : "");
        holder.textViewTimeSlot.setText(booking.getTimeSlot() != null ? booking.getTimeSlot() : "");
        holder.textViewPurpose.setText("Purpose: " + (booking.getPurpose() != null ? booking.getPurpose() : "N/A"));

        String status = booking.getStatus();
        holder.textViewStatus.setText(status != null ? status.toUpperCase() : "PENDING");

        holder.itemView.setOnClickListener(v -> listener.onItemClick(booking));

        // UI styling for status
        if (status != null) {
            switch (status.toLowerCase()) {
                case "accepted":
                case "booked":
                case "approved":
                    holder.textViewStatus.setBackgroundResource(R.drawable.status_accepted_bg);
                    break;
                case "rejected":
                    holder.textViewStatus.setBackgroundResource(R.drawable.status_rejected_bg);
                    break;
                case "pending":
                default:
                    holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSpaceName, textViewDate, textViewTimeSlot, textViewPurpose, textViewStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSpaceName = itemView.findViewById(R.id.textViewSpaceName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTimeSlot = itemView.findViewById(R.id.textViewTimeSlot);
            textViewPurpose = itemView.findViewById(R.id.textViewPurpose);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
