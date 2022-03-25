package com.warframefarm.activities.main;

import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.trendyol.medusalib.navigator.MultipleStackNavigator;
import com.trendyol.medusalib.navigator.Navigator;
import com.trendyol.medusalib.navigator.NavigatorConfiguration;
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction;
import com.warframefarm.AppExecutors;
import com.warframefarm.R;
import com.warframefarm.activities.account.DeleteAccountDialog;
import com.warframefarm.activities.account.SignInDialog;
import com.warframefarm.activities.account.SignUpDialog;
import com.warframefarm.activities.farm.FarmFragment;
import com.warframefarm.activities.list.components.ComponentsFragment;
import com.warframefarm.activities.list.planets.PlanetsFragment;
import com.warframefarm.activities.list.primes.PrimeCallback;
import com.warframefarm.activities.list.primes.PrimesFragment;
import com.warframefarm.activities.list.relics.RelicsFragment;
import com.warframefarm.database.Setting;
import com.warframefarm.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import kotlin.jvm.functions.Function0;

public class MainActivity extends AppCompatActivity implements Navigator.NavigatorListener, PrimeCallback, DeleteAccountDialog.DeleteAccountDialogListener, SignInDialog.SignInListener, SignUpDialog.SignUpListener {

    private ActivityMainBinding binding;

    //region Navigation
    public MultipleStackNavigator multipleStackNavigator;

    private final List<Function0<Fragment>> rootsFragmentProvider = Arrays
            .asList(
                    () -> new PrimesFragment(),
                    () -> new ComponentsFragment(),
                    () -> new RelicsFragment(),
                    () -> new PlanetsFragment(),
                    () -> new FarmFragment()
            );

    private final NavigationView.OnNavigationItemSelectedListener listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_primes:
                    multipleStackNavigator.switchTab(0);
                    return true;

                case R.id.navigation_components:
                    multipleStackNavigator.switchTab(1);
                    return true;

                case R.id.navigation_relics:
                    multipleStackNavigator.switchTab(2);
                    return true;

                case R.id.navigation_planets:
                    multipleStackNavigator.switchTab(3);
                    return true;

                case R.id.navigation_farm:
                    multipleStackNavigator.switchTab(4);
                    return true;
            }
            return false;
        }
    };
    //endregion

    private MainViewModel mainViewModel;
    private Executor backgroundThread, mainThread;

    private DrawerLayout layoutDrawer;
    private ImageView imageBackground;

    private TextView textTitle;
    private ImageView buttonMenu, buttonSettings;

    private NavigationView drawerNav;

    //region Settings
    private LinearLayout optionUnlimitedResults, optionLoadLimit;
    private ImageView imageUnlimitedResults;
    private EditText editTextLoadLimit;
    private LinearLayout layoutOnline;
    private Button buttonAccountSyncLocal, buttonAccountSyncOnline, buttonUpdate;
    private TextView textEmail;
    private EditText editEmail;
    private ImageButton buttonModifyEmail;
    private Button buttonDeleteAccount, buttonSignOut, buttonSignIn;

    private Button buttonCancel, buttonSave;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            SplashScreen splashScreen = getSplashScreen();
            splashScreen.setSplashScreenTheme(R.style.Theme_Splash);
        }

        //region Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        layoutDrawer = binding.layoutDrawer;
        imageBackground = binding.imageBackground;

        textTitle = binding.toolbar.textTitle;
        buttonMenu = binding.toolbar.buttonMenu;
        buttonSettings = binding.toolbar.buttonSettings;

        drawerNav = binding.drawerNav;

        optionUnlimitedResults = binding.drawerSettings.optionUnlimitedResults;
        imageUnlimitedResults = binding.drawerSettings.imageUnlimitedResults;
        optionLoadLimit = binding.drawerSettings.optionLoadLimit;
        editTextLoadLimit = binding.drawerSettings.editTextLoadLimit;

        layoutOnline = binding.drawerSettings.layoutOnline;

        buttonAccountSyncLocal = binding.drawerSettings.buttonAccountSyncLocal;
        buttonAccountSyncOnline = binding.drawerSettings.buttonAccountSyncOnline;
        buttonUpdate = binding.drawerSettings.buttonUpdate;

        textEmail = binding.drawerSettings.textEmail;
        editEmail = binding.drawerSettings.editEmail;
        buttonModifyEmail = binding.drawerSettings.buttonModifyEmail;

        buttonDeleteAccount = binding.drawerSettings.buttonDeleteAccount;
        buttonSignOut = binding.drawerSettings.buttonSignOut;
        buttonSignIn = binding.drawerSettings.buttonSignIn;

        buttonCancel = binding.drawerSettings.buttonCancel;
        buttonSave = binding.drawerSettings.buttonSave;
        //endregion

        mainViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(MainViewModel.class);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        buttonMenu.setOnClickListener(v -> layoutDrawer.openDrawer(GravityCompat.START));
        buttonSettings.setOnClickListener(v -> layoutDrawer.openDrawer(GravityCompat.END));

        //Navigation Drawer
        multipleStackNavigator = new MultipleStackNavigator(
                getSupportFragmentManager(),
                R.id.fragmentContainerMain,
                rootsFragmentProvider,
                this,
                new NavigatorConfiguration(0, true, NavigatorTransaction.SHOW_HIDE),
                null);

        multipleStackNavigator.initialize(savedInstanceState);

        drawerNav.setNavigationItemSelectedListener(listener);

        //region Settings Drawer
        buttonAccountSyncLocal.setOnClickListener(v -> {
            ProgressDialog progressDialog = ProgressDialog.show(this, "Syncing", "Syncing data from the local database");

            backgroundThread.execute(() -> {
                mainViewModel.syncFromLocal();
                mainThread.execute(progressDialog::dismiss);
            });
        });

        buttonAccountSyncOnline.setOnClickListener(v -> {
            ProgressDialog progressDialog = ProgressDialog.show(this, "Syncing", "Syncing data from the online database");

            backgroundThread.execute(() -> {
                mainViewModel.syncFromOnline();
                mainThread.execute(progressDialog::dismiss);
            });
        });

        buttonUpdate.setOnClickListener(v -> {
            mainViewModel.checkForUpdates();
            /**
            ProgressDialog progressDialog = ProgressDialog.show(this, "Updating Database", "Updating database with new information and changes");
            backgroundThread.execute(() -> {
                db.upgrade();
                mainThread.execute(progressDialog::dismiss);
            });**/
        });

        buttonModifyEmail.setOnClickListener(v -> editEmail());

        buttonDeleteAccount.setOnClickListener(v -> {
            final FragmentManager fm = getSupportFragmentManager();
            DeleteAccountDialog dialog = new DeleteAccountDialog(this);
            dialog.show(fm, "Delete Account Dialog");
        });

        buttonSignOut.setOnClickListener(v -> mainViewModel.signOut());

        buttonSignIn.setOnClickListener(v -> {
            layoutDrawer.closeDrawer(GravityCompat.END);
            showSignInDialog();
        });

        buttonCancel.setOnClickListener(v -> {
            layoutDrawer.closeDrawer(GravityCompat.END);
        });

        buttonSave.setOnClickListener(v -> {
            saveLoadLimit();
            String email = editEmail.getText().toString();
            if (editEmail.getVisibility() == View.VISIBLE && !email.isEmpty())
                mainViewModel.saveEmail(this, email);
        });
        //endregion

        //region Observers
        mainViewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser user) {
                if (user != null) {
                    String email = user.getEmail();
                    textEmail.setText(email);
                    editEmail.setText(email);

                    displayEmail();

                    buttonSignIn.setVisibility(View.GONE);
                    layoutOnline.setVisibility(View.VISIBLE);
                } else {
                    layoutOnline.setVisibility(View.GONE);
                    buttonSignIn.setVisibility(View.VISIBLE);
                }
            }
        });

        mainViewModel.getSettings().observe(this, new Observer<Setting>() {
            @Override
            public void onChanged(Setting settings) {
                if (settings == null)
                    return;
                boolean limited = settings.isLimited();
                if (limited) {
                    int loadLimit = settings.getLoadLimit();
                    imageUnlimitedResults.setBackgroundResource(R.drawable.not_checked);
                    optionLoadLimit.setVisibility(View.VISIBLE);
                    editTextLoadLimit.setText(String.valueOf(loadLimit));
                }
                else {
                    imageUnlimitedResults.setBackgroundResource(R.drawable.checked);
                    optionLoadLimit.setVisibility(View.GONE);
                }

                optionUnlimitedResults.setOnClickListener(v -> mainViewModel.setLimited(!limited));
            }
        });
        //endregion
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.updateUser();
    }

    @Override
    public void onBackPressed() {
        if (multipleStackNavigator.canGoBack())
            multipleStackNavigator.goBack();
        else super.onBackPressed();
    }

    @Override
    public void onTabChanged(int tabIndex) {
        layoutDrawer.closeDrawers();
        switch (tabIndex) {
            case 0:
                textTitle.setText(R.string.title_primes);
                imageBackground.setImageResource(R.drawable.arsenal);
                drawerNav.setCheckedItem(R.id.navigation_primes);
                break;

            case 1:
                textTitle.setText(R.string.components);
                imageBackground.setImageResource(R.drawable.mods);
                drawerNav.setCheckedItem(R.id.navigation_components);
                break;

            case 2:
                textTitle.setText(R.string.title_relics);
                imageBackground.setImageResource(R.drawable.relic_refinery);
                drawerNav.setCheckedItem(R.id.navigation_relics);
                break;

            case 3:
                textTitle.setText(R.string.title_planets);
                imageBackground.setImageResource(R.drawable.console);
                drawerNav.setCheckedItem(R.id.navigation_planets);
                break;

            case 4:
                textTitle.setText(R.string.farm);
                imageBackground.setImageResource(R.drawable.console);
                drawerNav.setCheckedItem(R.id.navigation_farm);
                break;
        }
    }

    private void saveLoadLimit() {
        String newLimit = editTextLoadLimit.getText().toString();
        if (newLimit.equals(""))
            newLimit = "0";
        mainViewModel.saveLoadLimit(Integer.parseInt(newLimit));
    }

    private void displayEmail() {
        editEmail.setVisibility(View.GONE);
        textEmail.setVisibility(View.VISIBLE);

        buttonModifyEmail.setImageResource(R.drawable.edit);
        buttonModifyEmail.setOnClickListener(v -> editEmail());
    }

    private void editEmail() {
        textEmail.setVisibility(View.GONE);
        editEmail.setVisibility(View.VISIBLE);

        buttonModifyEmail.setImageResource(R.drawable.back);
        buttonModifyEmail.setOnClickListener(v -> displayEmail());
    }

    @Override
    public void onPrimeClick(String prime_name, ImageView background, ImageView type,
                             ImageView typeShadow, ImageView vault, ImageView vaultShadow,
                             ImageView imageOwned, ImageView shadowOwned, TextView name) {
        /**
        Intent intent = new Intent(this, PrimeActivity.class);
        intent.putExtra(PRIME_NAME, prime_name);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(background, "background"),
                Pair.create(type, "type"),
                Pair.create(typeShadow, "typeShadow"),
                Pair.create(vault, "vault"),
                Pair.create(vaultShadow, "vaultShadow"),
                Pair.create(imageOwned, "imageOwned"),
                Pair.create(shadowOwned, "ownedShadow"),
                Pair.create(name, "name")
        );
        startActivity(intent, options.toBundle());
         */
    }

    @Override
    public void accountDeleted() {
        layoutDrawer.closeDrawer(GravityCompat.END);
        mainViewModel.updateUser();
    }

    @Override
    public void showSignUpDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SignUpDialog dialog = new SignUpDialog(this, this);
        dialog.show(fm, "Sign Up Dialog");
    }

    @Override
    public void showSignInDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SignInDialog dialog = new SignInDialog(this, this);
        dialog.show(fm, "Sign In Dialog");
    }
}