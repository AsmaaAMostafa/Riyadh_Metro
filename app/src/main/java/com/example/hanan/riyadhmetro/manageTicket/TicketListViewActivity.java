package com.example.hanan.riyadhmetro.manageTicket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;

//import com.example.hanan.riyadhmetro.manageTicket.ticket.AddticketActivity;
//import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.ticket_KEY_INTENT;

public class TicketListViewActivity extends AppCompatActivity implements  TicketListAdpater.ListItemClickListener ,SearchView.OnQueryTextListener  {


    private RecyclerView mticketList;
    private TicketListAdpater mAdapter;
    private List<String> mIdList;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mtickets;
    private List<Map<String, Object>> admintickets; // the com from admin
    private static View emptyView;
    private FirebaseAuth mFirebaseAuth;
    private TextView mEmptyTitleText;
    private String lastAct;
    private String ticketDate;
    private String tripCode;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        lastAct = null;
        ticketDate = null;
        tripCode = null;

//        getRecevedIntent();

        initElement();
        viewRecyclerView();

        hideAddButton();
    }

    @SuppressLint("RestrictedApi")
    private void hideAddButton() {

        mFloatingActionButton.setVisibility(View.GONE);

    }

    private void initElement(){

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        emptyView = findViewById(R.id.empty_view);
        mEmptyTitleText = findViewById(R.id.empty_title_text);
        mEmptyTitleText.setText("There no avalible Tickets");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mtickets = new ArrayList<Map<String, Object>>();
        mticketList=findViewById(R.id.rv);
        progressDialog = new ProgressDialog(this);
        mFloatingActionButton = findViewById(R.id.addButton);
    }



    /**/
    public static View getEmptyView(){

        return emptyView;
    }

    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mticketList.setLayoutManager(layoutManager);
        mticketList.setHasFixedSize(true);
        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY ){
           getDataFromAdminList();
        } else {
            getDataFromDatabase();
        }

    }
/**/
    private void getDataFromAdminList(){
        mtickets = admintickets ;
        if(mtickets != null ){
            emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {
            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    /**/
    private void initAdapter(){
        mAdapter = new TicketListAdpater(mtickets,this,this ,ticketDate,lastAct ,tripCode);
        mticketList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }
    /**/
    private void getDataFromDatabase(){

       String email = firebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("User").whereEqualTo("Email", email).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult() != null){
                    getDate(task,"tickets");
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TicketListViewActivity.this,"Error",Toast.LENGTH_LONG);
                }

            }
        });
    }
    /**/
    private  void getDate(Task<QuerySnapshot> task, String array) {
        boolean isEmpty = true;
        for (DocumentSnapshot document : task.getResult()) {
            mtickets = (ArrayList) document.getData().get(array);
            if (mtickets != null)
                isEmpty = false;
        }
        if(mtickets != null && isEmpty==false ){
            emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {
            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex, List<Map<String, Object>> tickets) {

        HashMap<String, Object> tickethere = (HashMap) tickets.get(clickedItemIndex);
        Context context = TicketListViewActivity.this;
        Class metroMonitorClass = ViewTicketActivity.class;

        Intent intent = new Intent(context,metroMonitorClass);
        intent.putExtra(Ticket_KEY_INTENT, tickethere);
        startActivity(intent);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ( TextUtils.isEmpty ( newText) ) {

            mAdapter.getFilter().filter("");
        } else {
            mAdapter.getFilter().filter(newText.toString());
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem m = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) m.getActionView();
        searchView.setOnQueryTextListener(TicketListViewActivity.this);


        return true;
    }
    /**/


}
