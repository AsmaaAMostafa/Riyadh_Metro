package com.example.hanan.riyadhmetro.manageMetro;

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

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.assign.AssignedMonitorToMetroActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NUMBER_OF_SEATS_FIELD;

public class MetroListAdpater extends RecyclerView.Adapter<MetroListAdpater.MetroViewHolder> implements Filterable {


    private List<Map<String, Object>> mMetro = new LinkedList<>();
    private List<Map<String, Object>> mMetroCopy = new LinkedList<>();

    public static final String Metro_KEY_INTENT = "metro";
    public static final String ID_KEY_INTENT = "id";

    private Context mContext;
    private List<String> mIdList;
    final private MetroListAdpater.ListItemClickListener mOnClickListener;

    private FirebaseFirestore db;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex,List<Map<String, Object>> mMetro);
    }

    public MetroListAdpater(List<Map<String, Object>> mMetro, MetroListAdpater.ListItemClickListener mOnClickListener, Context context , List<String> idList) {
        this.mMetro = mMetro;
        mMetroCopy = mMetro;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
        mIdList = idList;
    }

    @Override
    public MetroListAdpater.MetroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.metro_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MetroListAdpater.MetroViewHolder viewHolder = new MetroListAdpater.MetroViewHolder(view);



        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MetroListAdpater.MetroViewHolder holder, int position) {

        Map<String, Object> metro = mMetro.get(position);
        setData(metro,holder);

        final TextView buttonViewOption = holder.buttonViewOption;

        AddOnClickListenerRecyclerViewMenu(holder,position);

    }


    /**/
    private void setData( Map<String, Object> metro,MetroListAdpater.MetroViewHolder holder) {

        if (metro.size() != 0 ) {


            holder.mTextViewMetroId.setText(metro.get(METRO_ID_FIELD).toString());
//            holder.mTextViewNumber.setText(metro.get("number_of_seats").toString());
            holder.mTextViewStatus.setText(metro.get(METRO_STATUS_FIELD).toString());

        }

    }

    @Override
    public int getItemCount() {
        return mMetro.size();
    }

    /**/
    /**/
    public void AddOnClickListenerRecyclerViewMenu(final MetroListAdpater.MetroViewHolder holder, final int position){

        holder.mButtonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext,  holder.mButtonViewOption);

                int metroMenu = R.menu.metro_list_menu;
                //inflating menu from xml resource
                popup.inflate(metroMenu);

                displayMenuForAdmin(popup);
                displayMenuForMonitor(popup);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {


                            case R.id.delete:
                                displayDulogForDelete(position);

                                break;
                            case R.id.edit:
                                Toast.makeText(mContext,"edit",Toast.LENGTH_LONG);

                                goToEditMetro(position);
                                break;
                            case R.id.assign:
                                Toast.makeText(mContext,"assign",Toast.LENGTH_LONG);

                                goToAssignMonitor(position);
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

    private void displayMenuForMonitor(PopupMenu metroMenu) {

        if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY ){
            metroMenu.getMenu().findItem(R.id.delete).setVisible(false);
            metroMenu.getMenu().findItem(R.id.assign).setVisible(false);

        }

    }

    private void displayMenuForAdmin(PopupMenu metroMenu) {

        if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.ADMIN_AUTHORITY )
            metroMenu.getMenu().findItem(R.id.edit).setVisible(false);


    }

    private void goToAssignMonitor(int position) {

        HashMap<String, Object> metro =(HashMap) mMetro.get(position);

        String id = mIdList.get(position);
        Class editMetroClass = AssignedMonitorToMetroActivity.class;
        Intent intent = new Intent(mContext,editMetroClass);
        intent.putExtra(Metro_KEY_INTENT, metro);
        intent.putExtra(ID_KEY_INTENT,id);
        mContext.startActivity(intent);
    }

    /**/

    private void updateView() {

            Intent intent = new Intent(mContext,MetroListViewActivity.class);
            mContext.startActivity(intent);
        }

    /**/
    private void deleteMetro(int position){

        String id = mIdList.get(position);
        db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_METRO).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteMetroSuccefully();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteMetroUnsuccefully(e);
            }
        });


    }

    /**/
    private void deleteMetroUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void deleteMetroSuccefully(){

        Toast.makeText(mContext,"The Metro has been deleted succefully!",Toast.LENGTH_SHORT).show();

    }

    /**/
    private void goToEditMetro(int position){

        HashMap<String, Object> metro =(HashMap) mMetro.get(position);

        String id = mIdList.get(position);
        Class editMetroClass = EditMetroActivity.class;
        Intent intent = new Intent(mContext,editMetroClass);
        intent.putExtra(Metro_KEY_INTENT, metro);
        intent.putExtra(ID_KEY_INTENT,id);
        mContext.startActivity(intent);
    }


    /**/
    class MetroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mTextViewMetroId;
        public TextView mTextViewStatus;
//        public TextView mTextViewNumber;



        public TextView buttonViewOption;


        public MetroViewHolder(View itemView) {
            super(itemView);

            mTextViewMetroId = itemView.findViewById(R.id.metroIdText);

            this.buttonViewOption = itemView.findViewById(R.id.buttonViewOption);

            mTextViewStatus = itemView.findViewById(R.id.statusText);
//            mTextViewNumber=itemView.findViewById(R.id.seatsText);

            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mMetro);


        }

    }



    /**/
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mMetroCopy == null)
                    mMetroCopy = mMetro;
                if (constraint != null & mMetroCopy != null & mMetroCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> metro : mMetroCopy) {
                        if (metro.get(METRO_ID_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metro);
                            oReturn.values = searchResults;
                        }
                        else if (metro.get(METRO_STATUS_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metro);
                            oReturn.values = searchResults;
                        }
                        else if (metro.get(NUMBER_OF_SEATS_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metro);
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
                mMetro = (ArrayList< Map<String, Object>>) results.values;

                if(mMetro.size() == 0)
                    MetroListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    MetroListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }
        };



    }

    /**/
    private void displayDulogForDelete(final int position){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete the metro  ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                        deleteMetro(position);
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
