package ru.tpu.russiantpu.auth.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.requests.RequestService;

/*
* Диалоговое окно, отображающее ошибку
* */
public class ResetPasswordFragment extends DialogFragment implements Validator.ValidationListener {

    //поле ввода емейла
    @Email(messageResId = R.string.email_error)
    private TextInputEditText emailInput;
    private ContentLoadingProgressBar progressBar;

    private Validator validator;

    private final RequestService requestService;
    private final String language;

    public ResetPasswordFragment(RequestService requestService, String language) {
        this.requestService = requestService;
        this.language = language;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView((inflater.inflate(R.layout.fragment_password_reset, null)));
        View view = inflater.inflate(R.layout.fragment_password_reset, null);

        emailInput = view.findViewById(R.id.input_email);
        String email = getArguments().getString("email", "");
        if (!email.isEmpty()) {
            emailInput.setText(email);
        }

        progressBar = view.findViewById(R.id.progress_bar);

        builder.setView(view)
                .setPositiveButton(R.string.reset_password_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //validator.validate(); //проверяем правильность введенного емейла перед отправкой в коллбэк
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestService.cancelAllRequests(); //отменяем восстановление пароля при нажатии "отменить"
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog alertDialog = (AlertDialog)getDialog();
        if(alertDialog != null) {
            Button positiveButton = (Button) alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Validator validator = new Validator(ResetPasswordFragment.this);
                    validator.setValidationListener(ResetPasswordFragment.this);
                    validator.validate();
                    //alertDialog.dismiss();
                }
            });
        }
    }

    //метод включает/выключает кнопку отправки запроса
    private void switchSendButton(boolean isEnabled) {
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(isEnabled);
        }
    }

    @Override
    public void onValidationSucceeded() {
        final Activity activity = getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.show();
                switchSendButton(false);
            }
        });
        String email = emailInput.getText().toString().isEmpty() ? null : emailInput.getText().toString();
        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        switchSendButton(true);
                    }
                });
                ToastService toastService = new ToastService(activity);
                toastService.showToast(R.string.reset_password_success);
                getDialog().dismiss();
            }

            @Override
            public void onError(String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        switchSendButton(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.reset_password_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        switchSendButton(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.reset_password_error), message, getFragmentManager());
            }
        };
        requestService.doPostRequest("auth/password/reset/request", callback, language, "email", email);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
        }
    }

/*    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        requestService.cancelAllRequests();
    }*/
}
