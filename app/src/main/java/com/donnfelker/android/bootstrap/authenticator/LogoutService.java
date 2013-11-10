package com.donnfelker.android.bootstrap.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;

import com.donnfelker.android.bootstrap.core.Constants;
import com.donnfelker.android.bootstrap.util.Ln;
import com.donnfelker.android.bootstrap.util.SafeAsyncTask;

import javax.inject.Inject;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected final Context context;
    protected final AccountManager accountManager;

    @Inject
    public LogoutService(final Context context, final AccountManager accountManager) {
        this.context = context;
        this.accountManager = accountManager;
    }

    public void logout(final Runnable onSuccess) {
        new LogoutTask(context, onSuccess).execute();
    }

    private static class LogoutTask extends SafeAsyncTask<Boolean> {

        private final Context taskContext;
        private final Runnable onSuccess;

        protected LogoutTask(final Context context, final Runnable onSuccess) {
            this.taskContext = context;
            this.onSuccess = onSuccess;
        }

        @Override
        public Boolean call() throws Exception {

            final AccountManager accountManagerWithContext = AccountManager.get(taskContext);
            if (accountManagerWithContext != null) {
                final Account[] accounts = accountManagerWithContext
                        .getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
                if (accounts.length > 0) {
                    final AccountManagerFuture<Boolean> removeAccountFuture
                            = accountManagerWithContext.removeAccount(accounts[0], null, null);

                    return removeAccountFuture.getResult();
                }
            } else {
                //TODO what should be done here?
            }

            return false;
        }

        @Override
        protected void onSuccess(final Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);

            Ln.d("Logout succeeded: %s", accountWasRemoved);
            onSuccess.run();

        }

        @Override
        protected void onException(final Exception e) throws RuntimeException {
            super.onException(e);

            Ln.e(e.getCause(), "Logout failed.");
        }
    }
}
