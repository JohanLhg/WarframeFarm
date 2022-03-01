package com.warframefarm.activities.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.warframefarm.AppExecutors;
import com.warframefarm.R;
import com.warframefarm.activities.main.MainViewModel;
import com.warframefarm.databinding.DialogSignInBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class SignInDialog extends DialogFragment {

    private final Context context;
    private final SignInListener listener;
    private DialogSignInBinding binding;
    private MainViewModel mainViewModel;

    private FirebaseAuth auth;

    private final Executor mainThread, backgroundThread;

    private EditText editEmail, editPassword;
    private TextView textCreateAccount;
    private Button buttonSignIn;


    public SignInDialog(Context context, SignInListener listener) {
        this.context = context;
        this.listener = listener;

        AppExecutors executors = new AppExecutors();
        mainThread = executors.getMainThread();
        backgroundThread = executors.getBackgroundThread();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DialogSignInBinding.inflate(inflater, container, false);

        editEmail = binding.editEmail;
        editPassword = binding.editPassword;

        textCreateAccount = binding.textCreateAccount;

        buttonSignIn = binding.buttonSignIn;

        mainViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(MainViewModel.class);

        auth = FirebaseAuth.getInstance();

        textCreateAccount.setOnClickListener(v -> {
            listener.showSignUpDialog();
            dismiss();
        });

        buttonSignIn.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (email.equals("")) {
                Toast.makeText(context, "You need to enter an email address.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(context, "You need to enter your password.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = ProgressDialog.show(context, "Retrieving data", "Retrieving data from the cloud");
            backgroundThread.execute(() -> {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Authentication succeed.",
                                        Toast.LENGTH_SHORT).show();
                                mainViewModel.syncFromOnline();
                                mainViewModel.updateUser();

                                mainThread.execute(progressDialog::dismiss);
                                dismiss();
                            } else {
                                Log.w("FIREBASE ERROR", "createUserWithEmail:failure", task.getException());
                                // If sign in fails, display a message to the user.
                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                mainThread.execute(progressDialog::dismiss);
                            }
                        });
            });
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }

    public interface SignInListener {
        void showSignUpDialog();
    }
}
