package com.example.hanan.riyadhmetro.manageTrip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TripListAdpater extends RecyclerView.Adapter<TripListAdpater.TripViewHolder> implements Filterable {


    private List<Map<String, Object>> mTrips = new LinkedList<>();
    private List<Map<String, Object>> mTripsCopy = new LinkedList<>();


    public static final String TRIP_KEY_INTENT = "trip";
    public static final String ID_KEY_INTENT = "id";

    private Context mContext;
    private List<String> mIdList;
    final private ListItemClickListener mOnClickListener;

    private FirebaseFirestore db;
    private TripViewHolder mHolder;
//    private View mParent;
//    private View mEmptyView;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex,List<Map<String, Object>> mTrips);
    }

    public TripListAdpater(List<Map<String, Object>> mTrips, ListItemClickListener mOnClickListener,Context context ,List<String> idList) {
        this.mTrips = mTrips;
        mTripsCopy = mTrips;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
        mIdList = idList;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
//        mParent = parent;
        int layoutIdForListItem = R.layout.trip_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TripViewHolder viewHolder = new TripViewHolder(view);



        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {

        Map<String, Object> trip = mTrips.get(position);
        setData(trip,holder);

        this.mHolder = holder;
        final TextView buttonViewOption = holder.buttonViewOption;

        AddOnClickListenerRecyclerViewMenu(holder,position);
        hideButtonViewOptionForUser(buttonViewOption);


    }


    /**/
    private void setData( Map<String, Object> trip,TripViewHolder holder) {

        if (trip.size() != 0 ) {


            holder.mTextViewLeavingTime.setText(trip.get("Leaving time").toString());
            holder.mTextViewArrivingTime.setText(trip.get("Arrival time").toString());

            holder.mTextViewArrivingPlace.setText(trip.get("Arrival destination").toString());
            holder.mTextViewLeavingPlace.setText(trip.get("Leaving destination").toString());
            holder.mTextViewTripDate.setText(trip.get("Date").toString());
        }

    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    /**/
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mTripsCopy == null) {
                    mTripsCopy = mTrips;

                }
                if (constraint != null & mTripsCopy != null & mTripsCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> trip : mTripsCopy) {
                        if (trip.get("Arrival destination").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);

                            oReturn.values = searchResults;
                        }else if (trip.get("Leaving destination").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get("Arrival time").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get("Leaving time").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get("Date").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }
                        else {
                            oReturn.values = searchResults;

                        }
                    }
                }


                return oReturn;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTrips = (ArrayList< Map<String, Object>>) results.values;

                if(mTrips.size() == 0)
                    TripListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    TripListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }
        };



    }


    /**/
    public void AddOnClickListenerRecyclerViewMenu(final TripViewHolder holder, final int position){

        holder.mButtonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext,  holder.mButtonViewOption);

                //inflating menu from xml resource
                popup.inflate(R.menu.base_list_menu);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {


                            case R.id.delete:
//                                Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
//                                deleteTrip(position);
//                                updateView();
                                displayDulogForDelete(position);
                                break;
                            case R.id.edit:
                                Toast.makeText(mContext,"edit",Toast.LENGTH_LONG);

                                goToEditTrip(position);
                                break;

                                //handle menu2 click

                        }
                        return true;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    /**/
    private void updateView() {

        Class viewTripClass = TripListViewActivity.class;
        Intent intent = new Intent(mContext,viewTripClass);
        mContext.startActivity(intent);
    }

    /**/
    private void deleteTrip(int position){

        String id = mIdList.get(position);
        db = FirebaseFirestore.getInstance();

        db.collection("Trip").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteTripSuccefully();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteTripUnsuccefully(e);
            }
        });


    }

    /**/
    private void deleteTripUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void deleteTripSuccefully(){

        Toast.makeText(mContext,"The Trip has been deleted succefully!",Toast.LENGTH_SHORT).show();

    }

    /**/
    private void goToEditTrip(int position){

        HashMap<String, Object> trip =(HashMap) mTrips.get(position);

        String id = mIdList.get(position);

        Class editTripClass = EditTripAvtivity.class;
        Intent intent = new Intent(mContext,editTripClass);
        intent.putExtra(TRIP_KEY_INTENT, trip);
        intent.putExtra(ID_KEY_INTENT,id);
        mContext.startActivity(intent);
    }


    /**/
    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mTextViewTripDate;
        public TextView mTextViewArrivingTime;
        public TextView mTextViewLeavingTime;

        public TextView mTextViewLeavingPlace;
        public TextView mTextViewArrivingPlace;

        public TextView buttonViewOption;


        public TripViewHolder(View itemView) {
            super(itemView);

            mTextViewArrivingPlace = itemView.findViewById(R.id.arrivingPlaceText);
            mTextViewLeavingPlace = itemView.findViewById(R.id.laevingPlaceText);

            this.buttonViewOption = itemView.findViewById(R.id.buttonViewOption);

            mTextViewTripDate = itemView.findViewById(R.id.tripDateText);
            mTextViewArrivingTime = itemView.findViewById(R.id.arrivingTimeText);
            mTextViewLeavingTime = itemView.findViewById(R.id.leavingTimetext);

            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
//            mOnClickListener.onListItemClick(clickedPosition,mTrips,mIdLisCopy);
            mOnClickListener.onListItemClick(clickedPosition,mTrips);



        }

    }

    /**/
    private void hideButtonViewOptionForUser(TextView buttonViewOption){


        if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.USER_AUTHORITY
           || PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY)
            buttonViewOption.setVisibility(View.GONE);
    }

    /**/
    private void displayDulogForDelete(final int position){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete the trip?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                        deleteTrip(position);
                        updateView();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}

