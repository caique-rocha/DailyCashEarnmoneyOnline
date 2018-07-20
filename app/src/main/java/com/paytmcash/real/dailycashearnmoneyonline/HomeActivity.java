package com.paytmcash.real.dailycashearnmoneyonline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity  implements RewardedVideoAdListener {
    private boolean exit=false;
  // private LinearLayout logout_button;

  // FirebaseAuth.AuthStateListener mAuthListener;
    TextView score_text;
    LinearLayout spin_button,logout_button,redeem_button, watch_video_buuton,share_id,rate_button;

   // private RewardedVideoAd mRewardedVideoAd;

    int myIntValue;
    DatabaseReference  user_id_child;
    String user_id;

    //both DatabaseReference are same
    //Remove one and replace by Firebase database
    DatabaseReference databaseReference;

  private  RewardedVideoAd mRewardedVideoAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
      MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    //   databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        user_id_child = databaseReference.child(user_id);
        //below line added by me
        //it was not work because myIntValue gives 0
       // user_id_child.child("scores").setValue(myIntValue);


        getSupportActionBar().setTitle("Earn Money");


        // Admob video ads       ca-app-pub-3940256099942544/5224354917
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        spin_button =  findViewById(R.id.spin_linear_id);
        redeem_button =findViewById(R.id.linear_redeem_id);
        logout_button =findViewById(R.id.linear_logout_id);
        watch_video_buuton =findViewById(R.id.linear_watch_video_id);
        share_id =findViewById(R.id.linear_share_id);
        score_text =findViewById(R.id.wallet_text_score_id);
        rate_button =findViewById(R.id.linear_rate_id);


        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        myIntValue = sp.getInt("your_int_key", 0);



       // logout_button = (LinearLayout)findViewById(R.id.linear_logout_id);
      //  mAuth=FirebaseAuth.getInstance();

       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                   // startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);

                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };*/



        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              diaglog();
            }
        });


        rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.paytmcash.real.dailycashearnmoneyonline");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });



      watch_video_buuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this,"Please tap more times to load the ad", Toast.LENGTH_LONG).show();
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
                else {
                    loadRewardedVideoAd();
                }
            }
        });

        redeem_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, Redeem.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.side_right, R.anim.side_left);
            }
        });

        spin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, Spinwheel.class);
                // *********What is the value of myIntValue?********
                intent.putExtra("INT",myIntValue);

                startActivity(intent);


                overridePendingTransition(R.anim.side_right, R.anim.side_left);
            }
        });

        share_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(view);
            }
        });




      /*  databaseReference.child("users").child(user_id).setValue(user_id_child)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                        user_id_child.child("scores").setValue(myIntValue);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });*/


    }











//  loadRewardedVideoAd(); you should comment out
    @Override
    protected void onStart() {
        super.onStart();

        String s = String.valueOf(myIntValue);
        score_text.setText(s);


        //Must add this blow line for update the database everytime and it shows in app
     /*   databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //write code here

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
       loadRewardedVideoAd();
    }


    private void logout() {


        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void diaglog(){


        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.custom_logout);
        Button no = (Button) dialog.findViewById(R.id.no_id);

        Button yes = (Button)dialog.findViewById(R.id.yes_id);

        // if button is clicked, close the custom dialog
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();

            }
        });
        dialog.show();

    }

  /*  @Override
    public void onBackPressed() {
      if(exit){
          finish();
      }
      else {
          Toast.makeText(HomeActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
      }
        Timer timer = new Timer();
      timer.schedule(new TimerTask() {
          @Override
          public void run() {
              exit=false;
          }
      },2000);
      exit=true;
    }*/



   /* @Override
    public void onBackPressed() {
        diaglog();
    }*/
/*  If menu is required then it should open
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout_id: {
                logout();
            }

            return true;


        }


        return super.onOptionsItemSelected(item);


    }
*/

// ADMOB VIDEO METHODS

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        Toast.makeText(HomeActivity.this,"Congratulations, you will get 100 points", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        int n =  myIntValue+100;
        editor.putInt("your_int_key", n);
        editor.commit();
        user_id_child.child("scores").setValue(+n);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    private void loadRewardedVideoAd() {
//App id
        if(!mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());


        }
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }









//see carefully
    /*@Override
    protected void onStart() {
        super.onStart();

        String s = String.valueOf(myIntValue);
        score_text.setText(s);

        loadRewardedVideoAd();


    }*/


    public void share(View view){

//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Earn Money");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Earn unlimited money by simple tasks"+"  "+"https://play.google.com/store/apps/details?id=com.paytmcash.real.dailycashearnmoneyonline");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));



    }
   /* private void logout() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
*/

    @Override
    public void finish() {
        super.finish();


        finish();

    }

}
