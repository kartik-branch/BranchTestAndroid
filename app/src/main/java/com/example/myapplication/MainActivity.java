package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        BranchUniversalObject buo = new BranchUniversalObject()
        .setCanonicalIdentifier("content/12345")
        .setTitle("My Content Title")
        .setContentDescription("My Content Description")
        .setContentImageUrl("https://lorempixel.com/400/400")
        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
        .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
        .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session",false)) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        }
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
            Log.d("APPPPP", "onInitFinished: "+linkProperties);
            textView.setText(linkProperties.toString());
         
            LinkProperties lp = new LinkProperties()
        .setChannel("facebook")
        .setFeature("sharing")
        .setCampaign("content 123 launch")
        .setStage("new user")
        .addControlParameter("$desktop_url", "https://example.com/home")
        .addControlParameter("custom", "data")
        .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

    buo.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
        @Override
        public void onLinkCreate(String url, BranchError error) {
            if (error == null) {
                Log.i("BRANCH SDK", "got my Branch link to share: " + url);
            }
        }
    });
            
        }
    };
}
