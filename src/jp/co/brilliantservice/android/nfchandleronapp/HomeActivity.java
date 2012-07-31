/*
 * Copyright 2012 yamashita@brilliantservice.co.jp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.brilliantservice.android.nfchandleronapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

/**
 * @author yamashita@brilliantservice.co.jp
 */
public class HomeActivity extends Activity {

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "not found NFC feature", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "NFC feature is not available",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);
        IntentFilter[] intentFilter = new IntentFilter[] {
            new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
        };
        String[][] techList = new String[][] {
            {
                    android.nfc.tech.NfcA.class.getName(), android.nfc.tech.NfcB.class.getName(),
                    android.nfc.tech.IsoDep.class.getName(),
                    android.nfc.tech.MifareClassic.class.getName(),
                    android.nfc.tech.MifareUltralight.class.getName(),
                    android.nfc.tech.NdefFormatable.class.getName(),
                    android.nfc.tech.NfcV.class.getName(), android.nfc.tech.NfcF.class.getName(),
            }
        };
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techList);

    }

    @Override
    public void onPause() {
        super.onPause();

        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (TextUtils.isEmpty(action))
            return;

        if (!action.equals(NfcAdapter.ACTION_TAG_DISCOVERED))
            return;

        byte[] rawId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String id = Util.bytesToString(rawId);

        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

}
