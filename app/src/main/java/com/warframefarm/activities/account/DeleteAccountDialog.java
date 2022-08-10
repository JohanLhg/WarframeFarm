package com.warframefarm.activities.account;

import static com.warframefarm.data.FirestoreTags.USERS_TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.warframefarm.AppExecutors;
import com.warframefarm.R;
import com.warframefarm.activities.main.MainViewModel;
import com.warframefarm.databinding.DialogDeleteAccountBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class DeleteAccountDialog extends DialogFragment {
    private final Context context;
    private final DeleteAccountDialogListener listener;
    private DialogDeleteAccountBinding binding;
    private MainViewModel mainViewModel;

    private final Executor backgroundThread, mainThread;

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final FirebaseUser user;

    private EditText editPassword;
    private Button buttonCancel, buttonDelete;

    public DeleteAccountDialog(Context context) {
        this.context = context;

        AppExecutors appExecutors = new AppExecutors();
        backgroundThread = appExecutors.getBackgroundThread();
        mainThread = appExecutors.getMainThread();

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (context instanceof DeleteAccountDialogListener)
            listener = (DeleteAccountDialogListener) context;
        else throw new RuntimeException(context.toString() + " must implement DeleteAccountDialogListener");
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
        binding = DialogDeleteAccountBinding.inflate(inflater, container, false);

        editPassword = binding.editDialogPassword;

        buttonCancel = binding.buttonCancel;
        buttonDelete = binding.buttonDelete;

        mainViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(MainViewModel.class);

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonDelete.setOnClickListener(v -> {
            String password = editPassword.getText().toString();
            if (password.equals("") || password.length() < 6) {
                Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                return;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

            ProgressDialog progressReAuth = ProgressDialog
                    .show(context, getString(R.string.title_reauthentication), getString(R.string.text_reauthentication));
            backgroundThread.execute(() -> {
                user.reauthenticate(credential).addOnCompleteListener(userReAuthTask -> {
                    mainThread.execute(() -> {
                        progressReAuth.dismiss();
                        if (userReAuthTask.isSuccessful()) {
                            ProgressDialog progressAccountDeletion = ProgressDialog
                                        .show(context, getString(R.string.title_deleting_account), getString(R.string.text_deleting_account));

                            backgroundThread.execute(() -> {
                                DocumentReference docRef = firestore.document(USERS_TAG + "/" + user.getUid());

                                docRef.delete().addOnCompleteListener(docDeleteTask -> {
                                    user.delete().addOnCompleteListener(userDeleteTask -> {
                                        if (userDeleteTask.isSuccessful()) {
                                            mainThread.execute(() -> {
                                                progressAccountDeletion.dismiss();
                                                Toast.makeText(context, R.string.account_deleted, Toast.LENGTH_SHORT).show();
                                                listener.accountDeleted();
                                                mainViewModel.resetUserData();
                                            });
                                        }
                                        else {
                                            mainThread.execute(() -> {
                                                progressAccountDeletion.dismiss();
                                                Toast.makeText(context, userDeleteTask.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            });
                                        }
                                        dismiss();
                                    });
                                });
                            });
                        }
                        else {
                            Toast.makeText(context, userReAuthTask.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                });
            });

        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }

    public interface DeleteAccountDialogListener {
        void accountDeleted();
    }
}
