package com.apvereda.digitalavatars;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.apvereda.db.Avatar;
import com.apvereda.digitalavatars.ui.friendslist.MyFriendsFragment;
import com.apvereda.digitalavatars.ui.addfriend.AddFriendFragment;
import com.apvereda.digitalavatars.ui.home.HomeFragment;
import com.apvereda.digitalavatars.ui.profile.ProfileFragment;
import com.apvereda.utils.DigitalAvatar;
import com.apvereda.utils.OneSignalService;
import com.apvereda.utils.SiddhiService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DrawerActivity extends AppCompatActivity {

    public static final int MESSAGE_NOT_ID = 101;
    private DigitalAvatar da;
    NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    AddFriendFragment friendFragment = new AddFriendFragment();
    MyFriendsFragment friendlistFragment = new MyFriendsFragment();
    DrawerLayout drawer;
    Fragment[] fragments = new Fragment[]{homeFragment,profileFragment,friendFragment,friendlistFragment};
    String[] fragmentTAGS = new String[]{"home","profile","addfriend","friendlist"};
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragments[0] = getSupportFragmentManager().getFragment(savedInstanceState, "Home");
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                ft.show(fragments[0]);
                                ft.hide(fragments[1]);
                                ft.hide(fragments[2]);
                                ft.hide(fragments[3]);
                                toolbar.setVisibility(View.VISIBLE);
                                break;
                            case R.id.nav_profile:
                                profileFragment.updateAvatar();
                                ft.show(fragments[1]);
                                ft.hide(fragments[0]);
                                ft.hide(fragments[2]);
                                ft.hide(fragments[3]);
                                toolbar.setVisibility(View.GONE);
                                break;
                            case R.id.nav_add_friend:
                                ft.show(fragments[2]);
                                ft.hide(fragments[0]);
                                ft.hide(fragments[1]);
                                ft.hide(fragments[3]);
                                toolbar.setVisibility(View.GONE);
                                break;
                            case R.id.nav_friend_list:
                                friendlistFragment.updateFriends();
                                ft.show(fragments[3]);
                                ft.hide(fragments[0]);
                                ft.hide(fragments[1]);
                                ft.hide(fragments[2]);
                                toolbar.setVisibility(View.VISIBLE);
                                break;
                        }
                        //Log.i("Digital Avatars", "Paso por aqui al cambiar de fragment: bien hecho");
                        ft.commit();
                        menuItem.setChecked(true);
                        drawer.closeDrawers();
                        return true;
                    }
                });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragments[0], fragmentTAGS[0])
                .add(R.id.nav_host_fragment, fragments[1], fragmentTAGS[1])
                .add(R.id.nav_host_fragment, fragments[2], fragmentTAGS[2])
                .add(R.id.nav_host_fragment, fragments[3], fragmentTAGS[3])
                .hide(fragments[1])
                .hide(fragments[2])
                .hide(fragments[3])
                .commit();

        /*
        IMPORTANTE PRIMERO INICIALIZAR DA
         */
        SiddhiService.getServiceConnection(getApplicationContext());
        DigitalAvatar.init(getApplicationContext());
        da = DigitalAvatar.getDA();
        OneSignalService.initialize(getApplicationContext());
        createNotificationChannel();
        firebaseLogin();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                navController.getGraph())//, R.id.nav_profile, R.id.nav_add_friend
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.nav_home) {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Digital Avatars");
                } else {
                    toolbar.setVisibility(View.GONE);
                }
            }
        });*/
        //SiddhiService.registerReceivers(this);
        // new UpdateTask().execute();
    }
/*
    private class UpdateTask extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setFeature("http://xml.org/sax/features/allow-java-encodings", true);
            }catch (SAXException e) {
                System.err.println("could not set parser feature");
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            }
            String SOURCE = "https://protege.stanford.edu/ontologies/pizza/pizza.owl";
            String NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl" + "#";
            OntDocumentManager odm = OntDocumentManager.getInstance();
            OntModel base = ModelFactory.createOntologyModel();
            base.getReader().setProperty("http://apache.org/xml/features/allow-java-encodings", true);
            File file = new File(getExternalFilesDir(null)+"/OWL/pizza.owl");
            //odm.addModel(SOURCE,base);
            base.read( "file:"+file.getPath());
            OntModel inf = ModelFactory.createOntologyModel( OWL_MEM_MICRO_RULE_INF, base );
            // create a dummy paper for this example
            OntClass paper = base.getOntClass( NS + "AnchoviesTopping" );
            Individual p1 = base.createIndividual( NS + "americanhot1", paper );

            // list the asserted types
            for ( Iterator<Resource> i = p1.listRDFTypes(false); i.hasNext(); ) {
                System.out.println( p1.getURI() + " is asserted in class " + i.next() );
            }

            // list the inferred types
            p1 = inf.getIndividual( NS + "americanhot1" );
            for (Iterator<Resource> i = p1.listRDFTypes(false); i.hasNext(); ) {
                System.out.println( p1.getURI() + " is inferred to be in class " + i.next() );
            }
            return null;
        }
    }
    */


    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction()
                .show(fragments[0])
                .hide(fragments[1])
                .hide(fragments[2])
                .hide(fragments[3])
                .commit();
        toolbar.setVisibility(View.VISIBLE);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    private void firebaseLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    123);
        } else {
            logUser();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                logUser();
            } else {
                Log.i("Digital Avatar", "Log in fallido");
                if(response == null){
                    Log.i("Digital Avatar", "User cancelled sing in flow pressing back button");
                }else {
                    Log.i("Digital Avatar", response.getError().getMessage());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                //return true;
                break;
            case R.id.action_readapps:
                homeFragment.readFile();
                break;
            case R.id.action_removeapps:
                homeFragment.removeApps();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        drawer.openDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "Home", fragments[0]);
    }

    private void logUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //MutableDocument doc = da.getDoc("Avatar");
        user.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            Avatar.getAvatar().setIdToken(task.getResult().getToken());
                            /*MutableDocument doc = da.getDoc("Avatar");
                            doc.setString("IDToken", task.getResult().getToken());
                            da.saveDoc(doc);*/
                        } else {
                            Log.i("Digital Avatar", task.getException().getMessage());
                        }
                    }
                });
        Avatar avatar = Avatar.getAvatar();
        avatar.setEmail(user.getEmail());
        avatar.setName(user.getDisplayName());
        avatar.setOneSignalID(OneSignalService.getUserID());
        avatar.setUID(user.getUid());
        avatar.setPhone(user.getPhoneNumber());
        avatar.setPhoto(user.getPhotoUrl().toString());
        /*doc.setString("IDOneSignal", OneSignalService.getUserID());
        doc.setString("Name", user.getDisplayName());
        doc.setString("Email", user.getEmail());
        doc.setString("UID", user.getUid());
        doc.setString("Phone", user.getPhoneNumber());
        doc.setString("Photo", user.getPhotoUrl().toString());
        da.saveDoc(doc);*/
        Log.i("Digital Avatar", "Datos personales almacenados en el avatar");

        loadUserView(user);
    }

    private void loadUserView(FirebaseUser user) {
        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.myname);
        name.setText(user.getDisplayName());
        TextView email = header.findViewById(R.id.myemail);
        email.setText(user.getEmail());
        ImageView image = header.findViewById(R.id.userimage);
        image.setImageURI(user.getPhotoUrl());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DigitalAvatarsChannel";
            String description = "DigitalAvatarsChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Siddhi", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
