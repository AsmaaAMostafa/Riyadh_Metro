package com.example.hanan.riyadhmetro.manageTicket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TicketListAdpater extends RecyclerView.Adapter<TicketListAdpater.TicketViewHolder> {


    private List<Map<String, Object>> mTickets = new LinkedList<>();
    private List<Map<String, Object>> mTicketsCopy = new LinkedList<>();

    public static final String Ticket_KEY_INTENT = "ticket";
    public static final String ID_KEY_INTENT = "id";
    private Context mContext;
    private List<String> mIdList;
    final private ListItemClickListener mOnClickListener;
//    private FirebaseFirestore db;
//    private TicketViewHolder mHolder;
//    private String ticketDate ;
//    private String lastAct ;
//    private String Autherity;
//    private String tripCode;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, List<Map<String, Object>> mTickets);
    }

    public TicketListAdpater(List<Map<String, Object>> Tickets, ListItemClickListener mOnClickListener, Context context ,String thedate, String rAutherity , String rtripCode) {
        this.mTickets = Tickets;
        mTicketsCopy = Tickets;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
//        ticketDate = thedate;
//        tripCode=rtripCode;
//        Autherity=rAutherity;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.ticket_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TicketViewHolder viewHolder = new TicketViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {

        Map<String, Object> ticket ;

        if (mTickets != null) {
          //  position=0;
            ticket = mTickets.get(position);
            setTheData(ticket,holder);

        }

    }


    /**/
    private void setData( Map<String, Object> ticket,TicketViewHolder holder) {

        if (ticket.size() != 0 ) {
//              if (mTickets != null ){
//            if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY ){
//                setTheData(ticket,holder);
//            }
//            else if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY ){
//
//                if(lastAct!=null&& ticketDate!=null && tripCode!=null){
//                    if(ticketDate.equals(ticket.get("arriving  place").toString())){
//                        setTheData(ticket,holder);
//                    }
//                }else if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY ){
//
//                    setTheData(ticket,holder);
//                }
//            }

            setTheData(ticket,holder);

        }
//        }
    }
    /**/
    private void setTheData(Map<String, Object> ticket,TicketViewHolder holder){

        if (ticket != null && ticket.size() != 0 ) {


            holder.mTextViewLeavingTime.setText(ticket.get("Leaving time").toString());
            holder.mTextViewArrivingPlace.setText(ticket.get("Arrival destination").toString());
            holder.mTextViewArrivingTime.setText(ticket.get("Arrival time").toString());
            holder.mTextViewLeavingPlace.setText(ticket.get("Leaving destination").toString());
            holder.mTextViewticketDate.setText(ticket.get("Date").toString());
        }

    }
    @Override
    public int getItemCount() {
            return mTickets.size();
    }
 /*@Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTickets = (ArrayList< Map<String, Object>>) results.values;

                if(mTickets.size() == 0)
                    TicketListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    TicketListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }*/

    /*
    private void deleteticket(int position){ // use it for cancel ticket

        String id = mIdList.get(position);
        db = FirebaseFirestore.getInstance();

        db.collection("ticket").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteticketSuccefully();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteticketUnsuccefully(e);
            }
        });


    }

    /*
    private void deleteticketUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /*
    private void deleteticketSuccefully(){

        Toast.makeText(mContext,"The ticket has been deleted succefully!",Toast.LENGTH_SHORT).show();

    }

*/
    /**/
    class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mTextViewticketDate;
        public TextView mTextViewArrivingTime;
        public TextView mTextViewLeavingTime;
        public TextView mTextViewLeavingPlace;
        public TextView mTextViewArrivingPlace;


        public TextView buttonViewOption;


        public TicketViewHolder(View itemView) {
            super(itemView);

            mTextViewArrivingPlace = itemView.findViewById(R.id.arrivingPlaceText);
            mTextViewLeavingPlace = itemView.findViewById(R.id.laevingPlaceText);
            this.buttonViewOption = itemView.findViewById(R.id.buttonViewOption);
            mTextViewticketDate = itemView.findViewById(R.id.tripDateText);// to be detected
            mTextViewArrivingTime = itemView.findViewById(R.id.arrivingTimeText);
            mTextViewLeavingTime = itemView.findViewById(R.id.leavingTimetext);



            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mTickets);


        }

    }

    /*
    private void hideButtonViewOptionForUser(TextView buttonViewOption){


        if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.USER_AUTHORITY )
            buttonViewOption.setVisibility(View.GONE);
    }
*/
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mTicketsCopy == null)
                    mTicketsCopy = mTickets;
                if (constraint != null & mTicketsCopy != null & mTicketsCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> ticket : mTicketsCopy) {
                        if (ticket.get("Arrival destination").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get("Leaving destination").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get("Arrival time").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get("Leaving time").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get("Date").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else {
                            oReturn.values = searchResults;
                        }
                    }
                }
                return oReturn;
            }

            /**
             * <p>Invoked in the UI thread to publish the filtering results in the
             * user interface. Subclasses must implement this method to display the
             * results computed in {@link #performFiltering}.</p>
             *
             * @param constraint the constraint used to filter the data
             * @param results    the results of the filtering operation
             * @see #filter(CharSequence, FilterListener)
             * @see #performFiltering(CharSequence)
             * @see FilterResults
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTickets = (ArrayList< Map<String, Object>>) results.values;

                if( mTickets == null || mTickets.size() == 0)
                    TicketListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    TicketListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }
        };
    }

}


