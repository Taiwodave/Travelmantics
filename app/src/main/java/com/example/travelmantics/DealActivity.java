package com.example.travelmantics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText txtTitle,txtDescription,txtPrice;
    public static final int PICTURE_RESULT = 42;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        FirebaseUtil.openFbReference("traveldeals", this);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        final Intent intent = new Intent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"),PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if (deal.getId() == null){
            databaseReference.push().setValue(deal);
        }
        else{
            databaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal(){
        if (deal == null){
            Toast.makeText(this, "Please save the Deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean(){
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void enableEditText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
        }
        else
        {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == PICTURE_RESULT && resultCode == RESULT_OK ){
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.StorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri);


        }
    }


    public void showMenu() {
    }
}



