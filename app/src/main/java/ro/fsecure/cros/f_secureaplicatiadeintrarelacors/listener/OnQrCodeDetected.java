package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.listener;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model.User;

/**
 * Created by humin on 6/13/2017.
 */

public interface OnQrCodeDetected {
    void onCodeDetected(User user);
    void onUserSend(User user);
    void onClickTermsAndContitions();
}
