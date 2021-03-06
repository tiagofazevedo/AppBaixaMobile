package br.com.a3rtecnologia.baixamobile.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.a3rtecnologia.baixamobile.R;
import br.com.a3rtecnologia.baixamobile.api.EnumAPI;
import br.com.a3rtecnologia.baixamobile.dialogs.StatusDialog;
import br.com.a3rtecnologia.baixamobile.encomenda.DelegateEncomendasAsyncResponse;
import br.com.a3rtecnologia.baixamobile.encomenda.EncomendaBusiness;
import br.com.a3rtecnologia.baixamobile.encomenda.EncomendaVolley;
import br.com.a3rtecnologia.baixamobile.encomenda.Encomendas;
import br.com.a3rtecnologia.baixamobile.menu.MenuDrawerActivity;
import br.com.a3rtecnologia.baixamobile.ocorrencia.DelegateOcorrenciaAsyncResponse;
import br.com.a3rtecnologia.baixamobile.recuperar.RecuperarActivity;
import br.com.a3rtecnologia.baixamobile.cadastro.CadastroActivity;
import br.com.a3rtecnologia.baixamobile.status.StatusBusiness;
import br.com.a3rtecnologia.baixamobile.tipo_documento.TipoDocumentoVolley;
import br.com.a3rtecnologia.baixamobile.tipo_ocorrencia.TipoOcorrenciaVolley;
import br.com.a3rtecnologia.baixamobile.tipo_recebedor.TipoRecebedor;
import br.com.a3rtecnologia.baixamobile.tipo_recebedor.TipoRecebedorVolley;
import br.com.a3rtecnologia.baixamobile.usuario.Usuario;
import br.com.a3rtecnologia.baixamobile.util.DelegateAsyncResponse;
import br.com.a3rtecnologia.baixamobile.util.InternetStatus;
import br.com.a3rtecnologia.baixamobile.util.PermissionUtil;
import br.com.a3rtecnologia.baixamobile.util.SessionManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * controle login
     */
    private boolean isRunning = false;

    /**
     * atributos UI
     */
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mRegisterView;
    private TextView mRecoveryView;
    private View mProgressView;
    private View mLoginFormView;

    private Context mContext;
    private PermissionUtil permissionUtil;
    private StatusBusiness statusBusiness;
    private EncomendaBusiness encomendaBusiness;
    private SessionManager sessionManager;


    private static int REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private static int REQUEST_CODE_ASK_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
    private static int REQUEST_CODE_ASK_PERMISSIONS_INTERNET = 3;
    private static int REQUEST_CODE_ASK_PERMISSIONS_ACCESS_NETWORK_STATE = 4;
    private static int REQUEST_CODE_ASK_PERMISSIONS_ACCESS_COARSE_LOCATION = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        statusBusiness = new StatusBusiness(mContext);
        encomendaBusiness = new EncomendaBusiness(mContext);
        sessionManager = new SessionManager(mContext);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        int hasACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED && hasACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS_ACCESS_FINE_LOCATION);

                init();

                return;
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS_ACCESS_FINE_LOCATION);

            init();

            return;
        }

        init();
    }



    private void init(){

        register();
        recovery();
        password();
        login();
    }



    /**
     * Login
     */
    private void login(){

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });
    }



    /**
     * Password
     */
    private void password(){

        mPasswordView = (EditText) findViewById(R.id.password);
    }



    /**
     * Formulario de login
     *
     * Cadastre-se
     * Esqueci minha senha
     */
    private void attemptLogin() {

        if (isRunning) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         * PASSWORD
         */
        if (TextUtils.isEmpty(password)) {

            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;

        }else if(!isPasswordValid(password)){

            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        /**
         * EMAIL
         */
        if (TextUtils.isEmpty(email)) {

            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;

        } else if (!isEmailValid(email)) {

            mEmailView.setError("Email com formato inválido");
            focusView = mEmailView;
            cancel = true;
        }



        if (cancel) {

            focusView.requestFocus();

        } else {

            showProgress(true);

            autenticationServer(email, password);
        }
    }



    /**
     * Regra de validacao do email
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return true;
    }



    /**
     * Regra de validacao de senha
     *
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
//        return password.length() > 4;
        return true;
    }



    /**
     * Autenticacao no servidor
     *
     * @param email
     * @param password
     */
    private void autenticationServer(String email, String password){

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);

        if(InternetStatus.isNetworkAvailable(mContext)){

            isRunning = true;
            new LoginVolley(mContext, usuario, new DelegateAsyncResponse() {

                @Override
                public void processFinish(boolean success) {

                    isRunning = false;
                    showProgress(false);

                    if (success) {

                        permissaoWRITE_EXTERNAL_STORAGE();

                    } else {

                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }

                @Override
                public void processCanceled(boolean cancel) {

                    isRunning = false;
                    showProgress(cancel);
                }
            });

        }else{

            isRunning = false;
            showProgress(false);
            StatusDialog dialog = new StatusDialog((Activity)mContext, "Login", "Sem conexão com internet", false);
        }
    }



    private void permissaoWRITE_EXTERNAL_STORAGE(){

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                result();

                return;
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            result();

            return;
        }

        result();
    }



    private void result(){

        Intent intent = new Intent(mContext, MenuDrawerActivity.class);
        startActivity(intent);

        sessionManager.setPrimeiroLogin("1");

        finish();
    }



    /**
     * Registrar usuario
     */
    private void register(){

        mRegisterView = (TextView) findViewById(R.id.login_register_id);
        mRegisterView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mRegisterView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }



    /**
     * Recuperar senha
     */
    private void recovery(){

        mRecoveryView = (TextView) findViewById(R.id.login_recovery_id);
        mRecoveryView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mRecoveryView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }



    /**
     * Exibir progress UI no form de Login
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {

                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {

                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

        } else {

            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

