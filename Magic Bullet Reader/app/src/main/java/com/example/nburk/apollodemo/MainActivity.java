package com.example.nburk.apollodemo;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.CreatePostMutation;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;

import javax.annotation.Nonnull;

// https://laughingsquid.com/wp-content/uploads/2013/07/QjJjgS0.jpg
// https://i.pinimg.com/originals/6d/90/af/6d90af5a16ed15f7e2c4a092b8884700.jpg

public class MainActivity extends Activity {

    ApolloApplication application;

    private NfcAdapter mNfcAdapter;
    //The array lists to hold our messages
    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();

    //Text boxes to add and display our messages
    private TextView txtReceivedMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send

            //This will be called if the message is sent successfully
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtReceivedMessages = (TextView) findViewById(R.id.txtMessagesReceived);
        updateTextViews();

        application = (com.example.nburk.apollodemo.ApolloApplication) getApplication();
    }


    private void updateTextViews() {
        txtReceivedMessages.setText("Messages Received:\n");
        //Populate our list of messages we have received
        if (messagesReceivedArray.size() > 0) {
            for (int i = 0; i < messagesReceivedArray.size(); i++) {
                txtReceivedMessages.append(messagesReceivedArray.get(i));
                txtReceivedMessages.append("\n");
            }
        }
    }


    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("messagesToSend", messagesToSendArray);
        savedInstanceState.putStringArrayList("lastMessagesReceived",messagesReceivedArray);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messagesToSendArray = savedInstanceState.getStringArrayList("messagesToSend");
        messagesReceivedArray = savedInstanceState.getStringArrayList("lastMessagesReceived");
    }




    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure we don't pass along our AAR (Android Application Record)
                    if (string.equals(getPackageName())) { continue; }
                    messagesReceivedArray.add(string);
                }
//                createPost("test", "test");
                User johnny = new User("Johnny",
                        "Tan",
                        "94025",
                        "Menlo Park",
                        "CA",
                        "12/13/2019",
                        "13/13/2014",
                        "1226 University Drive",
                        "Windows",
                        "1233211234");
                handleNfcMsgReceived(johnny);
                Toast.makeText(this, "Received " + messagesReceivedArray.size() +
                        " Messages", Toast.LENGTH_LONG).show();
                updateTextViews();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleNfcMsgReceived(User user) {
        createPost(user.address1, user.address2, user.city, user.doe, user.doi, user.firstName,
                    user.lastName, user.ssn, user.state, user.zip);
    }
//
    private class User {
        String firstName, lastName, zip, city, state, doe, doi, address1, address2, ssn;

        public User(String firstName, String lastName, String zip, String city, String state,
                           String doe, String doi, String address1, String address2, String ssn) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.zip = zip;
            this.city = city;
            this.state = state;
            this.doe = doe;
            this.doi = doi;
            this.address1 = address1;
            this.address2 = address2;
            this.ssn = ssn;
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTextViews();
        handleNfcIntent(getIntent());
    }



    private ApolloCall.Callback<CreatePostMutation.Data> createPostMutationCallback = new ApolloCall.Callback<CreatePostMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<CreatePostMutation.Data> dataResponse) {
            Log.d(ApolloApplication.TAG, "Executed mutation: " + dataResponse.data());
//            fetchPosts();
        }
        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.d(ApolloApplication.TAG, "Error:" + e.toString());
        }
    };

    private void createPost(String address1,
                            String address2,
                            String city,
                            String doe,
                            String doi,
                            String firstName,
                            String lastName,
                            String ssn,
                            String state,
                            String zip) {
        application.apolloClient().mutate(
                CreatePostMutation.builder()
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .doe(doe)
                        .doi(doi)
                        .firstName(firstName)
                        .lastName(lastName)
                        .ssn(ssn)
                        .state(state)
                        .zip(zip)
                        .build())
                .enqueue(createPostMutationCallback);
    }

}
