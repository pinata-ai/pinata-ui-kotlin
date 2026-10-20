package com.pinata.ui.demojava;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.pinata.ui.PinataEnvironment;
import com.pinata.ui.PinataMarketplaceWalletView;

/**
 * Proves the Kotlin SDK is consumable from plain Java (java-support-mini-spike.md):
 * plain setters, enums and lambdas for the SAM listeners; no Kotlin types leak through.
 */
public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PinataMarketplaceWalletView wallet = new PinataMarketplaceWalletView(this);
    wallet.setEnvironment(PinataEnvironment.valueOf(BuildConfig.DEMO_ENV));
    wallet.setUserToken(BuildConfig.DEMO_TOKEN.isEmpty() ? null : BuildConfig.DEMO_TOKEN);
    wallet.setCompanyName("your-company");
    wallet.setOnMarketplaceOpenListener(event ->
        Toast.makeText(this, "open marketplace: source=" + event.getSource()
            + ", authenticated=" + event.getAuthenticated(), Toast.LENGTH_LONG).show());
    wallet.setOnErrorListener(message ->
        Toast.makeText(this, "pinata-error: " + message, Toast.LENGTH_LONG).show());
    wallet.load();
    setContentView(wallet);
  }
}
